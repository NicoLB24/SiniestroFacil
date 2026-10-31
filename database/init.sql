-- Una base de datos por microservicio (patron database-per-service).
-- Ningun servicio accede a la base de otro.
CREATE DATABASE IF NOT EXISTS db_usuarios  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_polizas   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_denuncias CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS db_talleres  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
