/* ARCHIVO: 07_scheduler_jobs_v.sql
   Ronny Mendez
*/

PROMPT === REINTENTANDO CREACION DE PROCEDIMIENTO DE LIMPIEZA ===

CREATE OR REPLACE PROCEDURE SP_CIERRE_DIARIO_AUTO IS
    v_count NUMBER;
    v_ayer  DATE;
BEGIN
    -- 1. Calcular "Ayer" en hora peruana de forma segura
    -- Convertimos el Timestamp con Zona a DATE simple
    v_ayer := TRUNC(CAST(SYSTIMESTAMP AT TIME ZONE 'America/Lima' AS DATE));

    -- 2. Identificar y Cerrar registros abiertos ANTES de hoy
    UPDATE ASISTENCIA
    SET hora_salida = TO_TIMESTAMP(TO_CHAR(fecha, 'DD/MM/YYYY') || ' 23:59:59', 'DD/MM/YYYY HH24:MI:SS'),
        estado_asistencia = 'O', -- O = Olvido / Cierre Automático
        device_info = CASE
                        WHEN device_info IS NULL THEN '[AUTO-CLOSE]'
                        ELSE SUBSTR(device_info || ' [AUTO-CLOSE]', 1, 255)
                      END
    WHERE hora_salida IS NULL
      AND fecha < v_ayer; -- Usamos la variable segura

    v_count := SQL%ROWCOUNT;

    -- 3. Registrar en el Log de Seguridad si hubo trabajo
    IF v_count > 0 THEN
        INSERT INTO LOG_SEGURIDAD (id_log_seg, username, evento, ip_origen, fecha)
        VALUES (SEQ_LOG_SEG.NEXTVAL, 'SYSTEM', 'AUTO_CLOSE_JOB_EXEC', 'LOCALHOST', SYSDATE);

        COMMIT;
    END IF;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        -- En un sistema real, aquí iría un insert a una tabla de errores de sistema
END;
/

PROMPT === REPROGRAMANDO EL JOB ===

BEGIN
    -- Limpieza preventiva
    BEGIN
        DBMS_SCHEDULER.DROP_JOB(job_name => 'JOB_CIERRE_DIARIO');
    EXCEPTION
        WHEN OTHERS THEN NULL;
    END;

    -- Creación del Job (Ejecuta cada noche a las 00:01 AM Hora del Servidor)
    DBMS_SCHEDULER.CREATE_JOB (
        job_name        => 'JOB_CIERRE_DIARIO',
        job_type        => 'STORED_PROCEDURE',
        job_action      => 'SP_CIERRE_DIARIO_AUTO',
        start_date      => SYSTIMESTAMP,
        repeat_interval => 'FREQ=DAILY; BYHOUR=00; BYMINUTE=01; BYSECOND=0',
        enabled         => TRUE,
        comments        => 'Cierra asistencias abiertas del día anterior'
    );
END;
/

PROMPT === JOB REPARADO Y ACTIVO ===