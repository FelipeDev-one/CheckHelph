# Plan de trazabilidad

| | |
|---|---|
| Versión | 0.1 (recreación) |
| Fecha | 2026-11-10 |
| Estado | **Por validar** por Felipe |
| Origen | Recreado desde el contexto del proyecto. El archivo original y la planilla `CheckHelph_Requerimientos_Historias_v0.2.0.xlsx` no se encontraron. Los IDs de este documento son nuevos: si la planilla aparece, hay que mapear sus IDs a estos antes de seguir. |

## 1. Cadena de trazabilidad

```
Objetivo de usuario → RU → RF / RNF → historia y criterios → decisión o diseño
   → código (commit o tag) → caso de prueba → resultado y evidencia → aceptación de Felipe
```

La relación es bidireccional: desde un objetivo se llega a su evidencia, y desde cualquier commit o prueba se llega al requisito que lo justifica.

## 2. Identificadores

| Prefijo | Qué identifica | Dónde vive |
|---|---|---|
| `OBJ-nn` | Objetivo de usuario | Este documento, sección 4 |
| `RU-nn` | Requisito de usuario | Este documento, sección 5 |
| `RF-nn` | Requisito funcional | Este documento, sección 6 |
| `RNF-XXX-nn` | Requisito de calidad | `Catalogo_Calidad_ISO25010_v0.1.md` |
| `SEC-nn`, `PRI-nn`, `DAT-nn`, `LEG-nn` | Control de seguridad, requisito de privacidad, dato, pregunta legal | `docs/security/Matriz_Calidad_Seguridad_Privacidad_v0.1.md` |
| `HU-nn` | Historia de usuario | Este documento, sección 7 |
| `DEC-nn` | Decisión | `docs/management/Registro_Decisiones_v0.1.md` |
| `CP-nn` | Caso de prueba | Este documento, sección 8 |
| `EV-nnn` | Evidencia de una ejecución | `docs/quality/evidence/` |
| `DEF-nn` | Defecto | Sección 9 |

Reglas:

- Un ID nunca se reutiliza ni se renumera. Si un requisito se descarta, queda con estado *Descartado*.
- Si un requisito cambia de fondo, sube de versión (`RF-07 v2`) y se anota el motivo.
- Los mensajes de commit citan el ID: `feat(preguntas): autocompletado [RF-08]`.

## 3. Estados

`Por validar` → `Aprobado por Felipe` → `En diseño` → `Implementado` → `Verificado` → `Aceptado/cerrado`

- Todo lo nuevo parte en **Por validar**.
- Solo Felipe mueve algo a *Aprobado por Felipe* o *Aceptado/cerrado*.
- *Implementado (prototipo)* significa que existe en `mvp-web/` o `android/` como simulación en el dispositivo. No equivale a implementado en el producto.

## 4. Objetivos de usuario

| ID | Objetivo |
|---|---|
| OBJ-01 | El equipo resuelve dudas del turno sin depender siempre del Jefe de local |
| OBJ-02 | La jefatura asigna y sigue las tareas del turno con claridad |
| OBJ-03 | Las fallas e incidentes se reportan y llegan a quien debe atenderlos |
| OBJ-04 | La jefatura revisa los horarios con apoyo antes de publicarlos |
| OBJ-05 | Solo las personas vigentes del local acceden, cada una con los permisos de su rol |

## 5. Requisitos de usuario

| ID | Requisito de usuario | Objetivo | Estado |
|---|---|---|---|
| RU-01 | Como integrante del equipo, quiero encontrar la respuesta a una duda del turno desde el teléfono | OBJ-01 | Por validar |
| RU-02 | Como Jefe, quiero enterarme de las dudas que no tienen respuesta para agregarlas | OBJ-01 | Por validar |
| RU-03 | Como Jefe o Asistente, quiero asignar tareas y ver su avance | OBJ-02 | Por validar |
| RU-04 | Como integrante del equipo, quiero ver y completar mis tareas | OBJ-02 | Por validar |
| RU-05 | Como integrante del equipo, quiero reportar una falla, accidente o incidente con contexto | OBJ-03 | Por validar |
| RU-06 | Como Jefe o Asistente, quiero una bandeja de tickets y saber qué tipos se repiten | OBJ-03 | Por validar |
| RU-07 | Como Jefe, quiero comparar la planilla de horarios anterior y la nueva y recibir alertas | OBJ-04 | Por validar |
| RU-08 | Como integrante del equipo, quiero entrar con mi correo y contraseña y recuperarla si la olvido | OBJ-05 | Por validar |
| RU-09 | Como Jefe, quiero crear cuentas y bloquear a quienes dejan el local | OBJ-05 | Por validar |

## 6. Requisitos funcionales

| ID | Requisito funcional | RU | Estado | Dónde está |
|---|---|---|---|---|
| RF-01 | Iniciar sesión con correo y contraseña | RU-08 | Implementado (prototipo) | `mvp-web`, `android` |
| RF-02 | Política de contraseña: largo, tipos de caracteres, sin correo ni palabras comunes, sin repetir las 3 anteriores | RU-08 | Implementado (prototipo) | `mvp-web`, `android` |
| RF-03 | Vencimiento de la contraseña a los 90 días con cambio obligatorio | RU-08 | Implementado (prototipo). Ver DEC-08 | `mvp-web`, `android` |
| RF-04 | Recuperar la contraseña con "Olvidé mi contraseña" | RU-08 | Implementado (prototipo, sin correo real) | `mvp-web`, `android` |
| RF-05 | El Jefe crea cuentas con contraseña temporal y asiste en la recuperación | RU-09 | Implementado (prototipo) | `mvp-web`, `android` |
| RF-06 | El Jefe bloquea y reactiva cuentas; el bloqueo cierra la sesión | RU-09 | Implementado (prototipo) | `mvp-web`, `android` |
| RF-07 | Determinar el momento del turno: Apertura 08:30–12:00, Intermedio 12:00–18:00, Cierre 18:00–21:30 (lunes, viernes, sábado y último día del mes) o 18:00–21:00 | RU-01 | Implementado (prototipo) | `mvp-web`, `android` |
| RF-08 | Buscar preguntas por palabras clave con autocompletado, sin distinguir mayúsculas ni tildes | RU-01 | Implementado (prototipo) | `mvp-web`, `android` |
| RF-09 | Sugerir preguntas según el momento del turno, priorizando las más usadas | RU-01 | Implementado (prototipo) | `mvp-web`, `android` |
| RF-10 | Sin respuesta: proponer Frecuentes; si tampoco hay, mostrar "Consulta con tu jefe de local" y registrar la consulta | RU-01, RU-02 | Implementado (prototipo) | `mvp-web`, `android` |
| RF-11 | El Jefe responde una consulta pendiente y la publica en una categoría | RU-02 | Implementado (prototipo). Depende de DEC-03 | `mvp-web`, `android` |
| RF-12 | El Jefe agrega, edita y elimina Preguntas frecuentes | RU-02 | Por validar | — |
| RF-13 | Preguntas de ingreso al local | RU-01 | Por validar (solo estructura) | `mvp-web`, `android` |
| RF-14 | Ver y completar tareas | RU-04 | Implementado (prototipo) | `mvp-web` |
| RF-15 | Crear, asignar y eliminar tareas según el rol | RU-03 | Por validar | — |
| RF-16 | Adjuntar evidencia en tareas con vencimiento | RU-04 | Por validar. Bloqueado por DEC-04 | — |
| RF-17 | Reportar un ticket con tipo y contexto | RU-05 | Implementado (prototipo) | `mvp-web` |
| RF-18 | Bandeja de tickets con cambio de estado | RU-06 | Implementado (prototipo) | `mvp-web` |
| RF-19 | Gráfico de torta de tickets por tipo | RU-06 | Implementado (prototipo) | `mvp-web` |
| RF-20 | Notificar por correo al Jefe y la Asistente al crear un ticket | RU-06 | Por validar | — |
| RF-21 | El Jefe habilita Horarios para la Asistente | RU-07 | Implementado (prototipo, solo el permiso) | `mvp-web`, `android` |
| RF-22 | Cargar dos planillas Excel y generar alertas de apoyo, sin leer RUT | RU-07 | Por validar. Bloqueado por DEC-05 | — |
| RF-23 | Solo el Jefe cambia la foto de perfil | RU-09 | Por validar | — |
| RF-24 | Ver el avance y las métricas del equipo | RU-03 | Por validar | — |

## 7. Historias de usuario y criterios de aceptación (borrador)

Se redactan primero las historias que ya tienen prototipo, para que Felipe pueda validarlas viendo algo que funciona. **Ninguna está aprobada.**

### HU-01 · Encontrar una respuesta del turno — RF-07, RF-08, RF-09

Como integrante del equipo, quiero escribir mi duda y ver sugerencias, para resolverla sin interrumpir al Jefe.

- CA-01.1: Al abrir Preguntas se muestra el momento del turno actual y su horario.
- CA-01.2: Sin escribir nada, se sugieren hasta 4 preguntas del momento actual, de la más usada a la menos usada.
- CA-01.3: Al escribir una palabra de 3 letras o más aparecen hasta 5 sugerencias.
- CA-01.4: "COMO ABRO caja" encuentra "¿Cómo abro la caja?".
- CA-01.5: Al elegir una sugerencia se muestra la respuesta y aumenta su contador de uso.

### HU-02 · Duda sin respuesta — RF-10

Como integrante del equipo, quiero dejar registrada una duda que no tiene respuesta, para que el Jefe la agregue.

- CA-02.1: Si no hay coincidencias en Del turno, se ofrece buscar en Preguntas frecuentes.
- CA-02.2: Si tampoco hay coincidencias, se muestra "Consulta con tu jefe de local" y un botón para enviar la consulta.
- CA-02.3: La misma consulta no se registra dos veces.

### HU-03 · Responder consultas pendientes — RF-11

Como Jefe, quiero ver las consultas sin respuesta y responderlas, para que la respuesta quede disponible para todos.

- CA-03.1: El Jefe y la Asistente ven la cantidad de consultas pendientes en la pestaña Preguntas.
- CA-03.2: Solo el Jefe puede responder o descartar. *(Supuesto, pendiente DEC-03.)*
- CA-03.3: Al publicar, la pregunta aparece en la categoría elegida y la consulta deja de estar pendiente.

### HU-04 · Iniciar sesión — RF-01, RF-02, RF-03

Como integrante del equipo, quiero entrar con mi correo y contraseña, para que solo yo use mi cuenta.

- CA-04.1: Con credenciales correctas se entra a Inicio.
- CA-04.2: Con credenciales incorrectas se muestra un mensaje que no indica si el correo existe.
- CA-04.3: Tras 5 intentos fallidos la cuenta queda bloqueada 15 minutos.
- CA-04.4: Una contraseña con más de 90 días obliga a crear una nueva antes de entrar.
- CA-04.5: La nueva contraseña debe cumplir todos los requisitos, que se muestran marcados en vivo, y no puede repetir las 3 anteriores.

### HU-05 · Recuperar la contraseña — RF-04

Como integrante del equipo, quiero recuperar mi contraseña si la olvido, para volver a entrar sin esperar al Jefe.

- CA-05.1: Al pedir la recuperación, el mensaje es el mismo exista o no la cuenta.
- CA-05.2: El código vence a los 15 minutos y sirve una sola vez.
- CA-05.3: Un código incorrecto no cambia la contraseña.

### HU-06 · Administrar cuentas — RF-05, RF-06

Como Jefe, quiero crear cuentas y bloquear a quienes dejan el local, para que solo el equipo vigente tenga acceso.

- CA-06.1: Solo el Jefe ve "Usuarios del local".
- CA-06.2: Una cuenta nueva recibe una contraseña temporal que debe cambiarse en el primer ingreso.
- CA-06.3: Bloquear pide confirmación; la persona bloqueada no puede entrar y su sesión abierta se cierra.
- CA-06.4: Una cuenta bloqueada se puede reactivar y conserva su historial.
- CA-06.5: El Jefe no puede bloquearse a sí mismo.

### HU-07 · Reportar un incidente — RF-17, RF-18, RF-19

Como integrante del equipo, quiero reportar una falla o incidente con un contexto breve, para que la jefatura lo atienda.

- CA-07.1: El ticket exige tipo y contexto.
- CA-07.2: El Jefe y la Asistente ven la bandeja, cambian el estado y ven el gráfico por tipo.

Historias pendientes de redactar: tareas (RF-14 a RF-16), preguntas frecuentes (RF-12), horarios (RF-22), foto de perfil (RF-23) y métricas (RF-24).

## 8. Casos de prueba registrados

| ID | Qué verifica | Requisitos | Tipo | Dónde | Última ejecución |
|---|---|---|---|---|---|
| CP-01 | Sin desplazamiento horizontal en 360, 390 y 430 px | RNF-INT-01 | Script en navegador | Descrito en EV-001 | EV-001: aprobado |
| CP-02 | Momentos del turno y cierre extendido | RF-07, RNF-FUN-02 | Unitaria automatizada | `android/app/src/test/.../ShiftScheduleTest.kt` | EV-002: aprobado |
| CP-03 | Política, vencimiento y hash de contraseñas | RF-02, RF-03, SEC-03, SEC-04 | Unitaria automatizada | `.../PasswordPolicyTest.kt` | EV-002: aprobado |
| CP-04 | Ingreso, bloqueo por intentos, bloqueo de cuenta, recuperación, cuenta nueva, consultas pendientes, búsqueda y persistencia | RF-01, RF-04 a RF-06, RF-08, RF-10, RF-11, SEC-05 a SEC-07 | Unitaria automatizada | `.../AppStoreTest.kt` | EV-002: aprobado |
| CP-05 | Flujos del MVP web de punta a punta | HU-01 a HU-07 | Script en navegador | Descrito en EV-003 | EV-003: aprobado |
| CP-06 | Sin RUT, correos reales ni secretos en el repositorio | PRI-06, SEC-12 | Búsqueda por patrones | Descrito en EV-004 | EV-004: aprobado con límites |

Los scripts de CP-01 y CP-05 se ejecutaron a mano desde la consola del navegador. **Pendiente:** versionarlos como pruebas automatizadas (por ejemplo, con Playwright) para que sean reproducibles.

## 9. Defectos

| ID | Descripción | Detectado en | Estado |
|---|---|---|---|
| DEF-01 | Descartar una consulta pendiente no pide confirmación (RNF-INT-05) | Revisión del MVP web, 2026-11-10 | Abierto |
| DEF-02 | El ticket de tipo Accidente no muestra el aviso de RNF-SAF-01 | Revisión del MVP web, 2026-11-10 | Abierto |
| DEF-03 | Los horarios del turno están fijos en el código (RNF-FLE-01) | Revisión del código, 2026-11-10 | Abierto |

## 10. Registro por requisito verificado

Cuando un requisito pase a *Verificado*, se completa una ficha con esta estructura en `docs/quality/evidence/`:

- ID y versión del requisito; historia y criterio de aceptación.
- Commit o tag, y archivos modificados.
- Caso de prueba, entorno y datos sintéticos usados.
- Resultado: aprobado, fallido, bloqueado o no ejecutado.
- Evidencia, sin datos personales ni secretos.
- Defectos encontrados y su corrección.
- Aceptación de Felipe, con fecha.

**Límite actual:** `mvp-web/` y `android/` todavía no tienen commit. Las evidencias EV-001 a EV-004 identifican el archivo probado por su huella SHA-256 o por la fecha de ejecución, y deben repetirse o enlazarse al commit cuando Felipe lo cree.
