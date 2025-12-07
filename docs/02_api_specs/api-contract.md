# 🔌 Contrato de API (API Specs) - Master
**Versión:** 1.0.0
**Protocolo:** RESTful JSON
**Estándar de Fechas:** ISO-8601 (`YYYY-MM-DD` para fechas, `HH:mm:ss` para horas).
**Seguridad:** Header `Authorization: Bearer <token>` requerido en todos los endpoints privados.

---

## 🛑 0. ESTÁNDARES GLOBALES (Cross-Cutting)

### 0.1 Respuesta Estándar de Error
*El Backend debe capturar todas las excepciones y retornarlas en este formato.*

**Schema JSON:**
```json
{
  "timestamp": "2025-12-10T10:15:30",
  "status": 409,
  "code": "ASIS-002", // Código de Negocio
  "message": "Ya has registrado tu salida hoy.",
  "path": "/api/v1/asistencia/checkin"
}
````

### 0.2 Estándar de Paginación (Spring Data)

*Para tablas grandes (Historial, Usuarios).*
**Query Params:** `?page=0&size=10&sort=fecha,desc`

**Response Wrapper:**

```json
{
  "content": [ ... ], // Array de objetos
  "pageable": { ... },
  "totalElements": 50,
  "totalPages": 5,
  "last": false
}
```

-----

## 🔐 1. MÓDULO DE AUTENTICACIÓN (The Gate)

*Soporta HU-001 y HU-002.*

### 1.1 Iniciar Sesión

**Endpoint:** `POST /auth/login`
**Acceso:** Público.
**Descripción:** Valida credenciales contra BD, verifica estado 'A' y retorna JWT.

**Request:**

```json
{
  "username": "ronny.mendez",  // @NotBlank
  "password": "password123"    // @NotBlank
}
```

**Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1Ni...",
  "type": "Bearer",
  "usuario": {
    "id": 1,
    "username": "ronny.mendez",
    "nombreCompleto": "Ronny Méndez",
    "email": "ronny@indra.com",
    "rol": "ADMIN" // Valores: 'ADMIN' | 'EMPLEADO'
  }
}
```

**Errores Comunes:**

* `401 Unauthorized` - Credenciales incorrectas o Usuario Inactivo (Code: `AUTH-001`).

### 1.2 Verificar Token / Obtener Perfil

**Endpoint:** `GET /auth/me`
**Acceso:** Privado (Cualquier Rol).
**Descripción:** Permite al Frontend recargar la página y saber quién sigue logueado sin pedir credenciales de nuevo.

**Response (200 OK):**
*(Misma estructura que el objeto "usuario" del login).*

-----

## 👥 2. MÓDULO DE USUARIOS (Gestión Admin)

*Soporta HU-010. Solo accesible para ROL: ADMIN.*

### 2.1 Listar Empleados

**Endpoint:** `GET /usuarios`
**Query Params:** `?search=ronny` (Filtra por nombre o dni)
**Acceso:** ADMIN.

**Response (200 OK):**

```json
{
  "content": [
    {
      "idUsuario": 5,
      "username": "juan.perez",
      "nombres": "Juan",
      "apellidos": "Pérez",
      "email": "juan@mapfre.com",
      "rol": "EMPLEADO",
      "estado": "A" // 'A' | 'I' (Front usa esto para opacidad)
    }
  ],
  "totalElements": 1
}
```

### 2.2 Crear Usuario

**Endpoint:** `POST /usuarios`
**Acceso:** ADMIN.

**Request:**

```json
{
  "username": "maria.gomez",
  "password": "temporal123",
  "nombres": "Maria",
  "apellidos": "Gomez",
  "email": "maria@mapfre.com",
  "rol": "EMPLEADO"
}
```

**Response (201 Created):**
*(Retorna el objeto creado con su ID).*

**Errores Comunes:**

* `409 Conflict` - Username o Email ya existen (Code: `USR-001`).

### 2.3 Cambiar Estado (Activar/Desactivar)

**Endpoint:** `PATCH /usuarios/{id}/estado`
**Acceso:** ADMIN.
**Descripción:** Switch rápido para bloquear acceso (Soft Delete).

**Request:**

```json
{
  "estado": "I" // Enviar 'A' o 'I'
}
```

**Response (200 OK):**

```json
{
  "mensaje": "Estado actualizado correctamente",
  "nuevoEstado": "I"
}
```

...

## ⏱️ 3. MÓDULO DE ASISTENCIA (Operativo - Empleado)
*Soporta HU-003, HU-004, HU-005 y HU-006.*

### 3.1 Obtener Estado Actual (Dashboard Zen)
**Endpoint:** `GET /asistencia/estado-actual`
**Acceso:** EMPLEADO.
**Descripción:** Consulta vital que se ejecuta al cargar el Dashboard. Determina qué botón mostrar y si activar el cronómetro.

**Response (200 OK) - Escenario 1: Nuevo día (Sin marcas):**
```json
{
  "estado": "SIN_MARCAR",        // Frontend pinta botón "INICIAR JORNADA" (Verde borde)
  "mensaje": "Listo para iniciar",
  "horaEntrada": null,
  "ultimoRegistro": null
}
```
**Response (200 OK) - Escenario 2: Trabajando (Reloj corriendo):**

```json
{
  "estado": "EN_JORNADA",        // Frontend pinta botón "TERMINAR JORNADA" (Rojo/Vino)
  "mensaje": "Jornada en curso",
  "horaEntrada": "08:15:20",     // Vital para inicializar el contador en el Front
  "esTardanza": true             // Frontend muestra badge amarillo "Tardanza"
}
```

**Response (200 OK) - Escenario 3: Día Cerrado:**

```json
{
  "estado": "FINALIZADO",        // Frontend pinta botón Deshabilitado o "Ver Resumen"
  "mensaje": "Jornada finalizada",
  "horaEntrada": "08:00:00",
  "horaSalida": "17:30:00"
}
```

-----

### 3.2 Registrar Marca (El Botón Maestro)

**Endpoint:** `POST /asistencia/marcar`
**Acceso:** EMPLEADO.
**Descripción:** Endpoint único e inteligente. El Backend (vía SP Oracle) decide si es Entrada o Salida.

**Request Body:**
*(Vacío. La identidad viene del Token JWT).*

```json
{}
```

**Response (201 Created) - Éxito:**

```json
{
  "mensaje": "Entrada registrada con éxito",
  "tipoMarca": "ENTRADA",      // Valores: 'ENTRADA' | 'SALIDA'
  "horaExacta": "08:05:00",
  "estadoAsistencia": "T"      // 'P'=Puntual, 'T'=Tarde (Front usa esto para el color del Toast)
}
```

**Errores de Negocio (409 Conflict):**

* `ASIS-001`: "Jornada ya cerrada. No puedes marcar de nuevo hoy."
* `ASIS-002`: "Usuario inactivo no puede marcar."

-----

### 3.3 Historial de Asistencias (Grilla)

**Endpoint:** `GET /asistencia/historial`
**Acceso:** EMPLEADO.
**Query Params:**

* `fechaInicio` (YYYY-MM-DD)
* `fechaFin` (YYYY-MM-DD)
* `page` (Default 0)
* `size` (Default 10)

**Response (200 OK):**

```json
{
  "content": [
    {
      "idAsistencia": 105,
      "fecha": "2025-12-05",
      "horaEntrada": "08:00:00",
      "horaSalida": "18:00:00",
      "estado": "P",               // 'P'=Puntual (Badge Verde)
      "esJustificable": false      // Front deshabilita botón "Justificar"
    },
    {
      "idAsistencia": 104,
      "fecha": "2025-12-04",
      "horaEntrada": "08:45:00",
      "horaSalida": null,          // Día incompleto
      "estado": "T",               // 'T'=Tarde (Badge Amarillo)
      "esJustificable": true       // Front habilita botón "Justificar"
    },
    {
      "idAsistencia": null,        // Caso especial: Falta total (sin registro)
      "fecha": "2025-12-03",
      "horaEntrada": null,
      "horaSalida": null,
      "estado": "A",               // 'A'=Ausente (Badge Vino)
      "esJustificable": true
    }
  ],
  "totalElements": 3,
  "totalPages": 1
}
```

-----

### 3.4 Solicitar Justificación

**Endpoint:** `POST /asistencia/justificaciones`
**Acceso:** EMPLEADO.
**Descripción:** Crea una solicitud para revisión del Admin.

**Request Body:**

```json
{
  "idAsistencia": 104,               // Null si es para una falta total
  "fecha": "2025-12-04",             // Obligatorio
  "motivo": "Cita médica en ESSALUD",// Mínimo 10 caracteres
  "tipo": "SALUD"                    // Enum: 'SALUD', 'PERSONAL', 'TRABAJO'
}
```

**Response (201 Created):**

```json
{
  "mensaje": "Solicitud enviada correctamente",
  "idJustificacion": 55,
  "estado": "PENDIENTE"
}
```

**Errores Comunes:**

* `JUST-001`: "Ya existe una solicitud pendiente para esta fecha."
* `JUST-002`: "No se puede justificar una fecha futura."

<!-- end list -->


-----


## 🏛️ 4. MÓDULO ADMINISTRATIVO (Gestión y Gobierno)
*Soporta HU-007, HU-008 y HU-010. Acceso exclusivo: ADMIN.*

### 4.1 Listar Justificaciones Pendientes
**Endpoint:** `GET /admin/justificaciones`
**Query Params:** `?estado=PENDIENTE` (Default)
**Descripción:** Bandeja de entrada para que el Admin resuelva incidencias.

**Response (200 OK):**

```json
{
  "content": [
    {
      "idJustificacion": 55,
      "empleado": "Juan Pérez",
      "fecha": "2025-12-04",
      "motivo": "Cita médica en ESSALUD",
      "tipo": "SALUD",
      "estado": "PENDIENTE",
      "fechaSolicitud": "2025-12-04T10:00:00"
    }
  ],
  "totalElements": 1
}
````

### 4.2 Resolver Justificación (Aprobar/Rechazar)

**Endpoint:** `PUT /admin/justificaciones/{id}/resolucion`
**Descripción:** Al aprobar, el sistema dispara internamente la actualización de la asistencia (cambia falta por justificada).

**Request Body:**

```json
{
  "estado": "APROBADO",    // Valores: 'APROBADO' | 'RECHAZADO'
  "comentario": "Documento médico validado." // Opcional
}
```

**Response (200 OK):**

```json
{
  "mensaje": "Resolución aplicada correctamente",
  "nuevoEstado": "APROBADO",
  "asistenciaActualizada": true
}
```

### 4.3 Configuración del Sistema (Reglas)

**Endpoint:** `GET /admin/configuracion`
**Response (200 OK):**

```json
[
  { "clave": "HORA_ENTRADA", "valor": "08:00", "descripcion": "Inicio Jornada" },
  { "clave": "TOLERANCIA_MINUTOS", "valor": "15", "descripcion": "Margen T" }
]
```

**Endpoint:** `PUT /admin/configuracion`
**Request Body:**

```json
[
  { "clave": "TOLERANCIA_MINUTOS", "valor": "20" }
]
```

**Response (200 OK):** `{"mensaje": "Configuración actualizada"}`

-----

## 📊 5. REPORTES E INTELIGENCIA (Dashboard)

*Soporta HU-009 y HU-011.*

### 5.1 KPIs del Dashboard (Métricas)

**Endpoint:** `GET /admin/dashboard/kpis`
**Descripción:** Datos para los gráficos de torta y tarjetas de resumen.

**Response (200 OK):**

```json
{
  "totalEmpleados": 50,
  "presentesHoy": 42,
  "ausentesHoy": 8,
  "tasaPuntualidad": 85.5, // Porcentaje para gráfico circular
  "distribucionAsistencia": {
    "PUNTUAL": 35,
    "TARDE": 7,
    "FALTA": 8
  }
}
```

### 5.2 Monitor en Vivo (Live Feed)

**Endpoint:** `GET /admin/dashboard/live`
**Descripción:** Lista rápida de quién está en la oficina AHORA MISMO.

**Response (200 OK):**

```json
[
  {
    "idUsuario": 5,
    "nombre": "Juan Pérez",
    "horaEntrada": "07:55:00",
    "estado": "ONLINE", // Verde (Marcó entrada, no salida)
    "ip": "192.168.1.50"
  },
  {
    "idUsuario": 8,
    "nombre": "Maria Gomez",
    "horaEntrada": null,
    "estado": "OFFLINE", // Gris (No ha marcado)
    "ip": null
  }
]
```

### 5.3 Reporte Detallado (Filtros Avanzados)

**Endpoint:** `GET /admin/reportes`
**Query Params:** `?inicio=2025-01-01&fin=2025-01-31&idEmpleado=5`

**Response (200 OK):**
*(Retorna estructura similar a `/asistencia/historial` pero enriquecida con datos del empleado).*

-----

## 📤 6. EXPORTACIÓN (Archivos)

*Soporta HU-012.*

### 6.1 Descargar Reporte

**Endpoint:** `GET /admin/exportar`
**Query Params:** `?formato=EXCEL&inicio=...&fin=...`
**Headers:**

* Accept: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` (Para Excel)
* Accept: `application/pdf` (Para PDF)

**Response (200 OK):**

* **Body:** Stream binario (Blob).
* **Header:** `Content-Disposition: attachment; filename="reporte_asistencia_2025.xlsx"`

-----

