# ms-auth-usuarios

Microservicio de **identidad** de SiniestroFácil: registro de usuarios, login, emisión de tokens JWT y validación de tokens para el autorizador del API Gateway.

- Puerto: `8081`
- Base de datos: `db_usuarios` (MySQL)
- Contraseñas guardadas con BCrypt; JWT firmado con HMAC-SHA256 (jjwt 0.12)

## Modelo

| Entidad | Tabla | Relación |
|---|---|---|
| `Rol` | `roles` | `@OneToMany` → usuarios |
| `Usuario` | `usuarios` | `@ManyToOne` → rol |

Roles: `ASEGURADO`, `TALLER`, `ADMIN` (migración `V2__insertar_roles.sql`).

## Endpoints

| Método | Ruta | Respuestas |
|---|---|---|
| POST | `/api/v1/auth/registro` | 201, 400, 404 (rol), 409 (email repetido) |
| POST | `/api/v1/auth/login` | 200 (token), 400, 401 |
| GET | `/api/v1/auth/validar` | 200, 401 |
| GET | `/api/v1/usuarios?rol=` | 200 |
| GET | `/api/v1/usuarios/{id}` | 200, 404 |
| PUT | `/api/v1/usuarios/{id}` | 200, 400, 404, 409 |
| DELETE | `/api/v1/usuarios/{id}` | 204, 404 |
| GET | `/api/v1/roles` | 200 |

Ejemplo de login:

```json
POST /api/v1/auth/login
{ "email": "juan.perez@mail.cl", "password": "Clave123" }
```

## Configuración

| Variable | Valor por defecto |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/db_usuarios?createDatabaseIfNotExist=true...` |
| `DB_USERNAME` / `DB_PASSWORD` | `root` / `root` |
| `JWT_SECRET` | clave de desarrollo (mín. 32 caracteres) |
| `JWT_EXPIRATION` | `3600` segundos |

## Ejecutar

```bash
./mvnw clean install
java -jar target/ms-auth-usuarios.jar
```

Swagger UI: http://localhost:8081/swagger-ui.html
