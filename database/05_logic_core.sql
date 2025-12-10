/* ARCHIVO: 04_logic_core.sql
   AUTOR: The Architect
   DESCRIPCIÓN: Implementación del Core de Negocio (Triggers, Vistas y Paquetes).
*/

PROMPT === 1. CREANDO TRIGGER DE AUDITORIA (EL OJO QUE TODO LO VE) ===

CREATE OR REPLACE TRIGGER TRG_AUDIT_ASISTENCIA
AFTER INSERT OR UPDATE ON ASISTENCIA
FOR EACH ROW
DECLARE
    v_accion VARCHAR2(10);
    v_datos  VARCHAR2(4000);
BEGIN
    IF INSERTING THEN
        v_accion := 'INSERT';
        v_datos := 'Nuevo registro de asistencia. ID: ' || :NEW.id_asistencia;
    ELSIF UPDATING THEN
        v_accion := 'UPDATE';
        v_datos := 'Cambio Estado: ' || :OLD.estado_asistencia || ' -> ' || :NEW.estado_asistencia ||
                   ' | Salida: ' || TO_CHAR(:OLD.hora_salida, 'HH24:MI:SS') || ' -> ' || TO_CHAR(:NEW.hora_salida, 'HH24:MI:SS');
    END IF;

    -- Inserción directa en el log (Eficiencia pura)
    INSERT INTO LOG_ASISTENCIA (
        id_log_asis, id_asistencia, accion, datos_anteriores, usuario_modificador
    ) VALUES (
        SEQ_LOG_ASIS.NEXTVAL, :NEW.id_asistencia, v_accion, v_datos, USER
    );
END;
/

PROMPT === 2. CREANDO VISTA DETALLADA (LECTURA OPTIMIZADA) ===

CREATE OR REPLACE VIEW V_ASISTENCIA_DETALLADA AS
SELECT
    a.id_asistencia,
    a.id_usuario,
    u.nombres || ' ' || u.apellidos AS nombre_completo,
    u.email,
    a.fecha,
    TO_CHAR(a.hora_entrada, 'HH24:MI:SS') AS hora_entrada_str,
    TO_CHAR(a.hora_salida, 'HH24:MI:SS') AS hora_salida_str,
    a.estado_asistencia,
    CASE a.estado_asistencia
        WHEN 'P' THEN 'PUNTUAL'
        WHEN 'T' THEN 'TARDANZA'
        WHEN 'A' THEN 'AUSENTE'
        WHEN 'O' THEN 'CIERRE AUTO'
        WHEN 'J' THEN 'JUSTIFICADO'
    END AS estado_texto,
    -- Columna calculada para facilitar lógica en frontend
    CASE WHEN a.estado_asistencia = 'T' THEN 1 ELSE 0 END AS es_tardanza
FROM ASISTENCIA a
JOIN USUARIO u ON a.id_usuario = u.id_usuario;
/

PROMPT === 3. COMPILANDO PAQUETE PKG_ASISTENCIA (EL CEREBRO) ===

-- A. ESPECIFICACIÓN (HEADER)
CREATE OR REPLACE PACKAGE PKG_ASISTENCIA AS

    -- HU-003: Obtener estado actual para el Dashboard
    PROCEDURE SP_OBTENER_ESTADO_ACTUAL(
        p_usuario_id IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    );

    -- HU-004: Botón Maestro (Check-in / Check-out)
    PROCEDURE SP_REGISTRAR_ASISTENCIA(
        p_usuario_id IN NUMBER,
        p_ip_origen  IN VARCHAR2,
        p_device     IN VARCHAR2,
        p_mensaje    OUT VARCHAR2 -- Feedback para el usuario
    );

END PKG_ASISTENCIA;
/

-- B. CUERPO (BODY) - LA LÓGICA DURA
CREATE OR REPLACE PACKAGE BODY PKG_ASISTENCIA AS

    -- HU-003: Obtener estado
    PROCEDURE SP_OBTENER_ESTADO_ACTUAL(
        p_usuario_id IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) IS
        -- Variable para saber "qué día es hoy" en Perú
        v_hoy DATE;
    BEGIN
        -- Calculamos la fecha en Lima, ignorando la hora del servidor en EEUU
        v_hoy := TRUNC(SYSTIMESTAMP AT TIME ZONE 'America/Lima');

        OPEN p_cursor FOR
            SELECT
                id_asistencia,
                hora_entrada,
                hora_salida,
                estado_asistencia,
                CASE
                    WHEN hora_salida IS NOT NULL THEN 'FINALIZADO'
                    ELSE 'EN_CURSO'
                    END AS estado_actual_jornada
            FROM ASISTENCIA
            WHERE id_usuario = p_usuario_id
              AND fecha = v_hoy; -- Usamos la fecha corregida
    END SP_OBTENER_ESTADO_ACTUAL;

    -- HU-004: Registrar Asistencia
    PROCEDURE SP_REGISTRAR_ASISTENCIA(
        p_usuario_id IN NUMBER,
        p_ip_origen  IN VARCHAR2,
        p_device     IN VARCHAR2,
        p_mensaje    OUT VARCHAR2
    ) IS
        v_existe NUMBER;
        v_hora_entrada_config VARCHAR2(5);
        v_tolerancia_min NUMBER;
        v_hora_limite TIMESTAMP; -- Cambiado a TIMESTAMP para precisión
        v_estado_calculado CHAR(1);
        v_asistencia_row ASISTENCIA%ROWTYPE;

        -- EL RELOJ MAESTRO: La hora exacta en Perú AHORA MISMO
        v_ahora TIMESTAMP;
        v_hoy DATE;
    BEGIN
        -- 1. Sincronizar Relojes (Magia de Timezone)
        v_ahora := SYSTIMESTAMP AT TIME ZONE 'America/Lima';
        v_hoy   := TRUNC(v_ahora);

        -- 2. Validar Usuario Activo
        SELECT COUNT(*) INTO v_existe FROM USUARIO WHERE id_usuario = p_usuario_id AND estado = 'A';
        IF v_existe = 0 THEN
            RAISE_APPLICATION_ERROR(-20002, 'Usuario inactivo o no existe.');
        END IF;

        -- 3. Buscar registro de HOY (Hora Perú)
        BEGIN
            SELECT * INTO v_asistencia_row
            FROM ASISTENCIA
            WHERE id_usuario = p_usuario_id AND fecha = v_hoy;

            v_existe := 1;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                v_existe := 0;
        END;

        -- 4. Lógica de Decisión
        IF v_existe = 0 THEN
            -- === ENTRADA ===

            -- Obtener Configuración (Con fallback si falta datos)
            BEGIN
                SELECT valor INTO v_hora_entrada_config FROM CONFIGURACION WHERE clave = 'HORA_ENTRADA';
                SELECT TO_NUMBER(valor) INTO v_tolerancia_min FROM CONFIGURACION WHERE clave = 'TOLERANCIA_MINUTOS';
            EXCEPTION WHEN NO_DATA_FOUND THEN
                v_hora_entrada_config := '08:00';
                v_tolerancia_min := 15;
            END;

            -- Calcular Límite usando la fecha corregida
            -- Construimos el timestamp límite combinando el día de hoy (Perú) con la hora config
            v_hora_limite := TO_TIMESTAMP(TO_CHAR(v_hoy, 'DD/MM/YYYY') || ' ' || v_hora_entrada_config, 'DD/MM/YYYY HH24:MI');
            v_hora_limite := v_hora_limite + NUMTODSINTERVAL(v_tolerancia_min, 'MINUTE');

            -- Comparar AHORA vs LÍMITE
            IF v_ahora > v_hora_limite THEN
                v_estado_calculado := 'T';
                p_mensaje := 'Entrada registrada con TARDANZA.';
            ELSE
                v_estado_calculado := 'P';
                p_mensaje := 'Entrada registrada PUNTUALMENTE.';
            END IF;

            -- INSERTAR (Usando v_ahora y v_hoy corregidos)
            INSERT INTO ASISTENCIA (
                id_asistencia, id_usuario, fecha, hora_entrada, estado_asistencia, ip_origen, device_info
            ) VALUES (
                         SEQ_ASISTENCIA.NEXTVAL, p_usuario_id, v_hoy, v_ahora, v_estado_calculado, p_ip_origen, p_device
                     );

        ELSE
            -- === SALIDA ===
            IF v_asistencia_row.hora_salida IS NOT NULL THEN
                RAISE_APPLICATION_ERROR(-20001, 'Ya marcó su salida el día de hoy.');
            END IF;

            -- ACTUALIZAR (Usando v_ahora corregido)
            UPDATE ASISTENCIA
            SET hora_salida = v_ahora
            WHERE id_asistencia = v_asistencia_row.id_asistencia;

            p_mensaje := 'Salida registrada correctamente. Jornada finalizada.';
        END IF;

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END SP_REGISTRAR_ASISTENCIA;

END PKG_ASISTENCIA;
/

PROMPT === LOGICA CORE DESPLEGADA EXITOSAMENTE ===