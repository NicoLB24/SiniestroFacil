# ms-polizas

Microservicio de **pólizas** de SiniestroFácil: catálogo de pólizas con sus coberturas y validación de vigencia, que el procesador de denuncias consulta antes de asignar un taller.

- Puerto: `8082`
- Base de datos: `db_polizas` (MySQL)

## Modelo

| Entidad | Tabla | Relación |
|---|---|---|
| `Poliza` | `polizas` | `@OneToMany(cascade = ALL, orphanRemoval = true)` → coberturas |
| `Cobertura` | `coberturas` | `@ManyToOne` → póliza |

Una póliza está **vigente** si está activa y la fecha de hoy está entre `fechaInicio` y `fechaFin`.

## Endpoints

| Método | Ruta | Respuestas |
|---|---|---|
| GET | `/api/v1/polizas?rut=` | 200 |
| GET | `/api/v1/polizas/{id}` | 200, 404 |
| GET | `/api/v1/polizas/numero/{numero}` | 200, 404 |
| GET | `/api/v1/polizas/numero/{numero}/vigencia` | 200, 404 |
| POST | `/api/v1/polizas` | 201, 400, 409 (número repetido o fechas incoherentes) |
| PUT | `/api/v1/polizas/{id}` | 200, 400, 404, 409 |
| DELETE | `/api/v1/polizas/{id}` | 204, 404 |

Ejemplo:

```json
POST /api/v1/polizas
{
  "numeroPoliza": "POL-2001",
  "rutAsegurado": "19876543-2",
  "nombreAsegurado": "Camila Rojas Fuentes",
  "patente": "PQRS34",
  "fechaInicio": "2025-06-01",
  "fechaFin": "2027-05-31",
  "coberturas": [ { "nombre": "Danos propios", "montoMaximo": 12000000 } ]
}
```

Datos de prueba (`V2__datos_iniciales_polizas.sql`): `POL-1001`, `POL-1002` vigentes; `POL-1003` vencida; `POL-1004` inactiva.

## Ejecutar

```bash
./mvnw clean install
java -jar target/ms-polizas.jar
```

Swagger UI: http://localhost:8082/swagger-ui.html
