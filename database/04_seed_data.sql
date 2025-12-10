/* ARCHIVO: 03_seed_data.sql
   PROYECTO: Sistema de Control de Asistencias (Slytherin Edition)
   DESCRIPCIÓN: Datos iniciales obligatorios para el funcionamiento del sistema.
   AUTOR: The Architect
*/

PROMPT === INICIANDO CARGA DE CONFIGURACION GLOBAL ===

-- 1. PARAMETROS DEL SISTEMA
-- Vitales para SP_REGISTRAR_ASISTENCIA. Si faltan, el sistema colapsa.
INSERT INTO CONFIGURACION (clave, valor, descripcion)
VALUES ('HORA_ENTRADA', '08:00', 'Hora oficial de inicio de jornada laboral (HH:mm)');

INSERT INTO CONFIGURACION (clave, valor, descripcion)
VALUES ('TOLERANCIA_MINUTOS', '15', 'Minutos de gracia antes de considerar Tardanza');

PROMPT === CREANDO USUARIOS RAÍZ (ROOT USERS) ===

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
    '$2a$10$7Y9aYcgtKXHJfpM63XfeieEGjW8s6Sus9tYvzi839jT/gz0002.rW', -- Hash Dummy
    'admin@sistema.com',
    'System',
    'Administrator',
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
    '$2a$10$BJ6psi9ASgfyQYjl7cOCZ.rcL.q16g1ZBpZvpeGJzPEFZu9rJyV6O', -- Hash Dummy
    'empleado@sistema.com',
    'Juan',
    'Perez',
    'EMPLEADO',
    'A'
);

-- Confirmar transacciones para persistencia inmediata
COMMIT;

PROMPT === SEMILLA PLANTADA. SISTEMA LISTO PARA OPERAR ===