/* ARCHIVO: 04_seed_data.sql
   Ronny Mendez
*/


-- 1. PARAMETROS DEL SISTEMA
-- Vitales para SP_REGISTRAR_ASISTENCIA. Si faltan, el sistema colapsa.
INSERT INTO CONFIGURACION (clave, valor, descripcion)
VALUES ('HORA_ENTRADA', '08:00', 'Hora oficial de inicio de jornada laboral (HH:mm)');

INSERT INTO CONFIGURACION (clave, valor, descripcion)
VALUES ('TOLERANCIA_MINUTOS', '15', 'Minutos de gracia antes de considerar Tardanza');


-- NOTA DE SEGURIDAD:
-- En producción, las contraseñas NUNCA deben ser texto plano.
-- Aquí usamos un hash BCrypt de ejemplo válido para 'password123'.
-- El backend debe generar estos hashes.

-- 2. ADMINISTRADOR SUPREMO
-- Este usuario tendrá acceso total a la gestión y reportes.
INSERT INTO USUARIO (
    id_usuario,
    username,
    password,
    email,
    nombres,
    apellidos,
    rol,
    estado
) VALUES (
    SEQ_USUARIO.NEXTVAL,
    'admin',
    '2a$10$sLB0JvVHQY/RbaIC8R7D3u9B33UOKi4Ab9Qqqz63rAM53jWk8iJPm', -- Hash Dummy
    'admin@sistema.com',
    'Ronny',
    'Mendez',
    'ADMIN',
    'A'
);

-- 3. EMPLEADO BETA (Para pruebas de Check-in/Check-out)
INSERT INTO USUARIO (
    id_usuario,
    username,
    password,
    email,
    nombres,
    apellidos,
    rol,
    estado
) VALUES (
    SEQ_USUARIO.NEXTVAL,
    'empleado1',
    '$2a$10$2j2tX8EepmIGzgNc0bRIkOSUwH1966iOgnwQhWWFB42nXCh.v5gKe', -- Hash Dummy
    'empleado@sistema.com',
    'Juan',
    'Perez',
    'EMPLEADO',
    'A'
);

-- Confirmar transacciones para persistencia inmediata
COMMIT;


