-- Talleres en convenio de ejemplo. El taller 1 corresponde al usuario taller.central@mail.cl
INSERT INTO talleres (nombre, direccion, comuna, telefono, email, capacidad_maxima, activo) VALUES
('Taller Automotriz Central', 'Av. Libertador Bernardo O''Higgins 1234', 'Santiago',    '+56222334455', 'taller.central@mail.cl', 5, TRUE),
('Servicio Tecnico Los Andes', 'Av. Vicuna Mackenna 4567',              'Nunoa',       '+56223456789', 'contacto@losandes.cl',   3, TRUE),
('Desabolladura Maipu',        'Av. Pajaritos 890',                     'Maipu',       '+56229876543', 'maipu@desabolladura.cl', 2, TRUE),
('Taller El Roble',            'Calle El Roble 321',                    'La Florida',  '+56221112233', 'elroble@taller.cl',      4, FALSE);
