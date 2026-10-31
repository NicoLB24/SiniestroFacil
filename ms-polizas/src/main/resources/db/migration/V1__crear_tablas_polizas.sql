CREATE TABLE polizas (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    numero_poliza    VARCHAR(20)  NOT NULL,
    rut_asegurado    VARCHAR(12)  NOT NULL,
    nombre_asegurado VARCHAR(100) NOT NULL,
    patente          VARCHAR(10)  NOT NULL,
    fecha_inicio     DATE         NOT NULL,
    fecha_fin        DATE         NOT NULL,
    activa           BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_polizas PRIMARY KEY (id),
    CONSTRAINT uk_polizas_numero UNIQUE (numero_poliza)
);

CREATE TABLE coberturas (
    id           BIGINT         NOT NULL AUTO_INCREMENT,
    nombre       VARCHAR(60)    NOT NULL,
    monto_maximo DECIMAL(12, 2) NOT NULL,
    poliza_id    BIGINT         NOT NULL,
    CONSTRAINT pk_coberturas PRIMARY KEY (id),
    CONSTRAINT fk_coberturas_poliza FOREIGN KEY (poliza_id) REFERENCES polizas (id) ON DELETE CASCADE
);

CREATE INDEX idx_polizas_rut ON polizas (rut_asegurado);
