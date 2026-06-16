# back-end-papertales-orders

Microservicio de órdenes de la aplicación **UNIR Paper Tales**.

## Tabla de contenidos

- [Arquitectura general](#arquitectura-general)
- [Capa controladora (Controller)](#capa-controladora-controller)
- [Capa de servicio (Service)](#capa-de-servicio-service)
- [Capa de acceso a datos (Repository)](#capa-de-acceso-a-datos-repository)
- [Modelo relacional de base de datos](#modelo-relacional-de-base-de-datos)
- [Reconstrucción de la base de datos para pruebas](#reconstrucción-de-la-base-de-datos-para-pruebas)
- [Configuración](#configuración)

---

## Arquitectura general

El microservicio sigue una arquitectura en capas clásica de Spring Boot:

```
Controller → Service → Repository → Base de datos MySQL
```

Se registra en **Eureka** como `orders` y utiliza **Spring Data JPA** con **Hibernate** para el acceso a datos. La validación del esquema se realiza con `ddl-auto: validate`.

La validación de libros (stock y visibilidad) se realiza por HTTP contra el microservicio **catalogue** usando el nombre relativo de Eureka.

---

## Capa controladora (Controller)

### `OrdersController` — `/api/v1`

| Método   | Endpoint                     | Descripción                                  | Request Body           | Response              |
|----------|------------------------------|----------------------------------------------|------------------------|-----------------------|
| `GET`    | `/api/v1/orders`             | Lista todas las órdenes (includeItems=true para incluir items) | —                      | `GetOrdersResponseDto`|
| `GET`    | `/api/v1/orders/user/{userId}` | Lista órdenes de un usuario                  | —                      | `GetOrdersResponseDto`|
| `GET`    | `/api/v1/orders/{orderId}`   | Detalle de una orden (incluye items)         | —                      | `OrderDto`            |
| `POST`   | `/api/v1/orders`             | Crea una nueva orden                         | `WriteOrderRequestDto` | `OrderDto`            |
| `PUT`    | `/api/v1/orders/{orderId}`   | Reemplaza completamente una orden            | `WriteOrderRequestDto` | `OrderDto`            |
| `PATCH`  | `/api/v1/orders/{orderId}`   | Actualización parcial (JSON Merge Patch)     | JSON parcial           | `OrderDto`            |
| `DELETE` | `/api/v1/orders/{orderId}`   | Elimina una orden                            | —                      | `204 No Content`      |

### `UsersController` — `/api/v1`

| Método   | Endpoint                     | Descripción                                  | Request Body           | Response              |
|----------|------------------------------|----------------------------------------------|------------------------|-----------------------|
| `GET`    | `/api/v1/users`              | Lista todos los usuarios                     | —                      | `GetUsersResponseDto` |
| `GET`    | `/api/v1/users/{userId}`     | Obtiene un usuario por ID                    | —                      | `UserDto`             |
| `POST`   | `/api/v1/users`              | Crea un usuario                              | `WriteUserRequestDto`  | `UserDto`             |
| `PUT`    | `/api/v1/users/{userId}`     | Reemplaza completamente un usuario           | `WriteUserRequestDto`  | `UserDto`             |
| `PATCH`  | `/api/v1/users/{userId}`     | Actualización parcial (JSON Merge Patch)     | JSON parcial           | `UserDto`             |
| `DELETE` | `/api/v1/users/{userId}`     | Elimina un usuario                           | —                      | `204 No Content`      |

### Manejo de errores — `OrdersControllerAdvice`

- `OrderNotFoundException` → **404 Not Found**
- `IllegalArgumentException` → **400 Bad Request**

```json
{
  "details": "Order not found with id: PED-1001"
}
```

### DTOs

| DTO                   | Uso                                                                 |
|-----------------------|---------------------------------------------------------------------|
| `GetOrdersResponseDto`| Lista de órdenes (vista resumida sin items)                         |
| `OrderDto`            | Detalle de una orden con items y datos básicos del usuario          |
| `OrderItemDto`        | Item de una orden (bookId, bookTitle, price, quantity)              |
| `WriteOrderRequestDto`| Cuerpo de creación/actualización (userId, orderDate, status, items) |
| `WriteOrderItemDto`   | Item solicitado (bookId, price, quantity)                           |
| `GetUsersResponseDto` | Lista de usuarios                                                   |
| `UserDto`             | Vista de usuario (id, name, email, avatar)                          |
| `WriteUserRequestDto` | Cuerpo de creación/actualización de usuario                         |
| `ErrorResponse`       | Respuesta de error genérica                                         |

---

## Capa de servicio (Service)

### `GetOrdersService`

- `getOrders()` y `getOrdersByUser(userId)` devuelven vistas resumidas de órdenes.
- `getOrdersWithItems()` devuelve todas las órdenes con sus items y títulos de libros.
- `getOrder(orderId)` obtiene una orden y enriquece los items con el título del libro vía **catalogue**.

### `CreateOrderService`

- Verifica existencia del usuario.
- Valida cada item contra **catalogue** (libro visible y stock suficiente).
- Descuenta el stock en **catalogue** al crear la orden.
- Calcula el total, crea la orden y persiste los items.

### `ModifyOrderService`

- Actualiza la orden existente y reemplaza completamente sus items.
- Revalida stock y visibilidad de libros en **catalogue**.
- Soporta PATCH parcial con JSON Merge Patch; si se incluyen items, también se revalidan.

### `ModifyUserService`

- Aplica JSON Merge Patch para actualizar parcialmente un usuario.
- Reemplazo completo (PUT) con validación de campos requeridos.

### `CreateUserService` / `GetUsersService` / `DeleteUserService`

- Creación, consulta y eliminación de usuarios.

### `DeleteOrderService`

- Elimina la orden y sus items asociados.

### `CatalogueBookService`

- Cliente HTTP hacia **catalogue** usando `http://catalogue/...` (Eureka).
- Reutilizado para validación y para obtener títulos de libros.

---

## Capa de acceso a datos (Repository)

### Repositorios JPA

| Repositorio              | Entidad      | Funcionalidad destacada                                 |
|--------------------------|--------------|---------------------------------------------------------|
| `OrderJpaRepository`     | `Order`      | `findByUserId`, `findByStatus`, `findByUserIdAndStatus` |
| `OrderItemJpaRepository` | `OrderItem`  | `findByOrderId`, `deleteByOrderId`                      |
| `UserJpaRepository`      | `User`       | `findByEmail`, `existsByEmail`                          |

---

## Modelo relacional de base de datos

```
┌──────────────────────────┐
│          users           │
├──────────────────────────┤
│ id           BIGINT (PK) │──────┐
│ name         VARCHAR(255)│      │
│ email        VARCHAR(255)│      │
│ password     VARCHAR(255)│      │
│ avatar       VARCHAR(500)│      │
│ created_at   TIMESTAMP   │      │
│ updated_at   TIMESTAMP   │      │
└──────────────────────────┘      │
                                  │ 1:N
                                  ▼
┌──────────────────────────┐
│          orders          │
├──────────────────────────┤
│ id          VARCHAR(20)  │
│ user_id     BIGINT (FK)  │
│ order_date  DATE         │
│ status      VARCHAR(50)  │
│ total       DECIMAL(10,2)│
│ created_at  TIMESTAMP    │
│ updated_at  TIMESTAMP    │
└──────────────────────────┘
              │ 1:N
              ▼
┌──────────────────────────┐
│       order_items        │
├──────────────────────────┤
│ id          BIGINT (PK)  │
│ order_id    VARCHAR(20)  │
│ book_id     BIGINT       │
│ price       DECIMAL(10,2)│
│ quantity    INT          │
│ created_at  TIMESTAMP    │
└──────────────────────────┘
```

### Relaciones

- **`users` → `orders`**: Relación **1:N** (`orders.user_id`).
- **`orders` → `order_items`**: Relación **1:N** (`order_items.order_id`).
- **`order_items`**: Clave única `(order_id, book_id)`.

---

## Reconstrucción de la base de datos para pruebas

### Inicialización automática

La aplicación ejecuta automáticamente los scripts en `src/main/resources/db/` al arrancar:

- `schema.sql` (elimina y recrea el schema `db_ordenes` y sus tablas)
- `indices.sql` (crea índices)
- `ejemplos.sql` (inserta datos de ejemplo)

Esto significa que **cada arranque borra los datos y repuebla** la base de datos de órdenes.

---

## Configuración

Variables de entorno configurables (`application.yml`):

| Variable      | Valor por defecto                      | Descripción                  |
|---------------|-----------------------------------------|------------------------------|
| `DB_URL`      | `jdbc:mysql://localhost:3307/db_ordenes`| URL JDBC de órdenes          |
| `DB_DRIVER`   | `com.mysql.cj.jdbc.Driver`              | Driver JDBC                  |
| `DB_USER`     | `root`                                  | Usuario de base de datos     |
| `DB_PASSWORD` | `root_password`                         | Contraseña de base de datos  |
| `EUREKA_URL`  | `http://localhost:8761/eureka`          | URL del servidor Eureka      |

El servicio se registra en Eureka con el nombre de instancia `unir-papertales-orders` y el serviceId `orders`.
La validación de stock y visibilidad de libros se realiza por HTTP contra `catalogue` usando el nombre relativo de Eureka.
