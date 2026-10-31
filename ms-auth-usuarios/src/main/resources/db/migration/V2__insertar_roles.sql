-- Roles definidos en el diseno: el asegurado denuncia y el taller recibe los casos
INSERT INTO roles (nombre, descripcion) VALUES
('ASEGURADO', 'Registra y consulta sus denuncias de siniestros'),
('TALLER',    'Consulta los casos asignados y confirma la recepcion del vehiculo'),
('ADMIN',     'Administra usuarios, polizas y talleres');
