# back-end-cloud-gateway

**API Gateway / Reverse Proxy** for the **UNIR Books** microservices ecosystem, built with **Spring Cloud Gateway (WebFlux)**. It acts as the single entry point for all client requests, automatically routing them to the appropriate microservice via **Eureka Service Discovery**.

[Official Documentation](https://cloud.spring.io/spring-cloud-gateway/reference/html/) · [More information](https://www.baeldung.com/spring-cloud-gateway)

---

## How it works

The gateway registers itself as an Eureka client (`@EnableDiscoveryClient`) and uses the **Discovery Locator** to automatically generate routes for every service registered in Eureka. No manual route definitions are needed — if a microservice registers as `Books-catalogue`, it becomes accessible through the gateway at `/Books-catalogue/**`.

```
                        ┌───────────────────────┐
   Client requests ───► │   Cloud Gateway       │  ← This project (port 8762)
                        │   (Reverse Proxy)     │
                        └───────┬───────────────┘
                                │  routes via Eureka
                 ┌──────────────┼──────────────┐
                 ▼              ▼              ▼
          /Books-    /Books-      /other-service/**
          catalogue/**  orders/**
```

### Key features

- **Automatic route discovery**: Routes are generated dynamically from the Eureka registry (`discovery.locator.enabled: true`).
- **Lowercase service IDs**: Service names are normalized to lowercase (`lower-case-service-id: true`).
- **Global CORS configuration**: All routes allow configurable origins, all headers, and `GET`, `POST`, `PUT`, `DELETE`, `PATCH` methods.
- **Duplicate header deduction**: The `DedupeResponseHeader` filter prevents duplicate `Access-Control-Allow-Credentials` and `Access-Control-Allow-Origin` headers.
- **Actuator route tables**: The gateway exposes its auto-discovered routes via Spring Boot Actuator.
- **Profile support**: The active Spring profile can be set via the `PROFILE` environment variable.

---

## Configuration

### Routing & Discovery (`application.yml`)

| Property | Value | Description |
|----------|-------|-------------|
| `spring.cloud.gateway.server.webflux.discovery.locator.enabled` | `true` | Enables automatic route creation from Eureka registry |
| `spring.cloud.gateway.server.webflux.discovery.locator.lower-case-service-id` | `true` | Normalizes service IDs to lowercase in route paths |

### CORS

| Property | Default | Description |
|----------|---------|-------------|
| `globalcors.cors-configurations.[/**].allowedOrigins` | `*` | Allowed origins. Configurable via `ALLOWED_ORIGINS` env var (e.g., `http://localhost:3000`) |
| `allowedHeaders` | `*` | All headers allowed |
| `allowedMethods` | `GET, POST, PUT, DELETE, PATCH` | Allowed HTTP methods |

### Default Filters

| Filter | Description |
|--------|-------------|
| `DedupeResponseHeader` | Removes duplicate `Access-Control-Allow-Credentials` and `Access-Control-Allow-Origin` headers |

### Actuator

The gateway exposes all actuator endpoints, including the route table:

```
GET <gateway-host>:<port>/actuator/gateway/routes
```

Controlled by the `ROUTE_TABLES_ENABLED` env var (default: `read_only`).

### Environment variables

| Variable | Default | Description |
|----------|---------|-------------|
| `PORT` | `8762` | Gateway listening port |
| `HOSTNAME` | `localhost` | Instance hostname for Eureka registration |
| `EUREKA_URL` | `http://localhost:8761/eureka` | Eureka server URL |
| `ALLOWED_ORIGINS` | `*` | CORS allowed origins (comma-separated for multiple) |
| `ROUTE_TABLES_ENABLED` | `read_only` | Actuator gateway endpoint access level |
| `PROFILE` | `default` | Spring active profile |

---

## Build & Run

### Compile and package

```bash
mvn clean package
```

### Run locally

```bash
java -jar target/gateway-0.0.1-SNAPSHOT.jar
```

The gateway will start on [http://localhost:8762](http://localhost:8762) and register with Eureka. Routes will be available at `http://localhost:8762/<service-name>/**`.

### Docker

Multi-stage Dockerfile included:

```bash
docker build -t cloud-gateway .
docker run -p 8762:8762 -e EUREKA_URL=http://eureka-host:8761/eureka cloud-gateway
```

---

## Deploy on Railway

Deploy this gateway standalone:

[![Deploy on Railway](https://railway.app/button.svg)](https://railway.app/template/OI2sbM?referralCode=jesus-unir)

Deploy the full Spring microservices ecosystem:

[![Deploy on Railway](https://railway.app/button.svg)](https://railway.app/template/f6CKpT?referralCode=jesus-unir)

---
