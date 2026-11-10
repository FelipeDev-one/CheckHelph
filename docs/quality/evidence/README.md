# Evidencias de verificación

| | |
|---|---|
| Versión | 0.1 (recreación) |
| Fecha | 2026-11-10 |
| Estado | **Por validar** por Felipe |

Esta carpeta guarda el resultado de cada ejecución de pruebas que respalda un requisito. Una evidencia registra lo que pasó en una fecha y sobre una versión concreta: no es una aprobación del requisito ni una certificación.

## Reglas

1. **Sin datos reales.** Nada de RUT, planillas reales, correos personales, incidentes reales, tokens, claves ni capturas donde se reconozca a una persona. Solo datos sintéticos (`@checkhelph.test`).
2. **Una ficha por ejecución**, con ID `EV-nnn` correlativo. Una evidencia no se edita para "mejorar" un resultado: si la prueba se repite, se crea una ficha nueva.
3. **Versión probada identificable:** commit o tag; si aún no existe, la huella SHA-256 del archivo.
4. **Resultado honesto:** aprobado, fallido, bloqueado o no ejecutado, con sus límites.
5. Los archivos pesados (videos, reportes HTML) no se suben al repositorio sin revisar antes que no contengan datos personales.

## Índice

| ID | Fecha | Caso | Requisitos | Resultado |
|---|---|---|---|---|
| EV-001 | 2026-11-10 | CP-01 Anchos móviles | RNF-INT-01, RNF-COM-01 | Aprobado (parcial) |
| EV-002 | 2026-11-08 | CP-02, CP-03, CP-04 Pruebas unitarias | RF-01 a RF-11, RNF-FUN-02, SEC-03 a SEC-07 | Aprobado |
| EV-003 | 2026-11-10 | CP-05 Flujos del MVP web | HU-01 a HU-07, RNF-SEG-03 | Aprobado |
| EV-004 | 2026-11-10 | CP-06 Datos reales y secretos | PRI-06, SEC-12 | Aprobado con límites |

---

## EV-001 · Anchos móviles sin desplazamiento horizontal

- **Requisitos:** RNF-INT-01 (principal), RNF-COM-01 (parcial).
- **Versión probada:** `mvp-web/index.html`, SHA-256 `28bc2ff7c3d583af…`, sin commit.
- **Entorno:** Chromium 152 de escritorio con emulación de viewport, servido desde `http://localhost:5173`. Sesión del Jefe de demostración.
- **Método:** en cada ancho se recorrieron las pantallas y se comparó `document.documentElement.scrollWidth` con `window.innerWidth`.
- **Resultado:**

| Ancho | Inicio | Tareas | Preguntas | Tickets | Perfil | Usuarios |
|---|---|---|---|---|---|---|
| 360 px | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| 390 px | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| 430 px | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |

- **Límites:**
  - No se midieron la pantalla de ingreso, la recuperación de contraseña ni las hojas modales.
  - No se probó en un teléfono físico ni en Safari de iOS.
  - El carrusel de Inicio se desplaza en horizontal a propósito y no cuenta como desborde.
- **Defectos:** ninguno.

## EV-002 · Pruebas unitarias de la maqueta Android

- **Requisitos:** RF-01 a RF-11 en su lógica; RNF-FUN-02; SEC-03 a SEC-07.
- **Versión probada:** carpeta `android/`, sin commit, estado del 2026-11-08.
- **Entorno:** JVM (JBR de Android Studio), Gradle 9.6.0, comando `./gradlew testDebugUnitTest`.
- **Resultado:** 18 pruebas, 0 fallos, 0 errores, 0 omitidas.

| Clase | Pruebas | Fallos |
|---|---|---|
| `ShiftScheduleTest` | 4 | 0 |
| `PasswordPolicyTest` | 5 | 0 |
| `AppStoreTest` | 9 | 0 |

- **Límites:**
  - Prueban la lógica en el dispositivo, que simula lo que hará el servidor. No demuestran seguridad del producto real.
  - RNF-FUN-02 queda parcial: no se cubren todos los minutos de borde que lista el requisito.
- **Defectos:** ninguno.

## EV-003 · Flujos del MVP web de punta a punta

- **Requisitos:** HU-01 a HU-07; RNF-SEG-03 (parcial).
- **Versión probada:** `mvp-web/index.html`, SHA-256 `28bc2ff7c3d583af…`, sin commit.
- **Entorno:** Chromium 152 con viewport de 375 × 812, `http://localhost:5173`, datos de demostración.
- **Método:** script ejecutado desde la consola que llena los formularios y lee el resultado en pantalla.
- **Resultado:** todos los pasos dieron lo esperado.

| Paso | Esperado | Obtenido |
|---|---|---|
| Ingreso con cuenta bloqueada | Mensaje de cuenta desactivada | ✓ |
| Ingreso con contraseña incorrecta | Mensaje genérico | ✓ |
| Ingreso con contraseña vencida | Pantalla de cambio obligatorio | ✓ |
| Repetir la contraseña anterior | Rechazo | ✓ |
| Sugerencias en Apertura | 3 preguntas de apertura | ✓ |
| Autocompletado con "cola" | "¿Cómo pido mi colación?" | ✓ |
| Búsqueda "COMO ABRO caja" | "¿Cómo abro la caja?" | ✓ |
| Consulta sin respuesta | Frecuentes → "Consulta con tu jefe de local" → enviada | ✓ |
| El Jefe responde la consulta | Deja de estar pendiente y se puede buscar | ✓ |
| Bloquear a un trabajador | Pasa a 3 activos y 2 bloqueados; no puede ingresar | ✓ |
| Crear cuenta | Contraseña temporal válida; cambio obligatorio al ingresar | ✓ |
| Crear ticket | Aparece en la bandeja y en el gráfico | ✓ |
| Viernes 21:15 (hora simulada) | Cierre 18:00–21:30 | ✓ |
| Martes 21:15 | Local cerrado | ✓ |
| Lunes 30-11 21:15 (lunes y además fin de mes; el fin de mes aislado lo cubre EV-002) | Cierre 18:00–21:30 | ✓ |
| Recuperación con código incorrecto | Rechazo | ✓ |
| Recuperación con código correcto | Contraseña cambiada; ingreso correcto | ✓ |
| Contraseñas en `localStorage` | Ninguna en texto plano | ✓ |

- **Límites:**
  - El script no está versionado; debe convertirse en una prueba automatizada.
  - No se probó abriendo el archivo con doble clic (`file://`).
  - `localStorage` no es almacenamiento seguro: el resultado solo muestra que el prototipo no guarda contraseñas en claro.
- **Defectos:** DEF-01 y DEF-02, detectados en la revisión posterior.

## EV-004 · Búsqueda de datos reales y secretos

- **Requisitos:** PRI-06, SEC-12.
- **Versión probada:** árbol de trabajo completo del repositorio al 2026-11-10 (archivos versionados y sin versionar; se excluyen `.git` y carpetas de compilación).
- **Método:** búsqueda por expresiones regulares de patrones de RUT, direcciones de correo y asignaciones de claves o contraseñas.
- **Resultado:**
  - Patrones de RUT: 0.
  - Correos: 9 direcciones distintas, todas del dominio ficticio `@checkhelph.test`.
  - Secretos: solo la contraseña de demostración, declarada a propósito en `DemoSeed.kt` y `mvp-web/index.html`.
- **Límites:**
  - No revisa archivos binarios, como `kit/CheckHelph_MVP.pptx`.
  - No revisa el historial de Git, solo el estado actual.
  - No reemplaza a un detector de secretos en CI (SEC-12).
- **Defectos:** ninguno.
