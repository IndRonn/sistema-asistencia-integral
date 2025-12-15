/* ARCHIVO: 99_pre_demo.sql
   Ronny Mendez
*/

-- 1. Limpieza
DELETE FROM LOG_ASISTENCIA;
DELETE FROM JUSTIFICACION;
DELETE FROM ASISTENCIA;
COMMIT;

-- 2. Inserción de Historia (Últimos 7 días)
DECLARE
    v_empleado_id NUMBER;
    v_fecha DATE;
BEGIN
    -- Obtenemos el ID del empleado 'empleado' (creado en seed_data)
    SELECT id_usuario INTO v_empleado_id FROM USUARIO WHERE username = 'empleado';

    -- Loop para los últimos 5 días
    FOR i IN 1..5 LOOP
        v_fecha := TRUNC(SYSDATE) - i;

        -- Día 1: Puntual (08:00 - 17:00)
        IF i = 1 THEN
            INSERT INTO ASISTENCIA (id_asistencia, id_usuario, fecha, hora_entrada, hora_salida, estado_asistencia)
            VALUES (SEQ_ASISTENCIA.NEXTVAL, v_empleado_id, v_fecha,
                    v_fecha + 8/24, v_fecha + 17/24, 'P');

        -- Día 2: Tarde (08:20 - 17:00)
        ELSIF i = 2 THEN
            INSERT INTO ASISTENCIA (id_asistencia, id_usuario, fecha, hora_entrada, hora_salida, estado_asistencia)
            VALUES (SEQ_ASISTENCIA.NEXTVAL, v_empleado_id, v_fecha,
                    v_fecha + 8/24 + 20/1440, v_fecha + 17/24, 'T');

        -- Día 3: Olvido de Salida (Job)
        ELSIF i = 3 THEN
            INSERT INTO ASISTENCIA (id_asistencia, id_usuario, fecha, hora_entrada, hora_salida, estado_asistencia)
            VALUES (SEQ_ASISTENCIA.NEXTVAL, v_empleado_id, v_fecha,
                    v_fecha + 7.5/24, TO_TIMESTAMP(TO_CHAR(v_fecha, 'DD/MM/YYYY') || ' 23:59:59', 'DD/MM/YYYY HH24:MI:SS'), 'O');

        -- Día 4: Falta (Ausente)
        -- Día 5: Puntual
        ELSIF i = 5 THEN
            INSERT INTO ASISTENCIA (id_asistencia, id_usuario, fecha, hora_entrada, hora_salida, estado_asistencia)
            VALUES (SEQ_ASISTENCIA.NEXTVAL, v_empleado_id, v_fecha,
                    v_fecha + 7.9/24, v_fecha + 18/24, 'P');
        END IF;

    END LOOP;

    COMMIT;
END;
/
