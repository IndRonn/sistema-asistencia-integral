# 📚 Diccionario de Datos Maestro (Parte 1: Núcleo Operativo)
**Sistema:** Control de Asistencias "Slytherin Edition"
**Base de Datos:** Oracle Database (Compatible 19c/21c/23c OCI)
**Schema Owner:** `CONTROL_ASISTENCIA`

---

## 1. TABLA: `USUARIO`
**Propósito:** Gestionar la identidad, acceso y roles de todos los actores del sistema.
**Historias de Usuario:** HU-001 (Login), HU-002 (Roles), HU-010 (Gestión Usuarios).

| Columna | Tipo de Dato (Oracle) | Obligatorio | Restricciones / Default | Descripción Técnica |
| :--- | :--- | :---: | :--- | :--- |
| `id_usuario` | `NUMBER(10)` | **SÍ** | **PK** (Primary Key) | Identificador único. Generado por secuencia `SEQ_USUARIO`. |
| `username` | `VARCHAR2(50)` | **SÍ** | **UNIQUE** | Nombre de usuario único para login. **Índice Único** requerido. |
| `password` | `VARCHAR2(100)` | **SÍ** | | Hash de contraseña (BCrypt). Longitud suficiente para hash de 60 chars. |
| `email` | `VARCHAR2(100)` | **SÍ** | **UNIQUE** | Correo corporativo único. |
| `nombres` | `VARCHAR2(100)` | **SÍ** | | Nombres completos. |
| `apellidos` | `VARCHAR2(100)` | **SÍ** | | Apellidos completos. |
| `rol` | `VARCHAR2(20)` | **SÍ** | `CHECK (rol IN ('ADMIN', 'EMPLEADO'))` | Nivel de autoridad. Sin roles dinámicos por ahora (Hardcoded). |
| `estado` | `CHAR(1)` | **SÍ** | `DEFAULT 'A'` <br> `CHECK (estado IN ('A', 'I'))` | 'A'=Activo, 'I'=Inactivo. **Regla:** Usuarios 'I' no pueden loguearse. |
| `created_at` | `TIMESTAMP` | **SÍ** | `DEFAULT SYSDATE` | Fecha de creación del registro (Auditoría). |

**📝 Notas de Arquitectura:**
* El campo `username` y `email` deben tener índices `UNIQUE` físicos para evitar duplicados a nivel de motor.
* No se borran usuarios físicamente, solo se pasan a `estado = 'I'` (Soft Delete).

---

## 2. TABLA: `ASISTENCIA`
**Propósito:** Registrar los eventos de entrada y salida diarios. Es la tabla transaccional más importante.
**Historias de Usuario:** HU-003 (Dashboard), HU-004 (Check-in), HU-005 (Historial).

| Columna | Tipo de Dato (Oracle) | Obligatorio | Restricciones / Default | Descripción Técnica |
| :--- | :--- | :---: | :--- | :--- |
| `id_asistencia` | `NUMBER(15)` | **SÍ** | **PK** (Primary Key) | Identificador único. Secuencia `SEQ_ASISTENCIA`. |
| `id_usuario` | `NUMBER(10)` | **SÍ** | **FK** ref `USUARIO` | Relación con el empleado. |
| `fecha` | `DATE` | **SÍ** | | Fecha normalizada (TRUNC). Sin hora. Vital para el índice compuesto. |
| `hora_entrada` | `TIMESTAMP` | **SÍ** | | Hora exacta del registro de entrada (sysdate completo). |
| `hora_salida` | `TIMESTAMP` | NO | `NULL` | Hora de salida. Si es NULL, la jornada sigue abierta. |
| `estado_asistencia`| `CHAR(1)` | **SÍ** | `CHECK (estado_asistencia IN ('P', 'T', 'A'))` | Clasificación calculada por SP: <br>'P'=Puntual <br>'T'=Tarde <br>'A'=Ausente/Falta (Generado por job o manual). |
| `ip_origen` | `VARCHAR2(50)` | NO | | Dirección IP desde donde se marcó (Auditoría HU-14). |
| `device_info` | `VARCHAR2(100)` | NO | | User-Agent o info del dispositivo (Opcional). |

**🔒 Restricciones de Integridad (Constraints):**
1.  **Unique Constraint:** `CONSTRAINT UK_ASISTENCIA_DIA UNIQUE (id_usuario, fecha)`
    * *Objetivo:* Impide físicamente que un usuario tenga dos registros de asistencia el mismo día. Garantiza la HU-004 "No doble marca".

**🔎 Índices de Rendimiento:**
1.  `IDX_ASISTENCIA_FECHA`: Sobre columna `fecha` (Para reportes rápidos).
2.  `IDX_ASISTENCIA_USR_FECHA`: Sobre `(id_usuario, fecha)` (Para el Dashboard HU-003).

---

## 3. TABLA: `JUSTIFICACION`
**Propósito:** Manejar solicitudes de corrección de asistencia.
**Historias de Usuario:** HU-006 (Solicitud), HU-007 (Aprobación).

| Columna | Tipo de Dato (Oracle) | Obligatorio | Restricciones / Default | Descripción Técnica |
| :--- | :--- | :---: | :--- | :--- |
| `id_justificacion` | `NUMBER(10)` | **SÍ** | **PK** (Primary Key) | Secuencia `SEQ_JUSTIFICACION`. |
| `id_usuario` | `NUMBER(10)` | **SÍ** | **FK** ref `USUARIO` | Empleado que solicita. |
| `id_asistencia` | `NUMBER(15)` | NO | **FK** ref `ASISTENCIA` | Registro vinculado. Puede ser NULL si se justifica una falta total (día sin registro). |
| `fecha_justificar` | `DATE` | **SÍ** | | Fecha sobre la cual se aplica la justificación. |
| `motivo` | `VARCHAR2(500)` | **SÍ** | | Texto explicativo del empleado. |
| `tipo` | `VARCHAR2(20)` | **SÍ** | `CHECK (tipo IN ('SALUD', 'PERSONAL', 'TRABAJO'))` | Categoría para estadísticas. |
| `estado` | `VARCHAR2(20)` | **SÍ** | `DEFAULT 'PENDIENTE'` <br> `CHECK (estado IN ('PENDIENTE', 'APROBADO', 'RECHAZADO'))` | Estado del flujo de aprobación. |
| `fecha_solicitud` | `TIMESTAMP` | **SÍ** | `DEFAULT SYSDATE` | Cuándo se creó la solicitud. |
| `admin_aprobador` | `NUMBER(10)` | NO | **FK** ref `USUARIO` | ID del Admin que resolvió la solicitud (Auditoría). |
| `fecha_resolucion` | `TIMESTAMP` | NO | | Cuándo se aprobó/rechazó. |

**📝 Reglas de Negocio en BD:**
* Un usuario no puede tener dos justificaciones `PENDIENTE` para la misma `fecha_justificar`. Validar mediante Trigger o SP.

---
---

## 4. TABLA: `CONFIGURACION`
**Propósito:** Almacenar parámetros globales del sistema modificables en caliente.
**Historias de Usuario:** HU-008 (Configuración Dinámica).
[cite_start]**Justificación:** Necesaria para el requerimiento "Controlar tolerancia de minutos"[cite: 49].

| Columna | Tipo de Dato (Oracle) | Obligatorio | Restricciones | Descripción Técnica |
| :--- | :--- | :---: | :--- | :--- |
| `clave` | `VARCHAR2(50)` | **SÍ** | **PK** | Identificador (ej. 'TOLERANCIA_MINUTOS'). |
| `valor` | `VARCHAR2(100)` | **SÍ** | | Valor actual del parámetro. |
| `descripcion` | `VARCHAR2(200)` | NO | | Explicación humana. |

**💾 Datos Semilla:**
1.  `('HORA_ENTRADA', '08:00', 'Hora oficial')`
2.  `('TOLERANCIA_MINUTOS', '15', 'Minutos antes de marcar Tarde')`

---

## 5. TABLA: `LOG_ASISTENCIA`
**Propósito:** Auditoría de cambios en registros de asistencia.
[cite_start]**Requisito PDF:** [cite: 71] "LOG_ASISTENCIA Log de la asistencia".

| Columna | Tipo de Dato (Oracle) | Obligatorio | Restricciones | Descripción Técnica |
| :--- | :--- | :---: | :--- | :--- |
| `id_log_asis` | `NUMBER(15)` | **SÍ** | **PK** | Secuencia `SEQ_LOG_ASIS`. |
| `id_asistencia` | `NUMBER(15)` | **SÍ** | **FK** ref `ASISTENCIA` | El registro que fue modificado. |
| `accion` | `VARCHAR2(10)` | **SÍ** | `CHECK ('INSERT', 'UPDATE')` | Tipo de cambio. |
| `datos_anteriores` | `VARCHAR2(4000)` | NO | `JSON` | Snapshot de los datos antes del cambio (vital si un Admin corrige una hora). |
| `usuario_modificador` | `VARCHAR2(50)` | **SÍ** | | Username del responsable (o 'SYSTEM'). |
| `fecha_log` | `TIMESTAMP` | **SÍ** | `DEFAULT SYSDATE` | Cuándo ocurrió. |

---

## 6. TABLA: `LOG_JUSTIFICACION`
**Propósito:** Auditoría del flujo de aprobación/rechazo.
[cite_start]**Requisito PDF:** [cite: 72] "LOG_JUSTIFICACION Log de las justificaciones".

| Columna | Tipo de Dato (Oracle) | Obligatorio | Restricciones | Descripción Técnica |
| :--- | :--- | :---: | :--- | :--- |
| `id_log_just` | `NUMBER(15)` | **SÍ** | **PK** | Secuencia `SEQ_LOG_JUST`. |
| `id_justificacion` | `NUMBER(10)` | **SÍ** | **FK** ref `JUSTIFICACION` | La solicitud afectada. |
| `accion` | `VARCHAR2(10)` | **SÍ** | `CHECK ('INSERT', 'UPDATE')` | Usualmente 'UPDATE' al aprobar/rechazar. |
| `estado_anterior` | `VARCHAR2(20)` | NO | | Estado previo (ej. 'PENDIENTE'). |
| `estado_nuevo` | `VARCHAR2(20)` | NO | | Nuevo estado (ej. 'APROBADO'). |
| `comentario` | `VARCHAR2(200)` | NO | | Observación del admin al cambiar estado. |
| `usuario_modificador` | `VARCHAR2(50)` | **SÍ** | | Admin que tomó la decisión. |
| `fecha_log` | `TIMESTAMP` | **SÍ** | `DEFAULT SYSDATE` | Cuándo ocurrió. |

---

## 7. TABLA: `LOG_SEGURIDAD` (Extra Mile - HU-014)
**Propósito:** Auditoría de Accesos e IPs (Seguridad).
[cite_start]**Requisito PDF:** Cumple el criterio de "Seguridad" [cite: 50, 89] de forma proactiva.

| Columna | Tipo de Dato (Oracle) | Obligatorio | Restricciones | Descripción Técnica |
| :--- | :--- | :---: | :--- | :--- |
| `id_log_seg` | `NUMBER(15)` | **SÍ** | **PK** | Secuencia `SEQ_LOG_SEG`. |
| `username` | `VARCHAR2(50)` | NO | | Usuario que intentó la acción (si se conoce). |
| `evento` | `VARCHAR2(50)` | **SÍ** | | 'LOGIN_OK', 'LOGIN_FAIL', 'LOGOUT'. |
| `ip_origen` | `VARCHAR2(50)` | **SÍ** | | Dirección IP. |
| `user_agent` | `VARCHAR2(200)` | NO | | Navegador/Dispositivo. |
| `fecha` | `TIMESTAMP` | **SÍ** | `DEFAULT SYSDATE` | Fecha exacta. |

---

## ⚙️ Automatización (Triggers)
* El Gem Architect debe crear `TRG_AUDIT_ASISTENCIA` y `TRG_AUDIT_JUSTIFICACION` para llenar las tablas 5 y 6 automáticamente tras cada INSERT o UPDATE en las tablas principales.

---

## 🛠️ DEFINICIÓN DE OBJETOS PL/SQL (Lógica en BD)

## 7. LÓGICA DE NEGOCIO EN BD (PL/SQL & Triggers)
**Filosofía:** "La base de datos es la última línea de defensa". La lógica crítica reside aquí, no en el backend, para garantizar velocidad e integridad.

### 📦 PAQUETE: `PKG_ASISTENCIA` (Operaciones del Empleado)

#### 1. `PROCEDURE SP_REGISTRAR_ASISTENCIA`
**Propósito:** Manejar el botón "Maestro" de Check-in/Check-out.
* **Parámetros:**
    * `p_id_usuario (IN NUMBER)`: ID del empleado.
    * `p_ip_origen (IN VARCHAR2)`: IP para auditoría.
    * `o_mensaje (OUT VARCHAR2)`: Mensaje para el usuario (ej. "Entrada registrada").
    * `o_status (OUT VARCHAR2)`: Código de estado ('OK', 'ERROR').
    * `o_tipo_marca (OUT VARCHAR2)`: 'ENTRADA' o 'SALIDA'.
* **Lógica Interna (Algoritmo):**
    1.  **Validación 1 (Usuario Activo):** Verificar si `USUARIO.estado = 'A'`. Si no, `RAISE_APPLICATION_ERROR(-20001, 'Usuario inactivo')`.
    2.  **Busqueda de Registro:** Buscar en `ASISTENCIA` un registro donde `id_usuario = p_id_usuario` AND `fecha = TRUNC(SYSDATE)`.
    3.  **CASO A (No existe registro):**
        * Es una **ENTRADA**.
        * Leer `CONFIGURACION` para obtener `HORA_ENTRADA` y `TOLERANCIA`.
        * Calcular estado:
            * Si `SYSDATE` <= (`HORA_ENTRADA` + `TOLERANCIA` mins) -> `estado_asistencia = 'P'` (Puntual).
            * Si `SYSDATE` > (`HORA_ENTRADA` + `TOLERANCIA` mins) -> `estado_asistencia = 'T'` (Tarde).
        * `INSERT INTO ASISTENCIA`.
    4.  **CASO B (Existe registro y `hora_salida` IS NULL):**
        * Es una **SALIDA**.
        * `UPDATE ASISTENCIA SET hora_salida = SYSDATE WHERE id...`.
    5.  **CASO C (Existe registro y `hora_salida` NOT NULL):**
        * Error. `RAISE_APPLICATION_ERROR(-20002, 'Jornada ya cerrada por hoy')`.

#### 2. `PROCEDURE SP_OBTENER_ESTADO_HOY`
**Propósito:** Alimentar el Dashboard (HU-003) para saber qué botón pintar.
* **Parámetros:**
    * `p_id_usuario (IN NUMBER)`
    * `o_cursor (OUT SYS_REFCURSOR)`
* **Query de Retorno:** Selecciona `hora_entrada`, `hora_salida`, `estado_asistencia` del día actual (`TRUNC(SYSDATE)`).

---

### 📦 PAQUETE: `PKG_ADMIN` (Operaciones de Gestión)

#### 1. `PROCEDURE SP_GESTIONAR_JUSTIFICACION`
**Propósito:** Aprobar o Rechazar solicitudes (HU-007). Transaccionalidad pura.
* **Parámetros:**
    * `p_id_justificacion (IN NUMBER)`
    * `p_nuevo_estado (IN VARCHAR2)`: 'APROBADO' o 'RECHAZADO'.
    * `p_id_admin (IN NUMBER)`: Quién ejecuta la acción.
* **Lógica Interna:**
    1.  `UPDATE JUSTIFICACION SET estado = p_nuevo_estado, admin_aprobador = p_id_admin, fecha_resolucion = SYSDATE`.
    2.  **SI `p_nuevo_estado` = 'APROBADO':**
        * Buscar si hay `id_asistencia` asociado.
        * Si existe: `UPDATE ASISTENCIA SET estado_asistencia = 'J'` (Justificado) WHERE `id_asistencia = ...`.
        * Si no existe (era falta): Insertar registro en `ASISTENCIA` con estado 'J' para rellenar el hueco.

#### 2. `PROCEDURE SP_REPORTE_ASISTENCIA`
**Propósito:** Generar la data para Excel/PDF y Tablas (HU-009).
* **Parámetros:**
    * `p_fecha_ini (IN DATE)`
    * `p_fecha_fin (IN DATE)`
    * `p_id_empleado (IN NUMBER DEFAULT NULL)`: Opcional.
    * `o_cursor (OUT SYS_REFCURSOR)`
* **Lógica:**
    * Query dinámico (o con `NVL`) que hace JOIN entre `ASISTENCIA` y `USUARIO`.
    * Calcula columnas extra como "Horas Trabajadas" (Salida - Entrada).

---

### 🔫 TRIGGERS DE AUDITORÍA (El Ojo que Todo lo Ve)

#### 1. `TRG_AUDIT_ASISTENCIA` y `TRG_AUDIT_JUSTIFICACION`
**Tipo:** `AFTER INSERT OR UPDATE ON [TABLA] FOR EACH ROW`
**Lógica:**
* Detectar operación (`INSERTING`, `UPDATING`).
* Insertar en tabla `LOG_CAMBIOS_DATA`:
    * `tabla_afectada`: Nombre de la tabla.
    * `id_registro`: `:NEW.id`.
    * `accion`: 'INSERT'/'UPDATE'.
    * `usuario_modificador`: Usar `SYS_CONTEXT('USERENV', 'OS_USER')` o un valor pasado por sesión si es posible, sino 'SYSTEM'.
    * `datos_anteriores`: Si es UPDATE, guardar un JSON con los valores de `:OLD`.

---

### 🤖 JOBS AUTOMÁTICOS (DBMS_SCHEDULER)

#### 1. `JOB_CIERRE_JORNADA_AUTO` (HU-013)
**Propósito:** Cerrar asistencias olvidadas para que no queden abiertas infinitamente.
* **Schedule:** Diario a las 23:59:00.
* **Bloque PL/SQL:**
    ```sql
    UPDATE ASISTENCIA
    SET hora_salida = TRUNC(SYSDATE) + 0.99999, -- (23:59:59)
        estado_asistencia = 'O' -- (O = Olvido/Cierre Auto)
    WHERE fecha = TRUNC(SYSDATE)
      AND hora_salida IS NULL;
    ```