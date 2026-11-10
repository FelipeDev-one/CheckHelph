# Línea base inicial y backlog por fases

| | |
|---|---|
| Versión | 0.1 |
| Fecha | 2026-11-10 |
| Estado | **Estimación por validar.** No es una línea base aprobada: se convierte en una cuando Felipe la valide. |

## 1. Lo que falta para que esto sea una línea base

Tres datos que no constan en el repositorio y que cambian toda la planificación:

1. **Fecha de entrega del Capstone** y fechas de hitos intermedios.
2. **Horas semanales disponibles** y si Felipe trabaja solo o en equipo.
3. **Presupuesto disponible**, aunque sea cero.

Hasta tenerlos, las duraciones están en semanas de esfuerzo y no en fechas.

## 2. Triángulo de hierro

| Restricción | Propuesta | Estado |
|---|---|---|
| **Alcance** | Tres niveles posibles (DEC-09), descritos abajo | Por validar |
| **Tiempo** | Fases en semanas de esfuerzo, suponiendo 12 horas semanales de una persona (?) | Estimación |
| **Costo** | Lista de partidas sin montos: hay que cotizarlas | Por cotizar |
| **Calidad** | Criterios del catálogo ISO 25010 y de la matriz de seguridad | Por validar |

**Regla:** un retraso no se resuelve bajando seguridad, privacidad, requisitos legales ni criterios de aceptación sin mostrar antes el impacto a Felipe. Lo primero que se ajusta es el alcance, sacando fases completas desde el final.

## 3. Alcance por niveles

| Nivel | Qué entrega | Incluye | Excluye |
|---|---|---|---|
| **A. Prototipo validado** | `mvp-web` completo, historias validadas, documentación y diseño del producto | Fases 0 y 1 | Backend, datos compartidos, despliegue |
| **B. Producto en entorno local** | Backend y frontend reales funcionando en el computador de desarrollo, con CI | Fases 0 a 4 | Despliegue, usuarios reales |
| **C. Piloto en un local** | Lo anterior, desplegado y usado por un equipo real | Fases 0 a 6 | Varios locales, sincronización sin conexión |

Exclusiones propuestas en cualquier nivel: varios locales, escritura sin conexión, app nativa publicada en tiendas, validación legal automática de horarios con valor jurídico.

## 4. Backlog por fases

Las semanas son de esfuerzo, no de calendario. Cada fase termina con algo que se puede mostrar.

### Fase 0 · Base ordenada — 1 semana (?)

| Ítem | Requisitos | Listo cuando |
|---|---|---|
| Felipe versiona `android/`, `mvp-web/` y `docs/` | DEC-12 | Hay un commit y un tag en GitHub |
| Resolver la entrada vacía `CheckHelph-Repository` del repositorio | — | El repositorio ya no apunta a un submódulo inexistente |
| Corregir la codificación del `README.md` de la raíz (hoy está en UTF-16) | — | Se lee bien en GitHub |
| Felipe revisa HU-01 a HU-07 | RNF-FUN-01 | Cada historia queda aprobada, corregida o descartada |
| Felipe responde DEC-01, DEC-03, DEC-08 y DEC-09 | — | Quedan registradas como tomadas |

### Fase 1 · Completar el prototipo web — 2 a 3 semanas (?)

| Ítem | Requisitos | Listo cuando |
|---|---|---|
| Crear, asignar y eliminar tareas según el rol | RF-15 | Criterios de su historia aprobados y verificados |
| Preguntas frecuentes: agregar, editar y eliminar | RF-12 | Ídem |
| Corregir DEF-01, DEF-02 y DEF-03 | RNF-INT-05, RNF-SAF-01, RNF-FLE-01 | Defectos cerrados con evidencia |
| Pruebas automatizadas de los flujos (por ejemplo, Playwright) | CP-01, CP-05 | Se ejecutan con un comando |
| Medir rendimiento y accesibilidad con Lighthouse y axe | RNF-PER-01, RNF-PER-02, RNF-INT-02 a RNF-INT-04 | Reportes en evidencias |
| Prueba con 3 a 5 personas del local, con datos ficticios | RNF-FUN-04, RNF-INT-07 | Observaciones registradas sin datos personales |

### Fase 2 · Backend: identidad, equipo y auditoría — 3 a 4 semanas (?)

| Ítem | Requisitos | Listo cuando |
|---|---|---|
| Proyecto Spring Boot modular, PostgreSQL y Flyway | RNF-MAN-01, RNF-MAN-04 | Una base vacía se levanta solo con migraciones |
| CI: compilar, probar, análisis estático, dependencias y secretos | RNF-MAN-03, SEC-11, SEC-12 | Se ejecuta en cada push |
| Sesiones, contraseñas con Argon2id, bloqueo por intentos, recuperación con token | SEC-01, SEC-03 a SEC-07 | Pruebas negativas en verde |
| Autorización por rol y local en el servidor | SEC-02 | Matriz rol × operación en verde |
| Registro de auditoría | SEC-08 | Cada operación sensible deja registro |

### Fase 3 · Frontend real y Preguntas — 3 semanas (?)

| Ítem | Requisitos | Listo cuando |
|---|---|---|
| React + TypeScript + Vite con ingreso y perfil contra la API | RF-01 a RF-06 | Los flujos pasan contra el backend |
| Preguntas con datos compartidos | RF-07 a RF-13 | Dos sesiones distintas ven la misma respuesta nueva |
| Limpieza de datos locales al cerrar sesión | SEC-14 | Almacenamiento vacío tras cerrar sesión |

### Fase 4 · Tareas y Tickets — 3 semanas (?)

| Ítem | Requisitos | Listo cuando |
|---|---|---|
| Tareas con asignación por rol | RF-14, RF-15 | Verificado |
| Tickets, bandeja y gráfico | RF-17 a RF-19 | Verificado |
| Notificación por correo con adaptador | RF-20, RNF-COM-03, RNF-SAF-02 | Verificado con adaptador simulado |

### Fase 5 · Horarios — 2 a 3 semanas (?), bloqueada por DEC-05

| Ítem | Requisitos | Listo cuando |
|---|---|---|
| Lectura de planillas en el navegador, sin RUT | RF-22, SEC-13 | Cero tráfico de red con contenido de la planilla |
| Reglas parametrizadas, con fuente y fecha | RNF-SAF-03, RNF-SAF-04 | Pruebas con planillas sintéticas |

### Fase 6 · Piloto — 2 a 3 semanas (?), más el tiempo de uso

| Ítem | Requisitos | Listo cuando |
|---|---|---|
| Elegir hosting, correo y almacenamiento | DEC-07 | Comparación documentada y decisión de Felipe |
| Despliegue con HTTPS, respaldo y monitoreo | SEC-09, SEC-10, SEC-18, RNF-FIA-01, RNF-FIA-02 | Simulacro de restauración aprobado |
| Definir responsable de datos e información a las personas | LEG-01 a LEG-03, PRI-02, PRI-03 | Documentado y revisado |
| Procedimiento de incidentes | SEC-19 | Simulacro de mesa realizado |

### Suma de esfuerzo estimado

| Nivel | Fases | Semanas de esfuerzo (?) |
|---|---|---|
| A | 0 y 1 | 3 a 4 |
| B | 0 a 4 | 12 a 14 |
| C | 0 a 6 | 16 a 20 |

Con 12 horas semanales, el nivel C no cabe en un semestre. Es el dato más importante de esta estimación y depende de confirmar las horas reales.

## 5. Costo: partidas por cotizar

No se incluyen montos porque no se han cotizado y los precios cambian.

| Partida | Necesaria desde | Notas |
|---|---|---|
| Hosting del backend | Nivel C | Comparar opciones con capa gratuita y de pago |
| Base de datos PostgreSQL administrada | Nivel C | Incluir respaldos |
| Dominio | Nivel C | Opcional si se usa el subdominio del proveedor |
| Correo transaccional | Nivel C | Varias opciones tienen capa gratuita con límite diario |
| Almacenamiento de fotos | Solo si DEC-04 incluye fotos | |
| Monitoreo de disponibilidad | Nivel C | Hay opciones gratuitas |
| Herramientas de desarrollo y CI | Todos | GitHub Actions es gratuito en repositorios públicos, con límites |
| Operación y mantenimiento posterior | Nivel C | Tiempo de alguien después del Capstone; hay que decidir quién |
| Revisión legal | Nivel C con datos reales | LEG-01 a LEG-04 |

Los niveles A y B se pueden hacer sin costo directo.

## 6. Riesgos principales

| ID | Riesgo | Efecto | Mitigación propuesta |
|---|---|---|---|
| RIE-01 | El trabajo anterior (v0.3.5 y documentos) existe solo fuera del repositorio | Duplicación o contradicciones si reaparece | Versionar todo en GitHub desde ahora; reconciliar IDs si aparece |
| RIE-02 | El alcance crece más rápido que el tiempo | No se termina ningún nivel | Fijar el nivel (DEC-09) y recortar por fases completas |
| RIE-03 | Usar datos reales en un prototipo sin seguridad | Exposición de datos personales | Avisos en pantalla, datos ficticios y regla de no usar datos reales |
| RIE-04 | Las reglas laborales de horarios se interpretan mal | Alertas erróneas tomadas como válidas | Fuente oficial, aviso de "herramienta de apoyo" y fase bloqueada hasta DEC-05 |
| RIE-05 | El piloto no tiene quién lo opere después | Sistema abandonado con datos personales | Decidir el responsable antes del nivel C y planificar el cierre y borrado |
| RIE-06 | Hay dos prototipos distintos y dos maquetas | Confusión sobre cuál es la referencia | Resolver DEC-01 |

## 7. Si cambia una restricción

Al cambiar alcance, tiempo o costo se actualizan, en este orden: registro de decisiones, este documento, catálogo de calidad, trazabilidad y riesgos. El cambio se anota en `CHANGELOG.md`.
