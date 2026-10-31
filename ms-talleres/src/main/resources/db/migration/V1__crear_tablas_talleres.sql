CREATE TABLE talleres (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    nombre           VARCHAR(100) NOT NULL,
    direccion        VARCHAR(150) NOT NULL,
    comuna           VARCHAR(60)  NOT NULL,
    telefono         VARCHAR(20),
    email            VARCHAR(120),
    capacidad_maxima INT          NOT NULL,
    activo           BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_talleres PRIMARY KEY (id)
);

-- denuncia_id referencia a ms-denuncias: no hay llave foranea porque cada servicio tiene su propia base
CREATE TABLE asignaciones (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    denuncia_id      BIGINT       NOT NULL,
    folio            VARCHAR(30)  NOT NULL,
    patente          VARCHAR(10)  NOT NULL,
    estado           VARCHAR(20)  NOT NULL,
    fecha_asignacion TIMESTAMP    NOT NULL,
    fecha_recepcion  TIMESTAMP    NULL,
    observacion      VARCHAR(255),
    taller_id        BIGINT       NOT NULL,
    CONSTRAINT pk_asignaciones PRIMARY KEY (id),
    CONSTRAINT fk_asignaciones_taller FOREIGN KEY (taller_id) REFERENCES talleres (id)
);

CREATE INDEX idx_asignaciones_denuncia ON asignaciones (denuncia_id);
CREATE INDEX idx_asignaciones_taller_estado ON asignaciones (taller_id, estado);
