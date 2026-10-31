# ms-denuncias

Microservicio de **siniestros** de SiniestroFácil: registra la denuncia del asegurado, genera el folio de recepción, guarda las referencias de las fotos y ejecuta el procesamiento posterior (validar póliza y asignar taller).

- Puerto: `8083`
- Base de datos: `db_denuncias` (MySQL)
- Depende de `ms-polizas` (8082) y `ms-talleres` (8084) **solo** para el procesamiento, nunca para registrar la denuncia

## Modelo

| Entidad | Tabla | Relación |
|---|---|---|
| `Denuncia` | `denuncias` | `@OneToMany(cascade = ALL, orphanRemoval = true)` → fotos |
| `Foto` | `fotos` | `@ManyToOne` → denuncia |

Estados: `RECIBIDA`, `RECHAZADA`, `PENDIENTE_TALLER`, `TALLER_ASIGNADO`, `VEHICULO_RECIBIDO`, `CERRADA`.
Folio: `SF-AAAAMMDD-XXXXXXXX`.

## Flujo asíncrono

```
POST /api/v1/denuncias ──► guarda (RECIBIDA) ──► devuelve FOLIO ──► publica evento en la cola
                                                                          │
POST /api/v1/procesador/denuncias/{id}  (en AWS: Lambda que lee SQS) ◄────┘
   ├─ GET ms-polizas  /api/v1/polizas/numero/{n}/vigencia
   │     ├─ no existe / no vigente ──► RECHAZADA
   │     └─ ms-polizas caído ──────────► 503, la denuncia sigue RECIBIDA
   └─ POST ms-talleres /api/v1/asignaciones
         ├─ 201 ──────────────────────► TALLER_ASIGNADO (guarda tallerId y asignacionId)
         └─ 409 o ms-talleres caído ──► PENDIENTE_TALLER (se reintenta después)
```

Los clientes HTTP (`PolizasClient`, `TalleresClient`) usan `RestClient` con timeout de conexión de 2 s y de lectura de 3 s.

## Endpoints

| Método | Ruta | Respuestas |
|---|---|---|
| POST | `/api/v1/denuncias` | 201 (con folio), 400 |
| GET | `/api/v1/denuncias?rut=&estado=` | 200, 400 |
| GET | `/api/v1/denuncias/{id}` | 200, 404 |
| GET | `/api/v1/denuncias/folio/{folio}` | 200, 404 |
| PUT | `/api/v1/denuncias/{id}` | 200, 400, 404, 409 (ya tomada por taller) |
| PATCH | `/api/v1/denuncias/{id}/estado` | 200, 400, 404, 409 |
| DELETE | `/api/v1/denuncias/{id}` | 204, 404, 409 |
| POST | `/api/v1/denuncias/{id}/fotos` | 201, 400, 404 |
| DELETE | `/api/v1/denuncias/{id}/fotos/{fotoId}` | 204, 404 |
| POST | `/api/v1/procesador/denuncias/{id}` | 200, 404, 409, 503 |
| POST | `/api/v1/procesador/pendientes` | 200, 503 |

## Configuración

| Variable | Valor por defecto |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/db_denuncias?createDatabaseIfNotExist=true...` |
| `DB_USERNAME` / `DB_PASSWORD` | `root` / `root` |
| `POLIZAS_URL` | `http://localhost:8082` |
| `TALLERES_URL` | `http://localhost:8084` |

## Ejecutar

```bash
./mvnw clean install
java -jar target/ms-denuncias.jar
```

Swagger UI: http://localhost:8083/swagger-ui.html
