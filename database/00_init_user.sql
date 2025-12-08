/* ARCHIVO: 00_init_user.sql
   EJECUCIÓN: Conectado como ADMIN (Superusuario)
   DESCRIPCIÓN: Crea el esquema de aplicación y asigna permisos.
*/

PROMPT === CREANDO USUARIO DE APLICACION (SCHEMA OWNER) ===

-- 1. Crear el usuario (Schema)
-- Reemplaza "SlytherinSecurePassword123!" con una contraseña fuerte.
CREATE USER CONTROL_ASISTENCIA IDENTIFIED BY "ControlAsistencia123";

-- 2. Asignar Quota (Espacio en disco)
-- En Autonomous Database (ATP), esto es vital.
ALTER USER CONTROL_ASISTENCIA QUOTA UNLIMITED ON DATA;

-- 3. Conceder Permisos (Grant)
-- Roles básicos para conectar y crear objetos.
GRANT CREATE SESSION TO CONTROL_ASISTENCIA;
GRANT CREATE TABLE TO CONTROL_ASISTENCIA;
GRANT CREATE SEQUENCE TO CONTROL_ASISTENCIA;
GRANT CREATE PROCEDURE TO CONTROL_ASISTENCIA;
GRANT CREATE TRIGGER TO CONTROL_ASISTENCIA;
GRANT CREATE VIEW TO CONTROL_ASISTENCIA;
GRANT CREATE JOB TO CONTROL_ASISTENCIA; -- Para el Job de cierre automático (HU-013)

-- Permiso para ejecutar Jobs programados
GRANT EXECUTE ON DBMS_SCHEDULER TO CONTROL_ASISTENCIA;

PROMPT === USUARIO CONTROL_ASISTENCIA LISTO ===