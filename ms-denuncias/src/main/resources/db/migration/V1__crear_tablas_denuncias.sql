CREATE TABLE denuncias (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    folio               VARCHAR(30)   NOT NULL,
    rut_asegurado       VARCHAR(12)   NOT NULL,
    numero_poliza       VARCHAR(20)   NOT NULL,
    patente             VARCHAR(10)   NOT NULL,
    fecha_siniestro     TIMESTAMP     NOT NULL,
    lugar               VARCHAR(150)  NOT NULL,
    comuna              VARCHAR(60),
    descripcion         VARCHAR(1000) NOT NULL,
    estado              VARCHAR(20)   NOT NULL,
    taller_id           BIGINT,
    asignacion_id       BIGINT,
    detalle_estado      VARCHAR(255),
    fecha_registro      TIMESTAMP     NOT NULL,
    fecha_actualizacion TIMESTAMP     NOT NULL,
    CONSTRAINT pk_denuncias PRIMARY KEY (id),
    CONSTRAINT uk_denuncias_folio UNIQUE (folio)
);

CREATE TABLE fotos (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    url         VARCHAR(500) NOT NULL,
    descripcion VARCHAR(150),
    fecha_carga TIMESTAMP    NOT NULL,
    denuncia_id BIGINT       NOT NULL,
    CONSTRAINT pk_fotos PRIMARY KEY (id),
    CONSTRAINT fk_fotos_denuncia FOREIGN KEY (denuncia_id) REFERENCES denuncias (id) ON DELETE CASCADE
);

CREATE INDEX idx_denuncias_rut ON denuncias (rut_asegurado);
CREATE INDEX idx_denuncias_estado ON denuncias (estado);
