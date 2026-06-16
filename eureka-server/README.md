# back-end-eureka

**Eureka Server** for the **UNIR Books** microservices ecosystem. Acts as the **Service Discovery** registry, allowing all microservices to register themselves and discover each other without hardcoded URLs.

[Official Documentation](https://cloud.spring.io/spring-cloud-netflix/reference/html/) · [More information](https://www.baeldung.com/spring-cloud-netflix-eureka)

---

## How it works

The Eureka Server is annotated with `@EnableEurekaServer` and configured as a **standalone registry** (it does not register with itself nor fetch the registry from other peers). All client microservices (e.g., `Books-catalogue`, `Books-orders`) register here and use the registry to resolve service names to actual network locations.

```
┌─────────────────────┐
│    Eureka Server    │  ← This project (port 8761)
│  (Service Registry) │
└────────┬────────────┘
         │  registers / discovers
    ┌────┴─────┬──────────────┐
    ▼          ▼              ▼
catalogue   orders    ... other services
```

---

## Configuration

Key properties in `application.yml`:

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8761` | HTTP port for the Eureka dashboard and registry API. Configurable via `PORT` env var. |
| `eureka.client.registerWithEureka` | `false` | Prevents the server from registering itself as a client. |
| `eureka.client.fetchRegistry` | `false` | Prevents the server from fetching the registry (standalone mode). |
| `eureka.instance.hostname` | `localhost` | Hostname advertised to clients. Configurable via `EUREKA_HOST` env var. |
| `eureka.server.renewal-percent-threshold` | `0.90` | Threshold for Eureka self-preservation mode. If the percentage of renewed leases drops below this value, Eureka stops evicting instances. |

### Environment variables

| Variable | Default | Description |
|----------|---------|-------------|
| `PORT` | `8761` | Server listening port |
| `EUREKA_HOST` | `localhost` | Eureka instance hostname (set to the public hostname in production) |

---

## Build & Run

### Compile and package

```bash
mvn clean package
```

### Run locally

```bash
java -jar target/eureka-0.0.1-SNAPSHOT.jar
```

The Eureka dashboard will be available at [http://localhost:8761](http://localhost:8761).

### Docker

The project includes a **multi-stage Dockerfile**:

1. **Build stage**: Uses `maven:3.9.12-eclipse-temurin-25` to compile and package the project.
2. **Runtime stage**: Uses `eclipse-temurin:25-jre` for a lightweight production image.

```bash
docker build -t eureka-server .
docker run -p 8761:8761 eureka-server
```

---

## Deploy on Railway

Deploy this Eureka server standalone:

[![Deploy on Railway](https://railway.app/button.svg)](https://railway.app/template/HM8cFB?referralCode=jesus-unir)

Deploy the full Spring microservices ecosystem:

[![Deploy on Railway](https://railway.app/button.svg)](https://railway.app/template/f6CKpT?referralCode=jesus-unir)

---

## New Relic Integration (optional)

> ⚠️ **This is entirely optional.** The application runs perfectly without it.

The project includes a **New Relic APM** integration for production performance monitoring. Three pieces in the `pom.xml` work together:

### 1. `newrelic-java` dependency (zip type)

```xml
<dependency>
    <groupId>com.newrelic.agent.java</groupId>
    <artifactId>newrelic-java</artifactId>
    <version>9.2.0</version>
    <scope>provided</scope>
    <type>zip</type>
</dependency>
```

Downloads the New Relic agent as a ZIP during dependency resolution. The `provided` scope prevents it from being packaged inside the fat JAR.

### 2. `maven-dependency-plugin` — agent unpack

```xml
<plugin>
    <artifactId>maven-dependency-plugin</artifactId>
    <executions>
        <execution>
            <id>unpack-newrelic</id>
            <phase>package</phase>
            <goals><goal>unpack-dependencies</goal></goals>
            <configuration>
                <includeGroupIds>com.newrelic.agent.java</includeGroupIds>
                <excludes>**/newrelic.yml</excludes>
                <outputDirectory>${project.build.directory}</outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Extracts the ZIP into `target/`, creating the `target/newrelic/` folder with `newrelic.jar` and other agent files. **Excludes `newrelic.yml`** so it doesn't overwrite the repository template.

### 3. `maven-resources-plugin` — filtered `newrelic.yml` copy

```xml
<plugin>
    <artifactId>maven-resources-plugin</artifactId>
    <executions>
        <execution>
            <id>copy-newrelic-yml</id>
            <phase>package</phase>
            <goals><goal>copy-resources</goal></goals>
            <configuration>
                <outputDirectory>${project.build.directory}/newrelic</outputDirectory>
                <resources>
                    <resource>
                        <directory>src/main/resources/newrelic</directory>
                        <filtering>true</filtering>
                    </resource>
                </resources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Copies `src/main/resources/newrelic/newrelic.yml` to `target/newrelic/`, applying **Maven filtering** to replace `${env.NEW_RELIC_LICENSE_KEY}` with the actual environment variable value. This way the license key is **never committed** to the repository.

### Usage

```bash
# 1. Export your New Relic ingest license key
export NEW_RELIC_LICENSE_KEY="eu01f......"

# 2. Build (downloads, unpacks the agent and copies the yml with the key)
mvn clean package

# 3. Start with the New Relic agent
java -javaagent:target/newrelic/newrelic.jar -jar target/eureka-0.0.1-SNAPSHOT.jar
```

The ingest license key can be obtained from your New Relic account under **API Keys**.
