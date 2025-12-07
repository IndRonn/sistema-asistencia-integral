## 🔐 ÉPICA: SEGURIDAD Y ACCESO (The Gate)

### HU-001: Autenticación y Generación de Token (JWT)

**Como** Usuario (Administrador o Empleado),
**Quiero** ingresar mis credenciales en un formulario seguro,
**Para** obtener un token de acceso (JWT) que me identifique y me redirija a mi dashboard correspondiente.

- **Criterios de Aceptación Funcionales:**
    1. El sistema debe validar que los campos `username` y `password` no estén vacíos.
    2. Si las credenciales son incorrectas, mostrar una alerta flotante (Toast) color **Vino**: "Credenciales no válidas". No detallar si falló usuario o clave (por seguridad).
    3. Al autenticarse correctamente, el backend debe retornar un **JWT** que contenga el `ROL` del usuario.
    4. Redirección automática basada en el rol desencriptado del token:
        - **ADMIN** → Redirige a `/admin/dashboard`.
        - **EMPLEADO** → Redirige a `/employee/dashboard`.
    5. **UI/UX:** El botón "Ingresar" debe mostrar un estado de carga (loading spinner minimalista) y deshabilitarse durante la petición.
- **🛠️ Instrucciones Técnicas para los Gems:**
    - **Frontend (Angular):** Crear `AuthService` con método `login(credentials)`. Usar `Signals` para manejar el estado `isLoading`. Guardar JWT en `localStorage`.
    - **Backend (Spring):** Endpoint `POST /auth/login`. Usar `Spring Security` + `JJWT`. Validar contraseña hasheada (BCrypt) contra tabla `USUARIO`.
    - **DB:** Consultar usuario activo (`estado = 'A'`).

### HU-002: Protección de Rutas y Cierre de Sesión

**Como** Usuario autenticado,
**Quiero** que el sistema proteja las rutas según mi rol y me permita cerrar sesión,
**Para** evitar que usuarios no autorizados accedan a pantallas administrativas o usen mi cuenta.

- **Criterios de Aceptación Funcionales:**
    1. Si un usuario intenta acceder a `/admin/*` sin tener el rol `ADMIN` en su token, debe ser redirigido a su dashboard o al login (Forbidden 403).
    2. Si el token ha expirado, cualquier petición debe redirigir automáticamente al Login.
    3. Botón "Cerrar Sesión" visible en el Navbar superior. Al hacer clic, elimina el token y lleva al Login.
- **🛠️ Instrucciones Técnicas para los Gems:**
    - **Frontend (Angular):** Implementar **Functional Guards** (`canActivateFn`).
        - `AuthGuard`: Verifica existencia de token.
        - `RoleGuard`: Decodifica el payload del JWT y verifica el claim `roles`.
    - **Backend (Spring):** Configurar `SecurityFilterChain`. Endpoints `/admin/**` requieren `hasAuthority('ADMIN')`.
    - **Seguridad:** El token debe tener un tiempo de expiración configurado (ej. 8 horas).

---

## ⏱️ ÉPICA: EXPERIENCIA DEL EMPLEADO (Zen Mode)

### HU-003: Dashboard Personal (Estado Inmediato)

**Como** Empleado,
**Quiero** visualizar inmediatamente mi estado actual (si estoy trabajando o no) y un resumen de mi puntualidad,
**Para** saber si debo marcar entrada o salida sin navegar por menús complejos.

- **Criterios de Aceptación Funcionales:**
    1. **Carga Inicial:** Al entrar, el sistema consulta el último registro de asistencia del día.
    2. **Indicador Visual:**
        - Si **NO** hay registro hoy: Mostrar estado "PENDIENTE" (Color Gris/Plata).
        - Si hay **ENTRADA** sin salida: Mostrar estado "EN JORNADA" (Color Verde #06402B) con un contador de tiempo transcurrido en vivo.
        - Si hay **SALIDA** marcada: Mostrar estado "JORNADA TERMINADA" (Color Azul #0A1128).
    3. **Gráfica Mini:** Mostrar un gráfico circular pequeño con el % de puntualidad del mes actual.
- **🛠️ Instrucciones Técnicas para los Gems:**
    - **Frontend:** Usar un componente `DashboardComponent`. Implementar el contador de tiempo usando `RxJS interval` o `Signal effects`.
    - **Backend:** Endpoint `GET /asistencia/estado-actual`.
    - **DB:** Query eficiente indexado por `id_usuario` y `fecha` (TRUNC(SYSDATE)).

### HU-004: Registro de Asistencia (Check-in/Check-out)

**Como** Empleado,
**Quiero** registrar mi entrada o salida pulsando un botón o escaneando un código,
**Para** dejar constancia de mis horas laborales cumpliendo las reglas de la empresa.

- **Criterios de Aceptación Funcionales:**
    1. **Botón Inteligente:** Un solo botón central que cambia de función dinámicamente (`MARCAR ENTRADA` o `MARCAR SALIDA`) según el estado actual.
    2. **Validaciones de Negocio:**
        - **Usuario Activo:** Solo usuarios con estado 'A' pueden marcar.
        - **Doble Marca:** Bloquear intentos de doble entrada el mismo día. Mostrar error amigable "Ya registraste tu entrada hoy".
    3. **Tolerancia:** El sistema debe calcular si es "Tardanza" basándose en la hora de configuración (ej. 08:00 AM + 15 min tolerancia).
    4. **Feedback:** Al marcar exitosamente, mostrar notificación Toast Verde. Si es tardanza, mostrar Toast Amarillo/Vino indicando "Registrado con Tardanza".
- **🛠️ Instrucciones Técnicas para los Gems:**
    - **DB (Núcleo):** Crear Stored Procedure `SP_REGISTRAR_ASISTENCIA`. Este SP maneja toda la lógica (INSERT/UPDATE, validación de horario, cálculo de estado P/T). Retorna códigos de error personalizados (-2000X) si falla.
    - **Backend:** Endpoint `POST /asistencia/checkin` y `/checkout`. Capturar `SQLException` y mapear a HTTP 409 o 200.
    - **QR:** Opción de activar cámara para leer un QR estático que contenga un token de ubicación (opcional según ).

### HU-005: Historial y Filtros de Asistencia

**Como** Empleado,
**Quiero** consultar mi historial de asistencias filtrando por rangos de fechas,
**Para** verificar mis horas y detectar faltas o tardanzas que debo justificar.

- **Criterios de Aceptación Funcionales:**
    1. Tabla de datos con columnas: Fecha, Hora Entrada, Hora Salida, Estado (Badge de color), Acciones.
    2. **Filtros:** Selector de "Fecha Inicio" y "Fecha Fin".
    3. **Visualización:**
        - Puntual = Badge Verde.
        - Tardanza = Badge Amarillo/Ámbar.
        - Falta/Ausencia = Badge Vino (#4A0404).
    4. **Acción:** Botón "Justificar" visible solo en filas con estado "Tardanza" o "Falta" que no tengan justificación previa.
- **🛠️ Instrucciones Técnicas para los Gems:**
    - **Frontend:** Componente tabla reutilizable. Paginación en cliente o servidor (preferible servidor si hay muchos datos). Pipe para formatear fechas.
    - **Backend:** Endpoint `GET /asistencia/historial?inicio=x&fin=y`.
    - **DB:** Cursor o Select simple sobre vista `V_ASISTENCIA_DETALLADA`.

### HU-006: Solicitud de Justificaciones

**Como** Empleado,
**Quiero** enviar una justificación para una tardanza o ausencia registrada,
**Para** corregir mi récord de asistencia y evitar descuentos.

- **Criterios de Aceptación Funcionales:**
    1. Al hacer clic en "Justificar" (desde HU-005), abrir un Modal elegante.
    2. **Formulario:**
        - Motivo (Select: Salud, Personal, Transporte).
        - Descripción (Textarea, obligatorio, mín 10 caracteres).
    3. **Persistencia:** Guardar la solicitud con estado "PENDIENTE".
    4. **Restricción:** No permitir crear otra justificación si ya existe una pendiente para esa misma fecha.
- **🛠️ Instrucciones Técnicas para los Gems:**
    - **Backend:** Endpoint `POST /asistencia/justificaciones_solicitud`.
    - **DB:** Insertar en tabla `JUSTIFICACION`. Trigger debe actualizar `LOG_JUSTIFICACION`

## 🏛️ ÉPICA: GESTIÓN ADMINISTRATIVA (God Mode)

### HU-007: Aprobación y Rechazo de Justificaciones

**Como** Administrador,
**Quiero** visualizar las justificaciones pendientes y decidir si aprobarlas o rechazarlas,
**Para** cerrar las incidencias de asistencia y mantener la disciplina en el equipo.

- **Criterios de Aceptación Funcionales:**
    1. **Bandeja de Entrada:** Una vista filtrada que muestre por defecto solo las solicitudes con estado "PENDIENTE".
    2. **Detalle:** Al seleccionar una solicitud, ver el motivo completo, fecha y empleado.
    3. **Acciones:**
        - **Aprobar (Verde):** Cambia el estado de la justificación a 'APROBADO' y actualiza el estado de la asistencia asociada (ej. de 'Falta' a 'Justificada').
        - **Rechazar (Vino):** Cambia el estado a 'RECHAZADO' y mantiene la falta/tardanza original.
    4. **Auditoría:** El sistema debe registrar automáticamente quién aprobó/rechazó y cuándo en `LOG_JUSTIFICACION`.
- **🛠️ Instrucciones Técnicas para los Gems:**
    - **DB:** Stored Procedure `SP_GESTIONAR_JUSTIFICACION(p_id, p_estado, p_admin_id)`. Debe ser transaccional (actualizar ambas tablas o ninguna).
    - **Backend:** Endpoint `PUT /asistencia/justificaciones/{id}/resolucion`.
    - **Front:** Tabla con acciones rápidas (iconos Check/X).

### HU-008: Configuración Dinámica de Reglas (Tolerancia)

**Como** Administrador,
**Quiero** modificar los parámetros globales del sistema (hora de entrada, minutos de tolerancia),
**Para** adaptar el control de asistencia a cambios operativos sin depender de un programador.

- **Criterios de Aceptación Funcionales:**
    1. Formulario de configuración accesible solo para el rol ADMIN.
    2. Campos editables:
        - Hora Inicio Jornada (ej. 08:00).
        - Minutos de Tolerancia (ej. 15).
    3. **Persistencia:** Al guardar, los cambios deben aplicarse inmediatamente para los siguientes registros de asistencia.
- **🛠️ Instrucciones Técnicas para los Gems:**
    - **DB:** Tabla `CONFIGURACION` (Clave-Valor).
    - **Backend:** Servicio `ConfigService` que lea estos valores y los inyecte en la lógica de validación o que el SP de Oracle los lea directamente.

### HU-009: Reportes de Asistencia Filtrados

**Como** Administrador,
**Quiero** consultar reportes detallados filtrando por empleado, rango de fechas o departamento,
**Para** analizar el cumplimiento laboral en periodos específicos.

- **Criterios de Aceptación Funcionales:**
    1. **Filtros Combinados:** Poder seleccionar "Empleado X" + "Mes Enero" + "Solo Tardanzas".
    2. **Visualización en Pantalla:** Tabla de resultados rápida, paginada.
    3. **Performance:** La consulta no debe congelar el navegador aunque traiga 1000 registros.
- **🛠️ Instrucciones Técnicas para los Gems:**
    - **DB:** Uso obligatorio de **Cursores (SYS_REFCURSOR)** dentro de un Paquete `PKG_REPORTES` para iterar eficientemente sobre los datos.
    - **Backend:** Mapear el cursor de Oracle a una lista de DTOs Java.

---

## 👥 ÉPICA: GESTIÓN DE USUARIOS (The Creator)

### HU-010: CRUD de Usuarios y Control de Acceso (Activo/Inactivo)

**Como** Administrador,
**Quiero** registrar nuevos empleados y desactivar a los que ya no trabajan,
**Para** asegurar que solo el personal autorizado y vigente pueda registrar asistencia.

- **Criterios de Aceptación Funcionales:**
    1. **Alta de Usuario:** Formulario con Nombre, Apellido, Email, DNI, Rol (Admin/Empleado).
    2. **Validación de Duplicados:** No permitir crear dos usuarios con el mismo `username` o `documento`.
    3. **Regla de Oro (Switch Activo/Inactivo):**
        - Un usuario marcado como "Inactivo" **NO** puede iniciar sesión ni registrar asistencia.
        - Visualmente, los inactivos se ven con opacidad reducida en la lista.
    4. **Contraseña:** Generación de contraseña inicial o hash seguro.
- **🛠️ Instrucciones Técnicas para los Gems:**
    - **DB:** Columna `estado CHAR(1) DEFAULT 'A'`. Constraint Check (`IN ('A', 'I')`). Índices en `id_usuario`.
    - **Front:** Switch toggle (Verde/Gris) en la tabla de usuarios para cambiar estado rápidamente.

---

## 📊 ÉPICA: INTELIGENCIA Y EXPORTACIÓN (Strategic Vision)

### HU-011: Dashboard Analítico y Monitor en Vivo

**Como** Administrador,
**Quiero** ver métricas gráficas de puntualidad y quién está presente en tiempo real,
**Para** tener una visión estratégica del comportamiento de la empresa.

- **Criterios de Aceptación Funcionales:**
    1. **Monitor en Vivo:** Lista o Grid de empleados que muestra su estado *ahora mismo* (Online/Offline) basado en si marcaron entrada pero no salida.
    2. **Gráficos (Charts):**
        - *Torta:* % Puntualidad vs Tardanzas vs Faltas (Global del mes).
        - *Barras:* Asistencias por día de la semana.
    3. **Estética:** Los gráficos deben usar la paleta del sistema (Verdes, Vinos, Grises) sobre fondo oscuro.
- **🛠️ Instrucciones Técnicas para los Gems:**
    - **Front:** Librería de gráficos (`Ngx-Charts` o `ECharts`). Componente "LiveMonitor" con actualización automática (polling cada 60s o Signals).
    - **DB:** Vistas (`VIEW`) pre-calculadas para no saturar la BD con cálculos matemáticos en cada renderizado.

### HU-012: Exportación de Estadísticas (Excel/PDF)

**Como** Administrador,
**Quiero** descargar los reportes y estadísticas en formato Excel o PDF profesional,
**Para** presentar informes oficiales fuera del sistema.

- **Criterios de Aceptación Funcionales:**
    1. Botón "Exportar" visible en las vistas de Reportes y Dashboard.
    2. **Formato Excel (.xlsx):** Debe generar un archivo estructurado con encabezados en negrita, celdas de fechas con formato correcto y filtros automáticos activados.
    3. **Formato PDF (.pdf):** Debe generar un documento imprimible con el logo de la empresa, título del reporte, fecha de generación y la tabla de datos bien paginada.
    4. **Descarga Directa:** El navegador debe iniciar la descarga del archivo binario, no abrir una pestaña nueva con HTML.
- **🛠️ Instrucciones Técnicas para los Gems:**
    - **Backend:** Crear servicio `ExportService`.
        - Para Excel: Usar **Apache POI**.
        - Para PDF: Usar **iText** o **OpenPDF**.
    - **Endpoint:** `GET /reportes/export?type=pdf` retornando `ResponseEntity<byte[]>`.