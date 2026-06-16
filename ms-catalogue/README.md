# Microservicio de Catálogo - UNIR Books

Microservicio de catálogo de la aplicación **UNIR Books**. Gestiona el CRUD completo del catálogo de libros, incluyendo sus detalles técnicos e imágenes asociadas.

## Tabla de contenidos

- [Arquitectura general](#arquitectura-general)
- [Capa controladora (Controller)](#capa-controladora)
- [Capa de servicio (Service)](#capa-de-servicio)
- [Capa de acceso a datos (Repository)](#capa-de-acceso-a-datos)
- [Modelo relacional de base de datos](#modelo-relacional-de-base-de-datos)
- [Reconstrucción de la base de datos para pruebas](#reconstrucción-de-la-base-de-datos-para-pruebas)
- [Configuración](#configuración)

---

## Arquitectura general

El microservicio sigue una arquitectura en capas clásica de Spring Boot:

```
Controller → Service → Repository → Base de datos MySQL
```

Se registra en **Eureka** como `catalogue` y utiliza **Spring Data JPA** con **Hibernate** para el acceso a datos. La validación del esquema se realiza con `ddl-auto: validate`.

---

## Capa controladora

Existen **tres controladores REST**, cada uno expuesto bajo una versión diferente de la API. Todos permiten CORS desde cualquier origen.

### `BooksController` — `/api/v1/`

Controlador principal con operaciones CRUD completas.

| Método   | Endpoint                    | Descripción                                              | Request Body             | Response                  |
|----------|-----------------------------|----------------------------------------------------------|--------------------------|---------------------------|
| `GET`    | `/api/v1/books`          | Lista todos los libros con stock > 0                | —                        | `GetBooksResponseDto`  |
| `GET`    | `/api/v1/books/{id}`     | Obtiene un libro por ID (con detalles e imágenes)      | —                        | `GetBookResponseDto`    |
| `POST`   | `/api/v1/books`          | Crea un nuevo libro                                 | `WriteBookRequestDto`  | `GetBookResponseDto`    |
| `PUT`    | `/api/v1/books/{id}`     | Reemplaza completamente un libro                    | `WriteBookRequestDto`  | `GetBookResponseDto`    |
| `PATCH`  | `/api/v1/books/{id}`     | Actualización parcial vía **JSON Merge Patch** (RFC 7386)| JSON parcial (String)    | `GetBookResponseDto`    |
| `DELETE` | `/api/v1/books/{id}`     | Elimina un libro                                    | —                        | `204 No Content`          |

### `BooksControllerGetWithPredicate` — `/api/v2/`

Búsqueda con filtros dinámicos usando **JPA Specifications**.

| Método | Endpoint            | Parámetros opcionales                                              |
|--------|---------------------|--------------------------------------------------------------------|
| `GET`  | `/api/v2/books`  | `title`, `author`, `code`, `description`, `category`, `publication_date`, `price`, `rating`, `visible`, `stock`, `image` |

- Los campos de texto (`title`, `author`, `code`, `description`, `category`, `image`) filtran con **LIKE** (coincidencia parcial, case-insensitive).
- `publication_date` filtra con **igualdad exacta** (YYYY-MM-DD).
- `price` filtra con **≤** (menor o igual).
- `rating` filtra con **≤** (menor o igual).
- `visible` filtra con **igualdad exacta**.
- `stock` filtra con **≥** (mayor o igual).

### `BooksControllerGetWithPredicateAndPagination` — `/api/v3/`

Igual que v2 pero con **paginación** y admite `id` opcional.

| Método | Endpoint            | Parámetros opcionales                                                                                 |
|--------|---------------------|--------------------------------------------------------------------------------------------------------|
| `GET`  | `/api/v3/books`  | `id`, `title`, `author`, `code`, `description`, `category`, `publication_date`, `price`, `rating`, `visible`, `stock`, `image`, `pageSize`, `page` |

### Manejo de errores — `BooksControllerAdvice`

El `@ControllerAdvice` captura `BookNotFoundException` y devuelve un **404 Not Found** con un cuerpo `ErrorResponse`:

```json
{
  "details": "Book not found with id: 42"
}
```

### DTOs

| DTO                       | Uso                                                        |
|---------------------------|------------------------------------------------------------|
| `GetBooksResponseDto`  | Lista de libros (vista resumida sin detalles ni imágenes) |
| `GetBookResponseDto`    | Detalle completo de un libro (con detalles e imágenes)    |
| `BookDto`               | Vista resumida de un libro (id, title, description, author, price, stock) |
| `WriteBookRequestDto`   | Cuerpo de creación/actualización (incluye detalles e imágenes) |
| `DetailDto`        | Par clave-valor de un detalle del libro (`detailKey`, `detailValue`) |
| `ErrorResponse`           | Respuesta de error genérica                                 |

---

## Capa de servicio

Cada operación de negocio está separada en su propio servicio:

### `GetBooksService`

- `getBooks()`: Obtiene todos los libros con stock > 0 usando `findAvailableBooks()` (JPQL).
- `getBook(Integer id)`: Obtiene un libro por ID con detalle completo (detalles e imágenes). Lanza `BookNotFoundException` si no existe.

### `GetBooksWithPredicateService`

- `getBooks(title, description, fullDescription, author, price, stock)`: Si se proporciona al menos un filtro, construye una `Specification` dinámica; si no, devuelve todos los disponibles.

### `GetBooksWithPredicateAndPaginationService`

- Igual que el anterior pero delega en `BookRepository.getBooks(...)` con parámetros `pageSize` y `page` para paginación.

### `CreateBooksService`

- `createBook(WriteBookRequestDto)`: Crea la entidad `Book` junto con sus `BookDetail` e `BookImage` asociadas. Gracias a `CascadeType.ALL`, las entidades hijas se persisten automáticamente. Operación `@Transactional`.

### `ModifyBooksService`

- `modifyBook(Integer id, WriteBookRequestDto)`: **PUT** — Reemplaza completamente el libro. Elimina los detalles e imágenes antiguas y crea las nuevas.
- `modifyBook(Integer id, String jsonPart)`: **PATCH** — Aplica un **JSON Merge Patch** (RFC 7386) usando la librería `json-patch`. Convierte el libro existente a JSON, aplica el merge patch y persiste el resultado.

### `DeleteBooksService`

- `deleteBook(int id)`: Verifica existencia y elimina. Los detalles e imágenes se eliminan por cascada (`ON DELETE CASCADE`). Lanza `BookNotFoundException` si no existe.

### `BookMapper` (Utilidad)

Componente de mapeo entre entidades JPA y DTOs. Gestiona también la eliminación y re-creación de detalles e imágenes al actualizar un libro (borra los antiguos con `deleteByBookId` y crea los nuevos).

---

## Capa de acceso a datos

### Repositorios JPA

| Repositorio                  | Entidad              | Hereda de                                                            | Funcionalidad destacada                                |
|------------------------------|----------------------|----------------------------------------------------------------------|--------------------------------------------------------|
| `BookJpaRepository`        | `Book`             | `JpaRepository`, `JpaSpecificationExecutor`, `PagingAndSortingRepository` | Consultas JPQL, nativas, derivadas y Specifications    |
| `DetailJpaRepository` | `BookDetail`| `JpaRepository`                                                      | `findByBookId`, `deleteByBookId` (query nativa)    |
| `ImageJpaRepository`         | `BookImage`        | `JpaRepository`                                                      | `findByBookId`, `deleteByBookId` (query nativa)    |

### `BookRepository` (Repositorio compuesto)

Clase `@Repository` que encapsula la lógica de construcción de `Specification` dinámicas y paginación. Métodos principales:

- `getBooks()` — Devuelve libros disponibles (stock > 0).
- `getBooks(size, page)` — Paginación simple sin filtros.
- `getBooks(title, description, ..., stock)` — Búsqueda con filtros dinámicos.
- `getBooks(title, description, ..., stock, pageSize, page)` — Búsqueda con filtros + paginación.

### Consultas disponibles en `BookJpaRepository`

| Método                          | Tipo        | Descripción                                        |
|---------------------------------|-------------|----------------------------------------------------|
| `findAvailableBooks()`       | JPQL        | Libros con stock > 0                          |
| `findAvailableBooksNative()` | SQL nativa  | Equivalente nativa de la anterior                  |
| `findAllWithDetails()`          | JPQL        | Todos los libros con JOIN FETCH de detalles e imágenes |
| `findAllWithDetailsNative()`    | SQL nativa  | Equivalente nativa con LEFT JOIN                   |
| `findByAuthorIgnoreCase(author)`    | Derivada    | Filtro por autor (case-insensitive)                 |
| `findByTitleContainingIgnoreCase(title)` | Derivada | Búsqueda parcial por título                  |

### Sistema de predicados dinámicos (JPA Specifications)

El paquete `repository.predicate` implementa un motor de consultas dinámicas:

- **`SearchCriteria<T>`**: Implementa `Specification<T>`, acumula `SearchStatement` y genera predicados JPA en `toPredicate()`.
- **`SearchStatement`**: Tripleta `(key, value, operation)`.
- **`SearchOperation`**: Enum con operaciones: `GREATER_THAN`, `LESS_THAN`, `GREATER_THAN_EQUAL`, `LESS_THAN_EQUAL`, `NOT_EQUAL`, `EQUAL`, `MATCH` (LIKE), `MATCH_END`.
- **`SearchFields`**: Constantes con los nombres de campos de la entidad `Book`.

---

## Modelo relacional de base de datos

El esquema del catálogo contiene **una sola tabla**: `books`.

```
┌──────────────────────────┐
│          books           │
├──────────────────────────┤
│ id               BIGINT (PK) │
│ title            VARCHAR(255)│
│ author           VARCHAR(255)│
│ code             VARCHAR(20) │
│ publication_date DATE        │
│ category         VARCHAR(100)│
│ price            DECIMAL(10,2)│
│ rating           INTEGER     │
│ visible          BOOLEAN     │
│ stock            INT         │
│ image_url        VARCHAR(500)│
│ description      VARCHAR(500)│
└──────────────────────────┘
```

---

## Reconstrucción de la base de datos para pruebas

### Inicialización automática

La aplicación ejecuta automáticamente los scripts en `src/main/resources/db/` al arrancar:

- `schema.sql` (crea el schema y la tabla `books`)
- `indices.sql` (crea índices)
- `ejemplos.sql` (inserta datos de ejemplo y hace `TRUNCATE` previo)

`schema.sql` elimina y recrea el schema `papertales_catalogue`, por lo que **cada arranque borra y repuebla** la base de datos de catálogo.

---

## Configuración

Variables de entorno configurables (`application.yml`):

| Variable      | Valor por defecto                              | Descripción                  |
|---|---|---|
| `DB_URL`      | `jdbc:mysql://localhost:3306/papertales_catalogue`| URL de conexión JDBC         |
| `DB_DRIVER`   | `com.mysql.cj.jdbc.Driver`                     | Driver JDBC                  |
| `DB_USER`     | `root`                                         | Usuario de base de datos     |
| `DB_PASSWORD` | `root_password`                                        | Contraseña de base de datos  |
| `EUREKA_URL`  | `http://localhost:8761/eureka`                 | URL del servidor Eureka      |

El servicio se registra en Eureka con el nombre de instancia `catalogue`.
