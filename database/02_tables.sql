/* ARCHIVO: 01_tables.sql
   PROYECTO: Sistema de Control de Asistencias (Slytherin Edition)
   DESCRIPCIÓN: Definición estructural de tablas maestras y de auditoría.
   AUTOR: The Architect
*/

PROMPT === INICIANDO CREACION DE TABLAS MAESTRAS ===

-- 1. TABLA USUARIO
-- El núcleo de identidad. Restricciones estrictas para evitar usuarios duplicados o roles inválidos.
CREATE TABLE USUARIO (
    id_usuario      NUMBER(10)      NOT NULL,
    username        VARCHAR2(50)    NOT NULL,
    password        VARCHAR2(100)   NOT NULL, -- Hash BCrypt (60 chars min)
    email           VARCHAR2(100)   NOT NULL,
    nombres         VARCHAR2(100)   NOT NULL,
    apellidos       VARCHAR2(100)   NOT NULL,
    rol             VARCHAR2(20)    NOT NULL,
    estado          CHAR(1)         DEFAULT 'A' NOT NULL,
    created_at      TIMESTAMP       DEFAULT SYSDATE NOT NULL,

    CONSTRAINT PK_USUARIO PRIMARY KEY (id_usuario),
    CONSTRAINT UK_USUARIO_USERNAME UNIQUE (username),
    CONSTRAINT UK_USUARIO_EMAIL UNIQUE (email),

    -- Reglas de Negocio Inmutables:
    CONSTRAINT CHK_USUARIO_ROL CHECK (rol IN ('ADMIN', 'EMPLEADO')),
    CONSTRAINT CHK_USUARIO_ESTADO CHECK (estado IN ('A', 'I')) -- A=Activo, I=Inactivo
);

-- 2. TABLA ASISTENCIA
-- La verdad operativa. Un usuario NO puede tener dos registros el mismo día (UK_ASISTENCIA_DIA).
CREATE TABLE ASISTENCIA (
    id_asistencia     NUMBER(15)    NOT NULL,
    id_usuario        NUMBER(10)    NOT NULL,
    fecha             DATE          NOT NULL, -- Se almacenará TRUNC(SYSDATE)
    hora_entrada      TIMESTAMP     NOT NULL,
    hora_salida       TIMESTAMP,              -- NULL mientras la jornada está abierta
    estado_asistencia CHAR(1)       NOT NULL,
    ip_origen         VARCHAR2(50),           -- Auditoría de ubicación
    device_info       VARCHAR2(100),          -- Auditoría de dispositivo

    CONSTRAINT PK_ASISTENCIA PRIMARY KEY (id_asistencia),

    -- P=Puntual, T=Tarde, A=Ausente, J=Justificado, O=Olvido(Cierre Auto)
    CONSTRAINT CHK_ASIS_ESTADO CHECK (estado_asistencia IN ('P', 'T', 'A', 'J', 'O')),

    -- Constraint Clave: Integridad física contra doble marcación
    CONSTRAINT UK_ASISTENCIA_DIA UNIQUE (id_usuario, fecha)
);

-- 3. TABLA JUSTIFICACION
-- Gestión de incidencias. Vincula empleados con admin aprobadores.
CREATE TABLE JUSTIFICACION (
    id_justificacion NUMBER(10)     NOT NULL,
    id_usuario       NUMBER(10)     NOT NULL, -- Empleado solicitante
    id_asistencia    NUMBER(15),              -- Puede ser NULL si justifica una falta total (sin registro previo)
    fecha_justificar DATE           NOT NULL,
    motivo           VARCHAR2(500)  NOT NULL,
    tipo             VARCHAR2(20)   NOT NULL,
    estado           VARCHAR2(20)   DEFAULT 'PENDIENTE' NOT NULL,
    fecha_solicitud  TIMESTAMP      DEFAULT SYSDATE NOT NULL,
    admin_aprobador  NUMBER(10),              -- ID del Admin que resuelve
    fecha_resolucion TIMESTAMP,

    CONSTRAINT PK_JUSTIFICACION PRIMARY KEY (id_justificacion),

    -- Valores permitidos según diccionario de datos
    CONSTRAINT CHK_JUST_TIPO CHECK (tipo IN ('SALUD', 'PERSONAL', 'TRABAJO')),
    CONSTRAINT CHK_JUST_ESTADO CHECK (estado IN ('PENDIENTE', 'APROBADO', 'RECHAZADO'))
);

-- 4. TABLA CONFIGURACION
-- Parámetros dinámicos para evitar "Hardcoding" en la aplicación.
CREATE TABLE CONFIGURACION (
    clave       VARCHAR2(50)  NOT NULL,
    valor       VARCHAR2(100) NOT NULL,
    descripcion VARCHAR2(200),

    CONSTRAINT PK_CONFIGURACION PRIMARY KEY (clave)
);

PROMPT === INICIANDO CREACION DE TABLAS DE AUDITORIA (THE WATCHERS) ===

-- 5. LOG_ASISTENCIA
-- Historial inmutable de cambios en asistencias (correcciones manuales, cierres automáticos).
CREATE TABLE LOG_ASISTENCIA (
    id_log_asis         NUMBER(15)      NOT NULL,
    id_asistencia       NUMBER(15)      NOT NULL,
    accion              VARCHAR2(10)    NOT NULL, -- INSERT o UPDATE
    datos_anteriores    VARCHAR2(4000),           -- Snapshot JSON de los datos previos
    usuario_modificador VARCHAR2(50)    NOT NULL, -- Quién hizo el cambio
    fecha_log           TIMESTAMP       DEFAULT SYSDATE NOT NULL,

    CONSTRAINT PK_LOG_ASIS PRIMARY KEY (id_log_asis),
    CONSTRAINT CHK_LOG_ASIS_ACC CHECK (accion IN ('INSERT', 'UPDATE'))
);

-- 6. LOG_JUSTIFICACION
-- Trazabilidad del flujo de aprobación. Quién aprobó qué y cuándo.
CREATE TABLE LOG_JUSTIFICACION (
    id_log_just         NUMBER(15)      NOT NULL,
    id_justificacion    NUMBER(10)      NOT NULL,
    accion              VARCHAR2(10)    NOT NULL,
    estado_anterior     VARCHAR2(20),
    estado_nuevo        VARCHAR2(20),
    comentario          VARCHAR2(200),            -- Razón del rechazo/aprobación
    usuario_modificador VARCHAR2(50)    NOT NULL,
    fecha_log           TIMESTAMP       DEFAULT SYSDATE NOT NULL,

    CONSTRAINT PK_LOG_JUST PRIMARY KEY (id_log_just),
    CONSTRAINT CHK_LOG_JUST_ACC CHECK (accion IN ('INSERT', 'UPDATE'))
);

-- 7. LOG_SEGURIDAD (Extra Mile - HU-014)
-- Auditoría de accesos e intentos de intrusión.
CREATE TABLE LOG_SEGURIDAD (
    id_log_seg  NUMBER(15)    NOT NULL,
    username    VARCHAR2(50),             -- Username intentado (puede no existir)
    evento      VARCHAR2(50)  NOT NULL,   -- LOGIN_OK, LOGIN_FAIL, LOGOUT
    ip_origen   VARCHAR2(50)  NOT NULL,
    user_agent  VARCHAR2(200),            -- Navegador/Dispositivo
    fecha       TIMESTAMP     DEFAULT SYSDATE NOT NULL,

    CONSTRAINT PK_LOG_SEG PRIMARY KEY (id_log_seg)
);

PROMPT === CREACION DE TABLAS COMPLETADA CON EXITO ===