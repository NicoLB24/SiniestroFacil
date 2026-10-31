# ms-talleres

Microservicio de la **red de talleres** de SiniestroFácil: talleres en convenio, asignación de denuncias a un taller con capacidad disponible y confirmación de recepción del vehículo.

- Puerto: `8084`
- Base de datos: `db_talleres` (MySQL)

## Modelo

| Entidad | Tabla | Relación |
|---|---|---|
| `Taller` | `talleres` | `@OneToMany` → asignaciones |
| `Asignacion` | `asignaciones` | `@ManyToOne` → taller |

`Asignacion.denunciaId` referencia a una denuncia de `ms-denuncias` sin llave foránea, porque cada servicio tiene su propia base.

Estados de una asignación: `ASIGNADA → RECIBIDO → FINALIZADA` (o `CANCELADA` antes de recibir). Un taller está **disponible** si está activo y sus casos `ASIGNADA` + `RECIBIDO` son menos que su `capacidadMaxima`.

**Regla de asignación:** taller activo con capacidad libre, prefiriendo la misma comuna del siniestro y luego el de menor carga. Si no hay ninguno responde `409` y la denuncia queda pendiente para reintento.

## Endpoints

| Método | Ruta | Respuestas |
|---|---|---|
| GET | `/api/v1/talleres?comuna=&disponibles=` | 200 |
| GET | `/api/v1/talleres/{id}` | 200, 400, 404 |
| GET | `/api/v1/talleres/{id}/denuncias` | 200, 404 |
| POST | `/api/v1/talleres` | 201, 400 |
| PUT | `/api/v1/talleres/{id}` | 200, 400, 404 |
| DELETE | `/api/v1/talleres/{id}` | 204, 404, 409 (tiene asignaciones) |
| POST | `/api/v1/asignaciones` | 201, 400, 409 (sin talleres / ya asignada) |
| GET | `/api/v1/asignaciones?denunciaId=` | 200 |
| GET | `/api/v1/asignaciones/{id}` | 200, 404 |
| PUT | `/api/v1/asignaciones/{id}/recepcion` | 200, 404, 409 |
| PUT | `/api/v1/asignaciones/{id}/finalizar` | 200, 404, 409 |
| PUT | `/api/v1/asignaciones/{id}/cancelar` | 200, 404, 409 |

## Ejecutar

```bash
./mvnw clean install
java -jar target/ms-talleres.jar
```

Swagger UI: http://localhost:8084/swagger-ui.html
