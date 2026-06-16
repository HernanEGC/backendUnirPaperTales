# Postman - RelatosPapel Act3

Archivos incluidos:

- `RelatosPapel_Act3.postman_collection.json`
- `RelatosPapel_Local.postman_environment.json`

## Importar en Postman

1. Abrir Postman.
2. `Import` -> seleccionar ambos archivos JSON.
3. Activar el environment `RelatosPapel Local`.

## Orden sugerido de ejecucion

1. `Login lector (guardar opaqueToken)`
2. `GET catalogo con token lector (200)`
3. `POST catalogo con lector (403)`
4. `Login admin (guardar adminOpaqueToken)`
5. `POST catalogo con admin (201/200)`
6. `GET ordenes con token lector (200)`
7. `Logout con token lector (204)`
8. `Reusar token tras logout (401)`

## Nota

Esta coleccion asume que el API Gateway esta en `http://localhost:8080` y que todos los microservicios ya estan levantados y registrados en Eureka.

