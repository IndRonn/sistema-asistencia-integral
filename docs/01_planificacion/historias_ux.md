## 🚪 UX-001: La Experiencia de Acceso (Login)

**Concepto:** "La Entrada al Búnker". Debe transmitir seguridad inmediata. No es una red social, es un sistema de control.

- **Paleta Específica:**
    - **Fondo:** `NEGRO (#000000)` Absoluto.
    - **Tarjeta (Card):** `AZUL NOCTURNO (#0A1128)` muy oscuro o degradado sutil a negro.
    - **Bordes:** `PLATA (#BCC6CC)` (Opacidad 20%).
    - **Botón:** `VERDE (#06402B)` con texto `BLANCO` (Bold).
- **Comportamiento & Interacción:**
    1. **Enfoque (Focus):** Los campos de texto (Usuario/Contraseña) no son cajas blancas brillantes. Son campos con fondo transparente y una línea inferior gris. Al hacer clic, la línea se "enciende" en **Verde (#06402B)** y el label flota hacia arriba suavemente.
    2. **Validación Sensorial:** Si el usuario se equivoca, la tarjeta no muestra un mensaje genérico rojo estándar. La tarjeta vibra (animación `shake`) y el borde se tiñe momentáneamente de **Vino (#4A0404)**.
    3. **Transición:** Al loguearse con éxito, no hay un "parpadeo" blanco. El formulario se desvanece (`fade-out`) y el dashboard aparece suavemente (`fade-in`).
- **Justificación de Usabilidad:** El alto contraste entre el fondo negro y el texto plata asegura legibilidad sin cansar la vista (Dark Mode nativo).

---

## 🧭 UX-002: Navegación y Estructura (Layout)

**Concepto:** "Jerarquía Visual". El usuario nunca debe preguntarse "¿dónde estoy?".

- **Diferenciación de Roles (UX Clave):**
    - **Para el Admin (El Estratega):** Sidebar Vertical (Barra lateral).
        - **Color:** `AZUL NOCTURNO (#0A1128)`.
        - **Motivo:** Transmite profundidad y control. Permite tener muchas opciones (Reportes, Usuarios, Config) organizadas verticalmente.
        - **Estado Activo:** El ítem seleccionado tiene un fondo ligeramente más claro y una barra vertical **Plata** a la izquierda.
    - **Para el Empleado (El Ejecutor):** Topbar Horizontal (Barra superior).
        - **Color:** `NEGRO (#000000)` con borde inferior `PLATA`.
        - **Motivo:** El empleado necesita espacio de pantalla para trabajar. La navegación arriba libera el resto de la pantalla para el "Botón de Acción".
- **Tipografía:**
    - Títulos: `Serif` (Elegante, autoritaria).
    - Datos/Tablas: `Sans-Serif` (Limpia, legible).
    - Números: `Monospace` (Para que las cifras en tablas se alineen perfectamente).

---

## 🟢 UX-003: El Botón Maestro (Interacción Principal)

**Concepto:** "Ley de Fitts". El elemento más importante debe ser el más grande y fácil de acceder.

- **El Problema a Resolver:** En muchas apps, el botón de "Marcar" es pequeño y se pierde. Aquí será el protagonista.
- **Diseño del Componente (Dashboard Empleado):**
    - Un botón circular o cuadrado redondeado grande (mínimo 200px) en el centro de la pantalla.
    - **Estado "Por Entrar":**
        - Fondo: Transparente.
        - Borde: `VERDE (#06402B)` (Grosor 2px).
        - Efecto: Un brillo sutil (Glow) pulsante que invita al clic.
        - Texto: "INICIAR JORNADA".
    - **Estado "Trabajando":**
        - Fondo: `VERDE (#06402B)` Sólido.
        - Texto: "EN CURSO".
        - **Feedback:** Un contador de tiempo digital justo debajo.
    - **Estado "Por Salir":**
        - Al hacer Hover sobre el estado "Trabajando", el botón cambia a color `VINO (#4A0404)`.
        - Texto: "TERMINAR JORNADA".
        - *¿Por qué?* Para evitar clics accidentales de salida. El cambio de color advierte: "Vas a detener tu tiempo".

---

## 🔔 UX-004: Sistema de Feedback (Notificaciones)

**Concepto:** "Comunicación Respetuosa". El sistema informa, no grita.

- **Adiós a los `alert()` del navegador:** Son intrusivos y feos.
- **Componente Toast (Notificación Flotante):**
    - Ubicación: Esquina superior derecha.
    - Animación: Deslizar desde la derecha (`Slide-in`).
    - Duración: 3 segundos.
- **Código de Color Semántico:**
    - **Éxito (Check-in OK):** Fondo Negro, Icono Check `VERDE`, Borde izquierdo `VERDE (#06402B)`.
    - **Advertencia (Tardanza):** Fondo Negro, Icono Reloj `AMARILLO/DORADO` (usaremos un tono ocre para que combine con lo dark), Borde izquierdo `AMARILLO`.
        - *Nota:* Aquí rompemos un poco la paleta estricta por usabilidad: el amarillo es universal para "Advertencia". Usaremos un Ocre Dorado (`#C5A000`) para mantener la elegancia.
    - **Error (Falta/Error Técnico):** Fondo Negro, Icono X `VINO`, Borde izquierdo `VINO (#4A0404)`.

---

## 📉 UX-005: Estados de Carga (Skeleton Screens)

**Concepto:** "Percepción de Velocidad".

- **El Problema:** Una pantalla en blanco o un spinner girando genera ansiedad ("¿Se colgó?").
- **La Solución:** Skeleton Loading.
    - Mientras los datos de Oracle viajan al Front (Angular), dibujamos cajas grises (`#1F2937`) que pulsan suavemente (`animate-pulse` de Tailwind).
    - Imitan la forma de la tabla o del gráfico que va a aparecer.
    - **Psicología:** Esto hace que la app se sienta instantánea, aunque la BD tarde 500ms. Mantiene el **Orden** visual.

## 📊 UX-006: Tablas de Datos "High-End" (El Archivo)

**Concepto:** "Legibilidad Quirúrgica". Las tablas suelen ser feas. Las tuyas serán impecables.

- **Estética Visual:**
    - **Encabezados (Header):** Fondo `AZUL NOCTURNO (#0A1128)`. Texto `PLATA (#BCC6CC)` en mayúsculas, fuente pequeña (`text-xs`), con espaciado amplio (`tracking-wider`). Esto da aire de formalidad.
    - **Filas (Body):** Fondo `NEGRO (#000000)`.
    - **Separadores:** En lugar de bordes duros en cada celda, usaremos solo líneas horizontales muy finas y oscuras (`border-b border-gray-800`) entre filas.
- **Interacción (Hover):**
    - Al pasar el mouse por una fila, esta no se pone gris claro. Se ilumina con un **resplandor lateral** (borde izquierdo de 2px color `VERDE`) y el fondo cambia sutilmente a un carbón muy oscuro (`#111`).
    - **Efecto "Fantasma":** Los botones de acción (Editar, Borrar) están ocultos (opacidad 0) y solo aparecen cuando el mouse está sobre la fila. Esto reduce el ruido visual.
- **Badges de Estado (Píldoras):**
    - No usar colores planos chillones. Usar fondos translúcidos:
        - *Puntual:* Fondo Verde (10% opacidad), Texto Verde brillante.
        - *Falta:* Fondo Vino (10% opacidad), Texto Vino.
        - *Justificado:* Fondo Azul (10% opacidad), Texto Azul.

---

## 📈 UX-007: Visualización de Métricas (La Estrategia)

**Concepto:** "Datos sobre el Vacío". Gráficos que flotan en la oscuridad.

- **Configuración de Gráficos (Admin Dashboard):**
    - **Fondo del Chart:** Transparente (para que tome el negro de la app).
    - **Ejes y Grillas:**
        - Eliminar las líneas de cuadrícula verticales.
        - Líneas horizontales muy tenues (`#333`) o punteadas.
        - Texto de los ejes en color Gris Oscuro (para que no distraiga).
    - **Colores de Datos:**
        - *Serie Asistencia:* No usar el verde oscuro del logo (no se vería). Usar un **Gradiente Vertical**: De Verde Esmeralda arriba a transparente abajo.
        - *Serie Faltas:* Línea sólida color `VINO (#4A0404)`.
    - **Tooltip (Al tocar un dato):**
        - Cuadro negro flotante con sombra fuerte (`shadow-2xl`). Borde fino `PLATA`. Datos en blanco.
- **Empty States (Sin Datos):**
    - Si no hay datos para un gráfico, nunca mostrar un cuadro blanco vacío. Mostrar una ilustración minimalista lineal en gris oscuro con el texto: "Esperando datos del periodo...".

---

## 🎛️ UX-008: Modales y Formularios de Configuración

**Concepto:** "Enfoque Profundo". Cuando configuras algo, el resto del mundo desaparece.

- **El Telón (Backdrop):**
    - Al abrir un modal (ej. "Justificar Tardanza" o "Configurar Tolerancia"), el fondo de la pantalla no solo se oscurece. Aplicamos un **`backdrop-blur-sm`** (desenfoque) sobre el contenido trasero. Esto pone al usuario en "Modo Foco".
- **La Ventana Modal:**
    - Bordes afilados ( `rounded-none` o `rounded-sm`).
    - Borde superior grueso de color: **Verde** (si es creación), **Vino** (si es rechazo/borrado), **Azul** (si es info).
    - Botones del pie de página alineados a la derecha:
        - *Cancelar:* Texto gris, sin fondo. Hover: Blanco.
        - *Confirmar:* Botón sólido (Verde/Vino según contexto).
- **Switch Toggle (Para Usuarios Activos/Inactivos):**
    - Un interruptor elegante.
    - *Activo:* Fondo Verde oscuro, círculo desplazado a la derecha.
    -

    *Inactivo:* Fondo Gris oscuro, círculo a la izquierda.


---

## 📱 UX-009: Adaptabilidad Móvil (Pocket Authority)

**Concepto:** "Control en la Palma". El sistema debe ser usable en el celular del empleado sin perder clase.

- **Transformación de Tablas (Card View):**
    - En móvil, las tablas anchas (Historial) se rompen.
    - **Solución:** Usar CSS Grid para transformar cada fila de la tabla en una **Tarjeta (Card)** vertical apilada.
        - Fecha y Estado arriba.
        - Detalles (Hora entrada/salida) en el medio.
    - Esto evita el scroll horizontal horrible.
- **Navegación Móvil:**
    - El Sidebar del Admin desaparece. Se convierte en un **Menú Hamburguesa** minimalista que despliega una cortina lateral negra completa (`w-full`).
- **Touch Targets:**
    - En móvil, los botones aumentan su altura a `48px` mínimo para ser fáciles de tocar con el dedo pulgar.

---

## 📤 UX-010: Experiencia de Exportación

**Concepto:** "Entrega Profesional".

- **El Botón de Descarga:**
    - Ubicado cerca de los filtros. Icono discreto (flecha hacia abajo o archivo).
    - Estilo: `Outline` (Fondo transparente, Borde Plata).
- **Feedback de Proceso:**
    - Generar un PDF toma tiempo (1-3 segundos).
    - Al hacer clic, el icono del botón cambia a un **pequeño círculo de carga** (spinner) y el texto cambia a "Generando...".
    - Al terminar, el navegador inicia la descarga y el botón vuelve a la normalidad mostrando un check verde temporalmente ("✓ Listo")

### 🚀 EL EMPUJÓN FINAL (EXTRA MILE REAL)

### HU-013: El "Limpiador" Automático (Oracle Scheduler Job)

*El problema:* ¿Qué pasa si un empleado se olvida de marcar la salida? El registro queda abierto "para siempre" y rompe los cálculos de horas.

- *La Solución Mediocre:* Dejarlo así o arreglarlo a mano.
- *La Solución Ronny:* Un **Job Automático de Base de Datos**.

**Como** Sistema (Automático),
**Quiero** ejecutar un proceso nocturno (23:59 PM),
**Para** cerrar forzosamente las asistencias que quedaron abiertas y marcarlas como "Sin marcar salida".

- **Criterios de Aceptación:**
    1. Proceso automático (sin intervención humana).
    2. Busca registros del día con `hora_entrada` pero `hora_salida` NULL.
    3. Actualiza `hora_salida` a las 23:59:59.
    4. Establece un estado especial o nota: "Cierre Automático por Sistema".
- **Impacto en Evaluación:** Demuestra uso de **`DBMS_SCHEDULER`** o Jobs en Oracle. Eso es nivel Senior. Casi nadie usa Jobs en proyectos universitarios/finales.
- **Costo de Desarrollo:** Bajo. Es 100% PL/SQL. 0% Frontend.

### HU-014: Auditoría de Seguridad (Huella Digital)

*El problema:* El PDF pide "Seguridad" (15%). Login con JWT es lo estándar. Vamos más allá.

- *La Solución:* Registrar **desde dónde** entra la gente.

**Como** Administrador de Seguridad,
**Quiero** registrar la Dirección IP y el Navegador (User-Agent) cada vez que alguien inicia sesión,
**Para** detectar accesos sospechosos (ej. alguien logueándose desde otro país o dispositivo raro).

- **Criterios de Aceptación:**
    1. Al hacer Login, el backend captura la IP (`request.getRemoteAddr()`) y el `User-Agent`.
    2. Guardar esto en una tabla `LOG_SESION` (Usuario, Fecha, IP, Navegador, Resultado).
    3. **Visualización (Admin):** En el detalle del usuario, mostrar "Último acceso: 12/12/2025 desde Chrome en IP 192.168.1.5".
- **Impacto en Evaluación:** Seguridad Proactiva. Cumple con exceso el criterio de seguridad.
- **Costo de Desarrollo:** Muy bajo. Solo es capturar headers en el Controller y guardar en tabla.