# Catálogo de requisitos de calidad · ISO/IEC 25010:2023

| | |
|---|---|
| Versión | 0.1 (propuesta) |
| Fecha | 2026-11-10 |
| Estado | **Por validar** por Felipe. Nada de este documento está aprobado. |
| Origen | Documento nuevo. Los documentos v0.1 citados en el contexto del proyecto no se encontraron en GitHub ni en el equipo de trabajo, así que este catálogo no deriva de ellos. |

## 1. Para qué sirve

Convierte las nueve características de calidad de producto de ISO/IEC 25010:2023 en requisitos no funcionales (RNF) que se pueden medir, probar y evidenciar. Mencionar la norma no demuestra nada: cada fila dice qué se mide, con qué umbral y cómo se comprueba.

**Este catálogo no afirma que CheckHelph cumpla ni esté certificado en ISO/IEC 25010.** Es un plan de verificación.

## 2. Cómo leer las tablas

- **ID:** `RNF-<característica>-<número>`. Los IDs no se reutilizan ni se renumeran.
- **Umbral:** valor propuesto. Los marcados con (?) son los que más conviene revisar, porque dependen de decisiones abiertas.
- **Aplica a:** `MVP` = prototipo `mvp-web/index.html`; `Piloto` = versión con backend para un local.
- **Estado:** todos parten en *Por validar*. La columna "Resultado" solo se llena cuando existe una ejecución registrada en `docs/quality/evidence/`.
- **Responsable de validación:** Felipe en todos los casos, salvo que se indique otro.

## 3. Requisitos por característica

### 3.1 Adecuación funcional

| ID | Subcaracterística | Contexto de uso | Umbral medible | Método de prueba | Evidencia | Aplica a | Resultado |
|---|---|---|---|---|---|---|---|
| RNF-FUN-01 | Completitud funcional | Cualquier historia que entra a una entrega | El 100 % de las historias de la entrega tiene al menos un criterio de aceptación verificable y un caso de prueba asociado | Revisión de la matriz de trazabilidad antes de cerrar la entrega | Matriz de trazabilidad con columnas completas | MVP, Piloto | No ejecutado |
| RNF-FUN-02 | Corrección funcional | Momento del turno según día y hora | 0 errores en los casos de borde: 08:29, 08:30, 11:59, 12:00, 17:59, 18:00, 20:59, 21:00, 21:29 y 21:30, en lunes, martes, viernes, sábado y último día del mes | Pruebas unitarias automatizadas | Reporte de pruebas | MVP, Piloto | Parcial: ver EV-002 |
| RNF-FUN-03 | Corrección funcional | Búsqueda de Preguntas | Sobre un set acordado de 20 consultas de prueba, la respuesta esperada aparece entre las 3 primeras sugerencias en al menos 18 (90 %) (?) | Prueba automatizada con el set versionado | Reporte con aciertos por consulta | MVP, Piloto | No ejecutado |
| RNF-FUN-04 | Pertinencia funcional | Trabajador con una duda durante el turno | Llega a una respuesta existente en 3 interacciones o menos desde Inicio (?) | Recorrido guiado con 5 personas del equipo | Planilla de observación sin datos personales | Piloto | No ejecutado |

### 3.2 Eficiencia de desempeño

| ID | Subcaracterística | Contexto de uso | Umbral medible | Método de prueba | Evidencia | Aplica a | Resultado |
|---|---|---|---|---|---|---|---|
| RNF-PER-01 | Comportamiento temporal | Primera carga en teléfono de gama media con red móvil | LCP ≤ 2,5 s en el percentil 75, con perfil "Slow 4G" de Lighthouse (?) | Lighthouse en modo móvil, 5 ejecuciones, se registra la mediana | Reporte Lighthouse exportado | MVP, Piloto | No ejecutado |
| RNF-PER-02 | Utilización de recursos | Primera carga | Peso transferido inicial ≤ 300 KB comprimido, sin contar fotos (?) | Panel Red del navegador o Lighthouse | Captura del resumen de red | MVP, Piloto | No ejecutado (el MVP pesa 62 KB sin comprimir; falta medir transferencia real) |
| RNF-PER-03 | Comportamiento temporal | Autocompletado mientras se escribe | Las sugerencias aparecen en ≤ 100 ms tras cada tecla con 500 preguntas cargadas (?) | Medición con `performance.now()` en prueba automatizada | Reporte de tiempos | MVP, Piloto | No ejecutado |
| RNF-PER-04 | Comportamiento temporal | Operaciones de la API (listar tareas, buscar, crear ticket) | p95 ≤ 500 ms medido en el servidor con datos de un local (?) | Prueba de carga ligera con 20 usuarios simultáneos | Reporte de la herramienta de carga | Piloto | No ejecutado |
| RNF-PER-05 | Capacidad | Un local en operación | Soporta 50 cuentas y 20 sesiones simultáneas sin superar RNF-PER-04 (?) | Misma prueba de carga | Reporte de la herramienta de carga | Piloto | No ejecutado |

### 3.3 Compatibilidad

| ID | Subcaracterística | Contexto de uso | Umbral medible | Método de prueba | Evidencia | Aplica a | Resultado |
|---|---|---|---|---|---|---|---|
| RNF-COM-01 | Coexistencia | Teléfonos del equipo | Los flujos críticos (ingresar, completar tarea, buscar pregunta, crear ticket) funcionan en las dos últimas versiones estables de Chrome en Android y Safari en iOS (?) | Lista de verificación manual por navegador | Tabla navegador × flujo | MVP, Piloto | Parcial: solo Chromium 152 de escritorio emulando móvil (EV-001) |
| RNF-COM-02 | Interoperabilidad | Planillas de horarios | La app lee el formato `.xlsx` oficial sin modificarlo y rechaza con mensaje claro cualquier otro formato | Pruebas con planillas sintéticas válidas e inválidas | Reporte de pruebas | Piloto | No ejecutado. Bloqueado por DEC-05 (formato de colación) |
| RNF-COM-03 | Interoperabilidad | Integración con correo | El envío de correo se hace por un adaptador con contrato documentado; cambiar de proveedor no modifica los módulos de negocio | Revisión de dependencias entre módulos y prueba con un proveedor simulado | Diagrama de dependencias y prueba | Piloto | No ejecutado |

### 3.4 Capacidad de interacción

| ID | Subcaracterística | Contexto de uso | Umbral medible | Método de prueba | Evidencia | Aplica a | Resultado |
|---|---|---|---|---|---|---|---|
| RNF-INT-01 | Operabilidad | Uso con una mano en teléfonos pequeños y medianos | Sin desplazamiento horizontal involuntario en anchos de 360, 390 y 430 px, en todas las pantallas | Script que compara `scrollWidth` con el ancho visible en cada pantalla | Salida del script por ancho y pantalla | MVP, Piloto | **Aprobado** en 6 pantallas del MVP (EV-001). Faltan las hojas modales y la pantalla de ingreso |
| RNF-INT-02 | Operabilidad | Controles táctiles | Todo control interactivo mide al menos 44 × 44 px CSS o tiene un área táctil equivalente | Script que mide los controles visibles | Lista de controles bajo el umbral | MVP, Piloto | No ejecutado de forma completa |
| RNF-INT-03 | Inclusividad | Personas con baja visión | Contraste de texto ≥ 4,5:1 (texto normal) y ≥ 3:1 (texto grande e íconos), en modo claro y oscuro (WCAG 2.2 nivel AA) | axe DevTools o Lighthouse Accesibilidad | Reporte exportado | MVP, Piloto | No ejecutado |
| RNF-INT-04 | Inclusividad | Uso con teclado y lector de pantalla | Los 4 flujos críticos se completan solo con teclado y con TalkBack; cada control tiene nombre accesible | Recorrido manual con lista de verificación | Lista de verificación firmada | MVP, Piloto | No ejecutado |
| RNF-INT-05 | Protección ante errores de usuario | Acciones difíciles de deshacer (bloquear cuenta, eliminar tarea, descartar consulta) | El 100 % pide confirmación o permite deshacer | Revisión de cada acción destructiva | Tabla acción × protección | MVP, Piloto | Parcial: bloquear confirma; descartar consulta no |
| RNF-INT-06 | Autodescripción | Mensajes de error | El 100 % de los mensajes dice qué pasó y qué hacer, en español y sin códigos técnicos | Revisión del catálogo de mensajes | Catálogo de mensajes revisado | MVP, Piloto | No ejecutado |
| RNF-INT-07 | Capacidad de aprendizaje | Persona nueva en el local | Completa una tarea asignada y encuentra una respuesta sin ayuda en ≤ 5 minutos en su primer uso (?) | Prueba con 5 personas, sin instrucciones previas | Planilla de observación sin datos personales | Piloto | No ejecutado |

### 3.5 Fiabilidad

| ID | Subcaracterística | Contexto de uso | Umbral medible | Método de prueba | Evidencia | Aplica a | Resultado |
|---|---|---|---|---|---|---|---|
| RNF-FIA-01 | Disponibilidad | Horario del local (08:30–21:30) durante el piloto | Disponibilidad ≥ 99 % mensual dentro del horario del local (≈ 4 horas de caída al mes como máximo) (?) | Monitoreo externo cada 5 minutos | Reporte mensual del monitor | Piloto | No ejecutado |
| RNF-FIA-02 | Recuperabilidad | Pérdida o daño de la base de datos | RPO ≤ 24 horas y RTO ≤ 4 horas (?) | Simulacro de restauración en un entorno de prueba, al menos una vez antes del piloto | Acta del simulacro con tiempos | Piloto | No ejecutado |
| RNF-FIA-03 | Tolerancia a fallos | Red inestable dentro del local | Si falla una petición, la app muestra el error, conserva lo escrito y permite reintentar; no se pierden datos del formulario | Pruebas con la red cortada desde las herramientas del navegador | Video o capturas del flujo | Piloto | No ejecutado. Depende de DEC-06 (alcance offline) |
| RNF-FIA-04 | Ausencia de fallos | Operación normal | 0 defectos de severidad alta abiertos al iniciar el piloto | Revisión del registro de defectos | Registro de defectos | Piloto | No ejecutado |

### 3.6 Seguridad

El detalle de amenazas, controles y pruebas está en `docs/security/Matriz_Calidad_Seguridad_Privacidad_v0.1.md`. Aquí solo queda el requisito medible y el control asociado.

| ID | Subcaracterística | Umbral medible | Control asociado | Aplica a | Resultado |
|---|---|---|---|---|---|
| RNF-SEG-01 | Confidencialidad | El 100 % de los endpoints rechaza peticiones sin sesión válida (prueba negativa por endpoint) | SEC-01 | Piloto | No ejecutado |
| RNF-SEG-02 | Confidencialidad | El 100 % de las operaciones valida rol y local en el servidor; una matriz rol × operación automatizada no tiene accesos indebidos | SEC-02 | Piloto | No ejecutado |
| RNF-SEG-03 | Autenticidad | Las contraseñas cumplen la política y se guardan con hash lento y sal; 0 contraseñas en texto plano en base, logs o respuestas | SEC-03, SEC-04 | MVP, Piloto | Parcial en MVP (EV-003) |
| RNF-SEG-04 | Resistencia | Tras 5 intentos fallidos la cuenta se bloquea 15 minutos; además hay límite de intentos por IP en el servidor | SEC-05 | MVP, Piloto | Parcial en maqueta Android (EV-002) |
| RNF-SEG-05 | Responsabilidad | El 100 % de las operaciones sensibles (crear, bloquear y reactivar cuentas, cambios de rol, restablecer contraseñas, eliminar) queda en un registro de auditoría con quién, qué y cuándo | SEC-08 | Piloto | No ejecutado |
| RNF-SEG-06 | Integridad | 0 hallazgos de severidad alta o crítica en análisis de dependencias y de secretos al momento de cada entrega | SEC-11, SEC-12 | Piloto | No ejecutado |
| RNF-SEG-07 | Confidencialidad | Una cuenta bloqueada pierde acceso en ≤ 1 minuto, incluidas las sesiones ya abiertas | SEC-06 | MVP, Piloto | Parcial en maqueta Android (EV-002) |
| RNF-SEG-08 | No repudio | Cada cierre de tarea con evidencia y cada cambio de estado de un ticket registra autor y hora del servidor | SEC-08 | Piloto | No ejecutado |

### 3.7 Mantenibilidad

| ID | Subcaracterística | Contexto de uso | Umbral medible | Método de prueba | Evidencia | Aplica a | Resultado |
|---|---|---|---|---|---|---|---|
| RNF-MAN-01 | Modularidad | Backend como monolito modular | Ningún módulo importa clases internas de otro; solo usa su interfaz pública. 0 violaciones | Prueba de arquitectura automatizada (por ejemplo ArchUnit o Spring Modulith) | Reporte de la prueba | Piloto | No ejecutado |
| RNF-MAN-02 | Capacidad de prueba | Reglas de negocio (turnos, contraseñas, permisos, búsqueda) | Cobertura de líneas ≥ 80 % en la capa de dominio (?) | Reporte de cobertura en CI | Reporte de cobertura | Piloto | No ejecutado |
| RNF-MAN-03 | Capacidad de análisis | Todo cambio | CI ejecuta compilación, pruebas, análisis estático y detección de secretos en cada push; no se integra con CI en rojo | Revisión de la configuración y del historial de ejecuciones | Historial de GitHub Actions | Piloto | No ejecutado |
| RNF-MAN-04 | Capacidad de modificación | Cambios de esquema de datos | El 100 % de los cambios de esquema se hace con migraciones versionadas (Flyway); una base vacía se levanta solo con las migraciones | Levantar una base desde cero en CI | Log de CI | Piloto | No ejecutado |
| RNF-MAN-05 | Capacidad de análisis | Decisiones de diseño | Toda decisión que cambie alcance, arquitectura o seguridad tiene una entrada en el registro de decisiones | Revisión en cada entrega | Registro de decisiones | MVP, Piloto | En curso |

### 3.8 Flexibilidad

| ID | Subcaracterística | Contexto de uso | Umbral medible | Método de prueba | Evidencia | Aplica a | Resultado |
|---|---|---|---|---|---|---|---|
| RNF-FLE-01 | Adaptabilidad | Horarios del turno y días de cierre extendido | Se cambian por configuración, sin modificar código | Cambiar la configuración en un entorno de prueba y repetir RNF-FUN-02 | Reporte de pruebas con dos configuraciones | Piloto | No ejecutado (hoy están fijos en el código) |
| RNF-FLE-02 | Escalabilidad | Crecer a más de un local | Todas las tablas de negocio llevan `local_id` y las consultas filtran por él; agregar un local no requiere migrar esquema | Prueba con dos locales sintéticos: un usuario no ve datos del otro | Reporte de pruebas | Piloto | No ejecutado. Depende de DEC-02 (un local o varios) |
| RNF-FLE-03 | Capacidad de reemplazo | Proveedor de correo y de almacenamiento de archivos | Reemplazar el proveedor exige cambiar solo el adaptador y la configuración | Implementar un segundo adaptador simulado y ejecutar las pruebas | Reporte de pruebas | Piloto | No ejecutado |
| RNF-FLE-04 | Capacidad de instalación | Poner en marcha un entorno | Un entorno nuevo se levanta siguiendo el README en ≤ 30 minutos (?) | Una persona distinta del autor sigue el README | Acta con el tiempo y los tropiezos | Piloto | No ejecutado |

### 3.9 Seguridad física (safety)

CheckHelph no controla maquinaria. Su aporte a la seguridad de las personas es indirecto: que un accidente o un riesgo se reporte y llegue a quien debe actuar.

| ID | Subcaracterística | Contexto de uso | Umbral medible | Método de prueba | Evidencia | Aplica a | Resultado |
|---|---|---|---|---|---|---|---|
| RNF-SAF-01 | Advertencia de peligro | Ticket de tipo "Accidente" | Al crear el ticket, la pantalla muestra de inmediato que la app no reemplaza el aviso directo a la jefatura ni a emergencias | Revisión de la pantalla | Captura sin datos reales | MVP, Piloto | No ejecutado (el MVP aún no muestra el aviso) |
| RNF-SAF-02 | Identificación de riesgos | Ticket de tipo "Accidente" | La notificación a Jefe y Asistente sale en ≤ 1 minuto; si el envío falla, queda registrado y visible en la bandeja (?) | Prueba con el adaptador de correo simulando éxito y fallo | Reporte de pruebas | Piloto | No ejecutado |
| RNF-SAF-03 | Fallo seguro | Revisión de horarios | Si la planilla no se puede interpretar, la app lo dice y no muestra "sin observaciones" | Pruebas con planillas sintéticas dañadas | Reporte de pruebas | Piloto | No ejecutado |
| RNF-SAF-04 | Restricción operativa | Revisión de horarios | Cada resultado muestra que es una herramienta de apoyo y no asesoría jurídica | Revisión de la pantalla | Captura | Piloto | No ejecutado |

## 4. Resumen de estado

- Requisitos propuestos: 44.
- Aprobados por Felipe: 0.
- Con alguna evidencia inicial: RNF-FUN-02, RNF-COM-01, RNF-INT-01, RNF-INT-05, RNF-SEG-03, RNF-SEG-04 y RNF-SEG-07.
- Bloqueados por decisiones abiertas: RNF-COM-02 (DEC-05), RNF-FIA-03 (DEC-06) y RNF-FLE-02 (DEC-02).

## 5. Qué necesito de Felipe

1. Confirmar o corregir los umbrales marcados con (?).
2. Indicar qué teléfonos y navegadores usa realmente el equipo, para cerrar RNF-COM-01.
3. Decidir si los requisitos de `Piloto` entran en el alcance del Capstone o quedan como trabajo futuro.
