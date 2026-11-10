# Registro de cambios

Los cambios se anotan aquí al prepararse. Una entrada pasa a tener versión cuando Felipe crea el commit y el tag correspondientes.

## Sin versionar · preparado entre el 2026-11-08 y el 2026-11-10

Estado: **cambios locales sin commit ni push.** Versión sugerida al etiquetar: `v0.4.0`.

### Agregado

- `android/`: maqueta Android en Kotlin + Jetpack Compose.
  - Tema de marca, ícono adaptativo y navegación inferior.
  - Inicio de sesión, política de contraseñas, vencimiento a 90 días, recuperación con código y bloqueo por intentos.
  - Usuarios: crear cuentas, bloquear, reactivar y contraseña temporal.
  - Preguntas con momento del turno, sugerencias, autocompletado y consultas sin respuesta.
  - 18 pruebas unitarias.
- `mvp-web/index.html`: prototipo web mobile-first en un archivo, con los mismos flujos, más tareas, tickets con gráfico por tipo y hora simulada para demostraciones.
- `docs/`: documentación recreada como propuesta por validar.
  - Catálogo de calidad ISO/IEC 25010:2023 con 44 requisitos.
  - Matriz de seguridad y privacidad.
  - Plan de trazabilidad con historias HU-01 a HU-07.
  - Evidencias EV-001 a EV-004.
  - Principios de arquitectura.
  - Registro de decisiones.
  - Línea base estimada y backlog por fases.

### Observaciones

- La versión v0.3.5 y el commit `7ad2571` citados en el contexto del proyecto no existen en este repositorio.
- La entrada `CheckHelph-Repository` del repositorio apunta a un submódulo sin contenido.
- El `README.md` de la raíz está en UTF-16 y se ve mal en GitHub.

## 2026-11-08 · commit `a0f7d59`

- Se agregó `CheckHelph.html` (prototipo web anterior). El mensaje del commit menciona Android, pero la carpeta `android/` no se incluyó.

## 2026-10-23 · commits `994b4c3` y `c61caf6`

- Commit inicial y carpeta `kit/`: identidad visual, láminas del MVP y prototipo con Inicio, Tareas, Bloqueos y Traspasos.
