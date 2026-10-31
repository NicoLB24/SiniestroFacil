-- Polizas de ejemplo para las pruebas en Postman
-- POL-1001 y POL-1002 vigentes, POL-1003 vencida, POL-1004 inactiva
INSERT INTO polizas (numero_poliza, rut_asegurado, nombre_asegurado, patente, fecha_inicio, fecha_fin, activa) VALUES
('POL-1001', '12345678-9', 'Juan Perez Soto',      'ABCD12', '2025-01-01', '2030-12-31', TRUE),
('POL-1002', '11111111-1', 'Maria Gonzalez Rojas', 'WXYZ98', '2025-03-01', '2030-02-28', TRUE),
('POL-1003', '22222222-2', 'Pedro Munoz Diaz',     'KLMN45', '2022-01-01', '2023-12-31', TRUE),
('POL-1004', '33333333-3', 'Ana Torres Vega',      'GHJK77', '2025-01-01', '2030-12-31', FALSE);

INSERT INTO coberturas (nombre, monto_maximo, poliza_id) VALUES
('Danos propios',         15000000.00, 1),
('Responsabilidad civil', 10000000.00, 1),
('Robo total',            12000000.00, 2),
('Danos propios',          8000000.00, 3),
('Danos propios',          9000000.00, 4);
