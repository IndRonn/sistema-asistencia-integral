/* ARCHIVO: 06_analytics.sql
   Ronny Mendez
*/



-- 1. VISTA MONITOR EN VIVO (Perfecta, se mantiene)
CREATE OR REPLACE VIEW V_MONITOR_VIVO AS
SELECT
    u.id_usuario,
    u.nombres || ' ' || u.apellidos AS nombre_completo,
    TO_CHAR(a.hora_entrada, 'HH24:MI:SS') AS hora_entrada,
    a.ip_origen,
    a.device_info
FROM ASISTENCIA a
JOIN USUARIO u ON a.id_usuario = u.id_usuario
WHERE a.fecha = TRUNC(SYSTIMESTAMP AT TIME ZONE 'America/Lima')
  AND a.hora_salida IS NULL;

-- 2. VISTA KPIs (OPTIMIZADA - SINGLE PASS SCAN)
-- Escanea la tabla una sola vez y clasifica las métricas.
CREATE OR REPLACE VIEW V_RESUMEN_DIA AS
WITH metricas_hoy AS (
    SELECT
        COUNT(*) as total_presentes,
        SUM(CASE WHEN estado_asistencia = 'P' THEN 1 ELSE 0 END) as puntuales,
        SUM(CASE WHEN estado_asistencia = 'T' THEN 1 ELSE 0 END) as tardanzas
    FROM ASISTENCIA
    WHERE fecha = TRUNC(SYSTIMESTAMP AT TIME ZONE 'America/Lima')
),
total_staff AS (
    SELECT COUNT(*) as total_empleados FROM USUARIO WHERE estado = 'A'
)
SELECT
    t.total_empleados,
    COALESCE(m.total_presentes, 0) AS presentes,
    COALESCE(m.puntuales, 0) AS puntuales,
    COALESCE(m.tardanzas, 0) AS tardanzas,
    -- Cálculo derivado: Los que no han llegado son (Total - Presentes)
    (t.total_empleados - COALESCE(m.total_presentes, 0)) AS ausentes
FROM total_staff t
LEFT JOIN metricas_hoy m ON 1=1; -- Join ficticio para unir ambas métricas
/

CREATE OR REPLACE VIEW V_ESTADISTICAS_SEMANALES AS
WITH staff_activo AS (
    SELECT COUNT(*) as total_empleados FROM USUARIO WHERE estado = 'A'
)
SELECT
    TO_CHAR(fecha, 'YYYY-MM-DD') AS fecha,
    -- Obtenemos las 3 primeras letras: LUN, MAR, MIE... y en Mayúsculas
    UPPER(SUBSTR(TO_CHAR(fecha, 'DAY', 'NLS_DATE_LANGUAGE=SPANISH'), 1, 3)) AS dia,

    -- Columna TOTAL (Universo de empleados)
    (SELECT total_empleados FROM staff_activo) AS total,

    -- Puntuales y Tardanzas (Conteo real)
    SUM(CASE WHEN estado_asistencia = 'P' THEN 1 ELSE 0 END) AS puntuales,
    SUM(CASE WHEN estado_asistencia = 'T' THEN 1 ELSE 0 END) AS tardanzas,

    -- Faltas (Cálculo: Universo - (Puntuales + Tardanzas))
    (SELECT total_empleados FROM staff_activo) - COUNT(*) AS faltas

FROM ASISTENCIA
WHERE fecha >= TRUNC(SYSDATE) - 6 -- Últimos 7 días
GROUP BY fecha
ORDER BY fecha ASC;
/