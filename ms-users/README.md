# Microservicio `users` - Paper Tales

Microservicio de autenticación para **React_RelatosPapel**. Implementa login con patrón **Phantom Token**:

- el cliente recibe un **token opaco**
- `api-gateway` intercambia ese token por un **JWT interno**
- `ms-catalogue` y `ms-orders` validan el JWT localmente

## Endpoints

### `POST /api/v1/auth/login`

Body:

```json
{
  "email": "lector@unir.com",
  "password": "password123"
}
```

Respuesta:

```json
{
  "accessToken": "rp_opaque_...",
  "tokenType": "Bearer",
  "expiresIn": 300
}
```

### `POST /api/v1/auth/logout`

Requiere:

```http
Authorization: Bearer <opaqueToken>
```

### `POST /api/v1/auth/register`

Body:

```json
{
  "email": "nuevo@unir.com",
  "password": "password123",
  "role": "ROLE_LECTOR",
  "enabled": true
}
```

Notas:

- `role` es opcional (si no se envía, se usa `ROLE_LECTOR`)
- roles válidos: `ROLE_LECTOR`, `ROLE_ADMIN`

Respuesta `201 Created`:

```json
{
  "id": 3,
  "email": "nuevo@unir.com",
  "role": "ROLE_LECTOR",
  "enabled": true
}
```

## Usuarios de prueba

- `lector@unir.com` / `password123`
- `admin@unir.com` / `admin123`

## Dependencias operativas

- MySQL en `localhost:3308`
- Redis en `localhost:6379`
- Eureka en `http://localhost:8761/eureka`

## Ejecución rápida

```zsh
cd "/Users/gunnar/Documents/Desarrollo/React_RelatosPapel/ms-users"
mvn spring-boot:run
```
