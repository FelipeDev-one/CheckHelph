# Registro de decisiones

| | |
|---|---|
| Versión | 0.1 |
| Fecha | 2026-11-10 |
| Estado | Registro vivo. Solo Felipe marca una decisión como *Tomada*. |

Cada decisión indica su impacto en alcance, tiempo y costo. Las entradas no se borran: si una decisión cambia, se agrega una nueva que la reemplaza.

## 1. Decisiones tomadas por Felipe

| ID | Fecha | Decisión | Impacto |
|---|---|---|---|
| DEC-10 | 2026-11-08 | El producto será un **sitio web mobile-first** accesible para todos. La app Android es solo una maqueta de diseño | Alcance: una sola base de código para Android e iPhone. Tiempo y costo: menores que mantener una app nativa |
| DEC-12 | 2026-11-08 | Felipe hace todos los commits, tags y push. El asistente solo prepara cambios y sugiere el mensaje | Los cambios quedan sin versionar hasta que Felipe los confirma; las evidencias se enlazan al commit después |
| DEC-13 | 2026-11-08 | Horario del turno: Apertura 08:30–12:00; Intermedio 12:00–18:00; Cierre 18:00–21:30 los lunes, viernes, sábados y el último día del mes, y 18:00–21:00 los demás días | Define RF-07 |
| DEC-14 | 2026-11-08 | Inicio de sesión con correo y contraseña, recuperación por el usuario, contraseña con requisitos de caracteres y cambio cada 3 meses | Define RF-01 a RF-04. El plazo de 3 meses tiene una observación abierta en DEC-08 |
| DEC-15 | 2026-11-08 | El Jefe puede bloquear a las personas desvinculadas del local | Define RF-06 |
| DEC-16 | Anterior | Tickets reemplaza a la antigua sección Bloqueos | El prototipo `kit/prototipo/index.html` quedó desactualizado |

## 2. Decisiones abiertas

| ID | Decisión pendiente | Opciones | Impacto en alcance | Impacto en tiempo | Impacto en costo | Qué bloquea | Recomendación |
|---|---|---|---|---|---|---|---|
| DEC-01 | ¿Cuál es el prototipo base? | (a) `mvp-web/index.html`, con inicio de sesión; (b) el prototipo v0.3.5 con selector de rol y Horarios, que no está en este repositorio | (a) no tiene carga de Excel; (b) no tiene inicio de sesión | Unificar ambos toma trabajo adicional | Ninguno | Toda la trazabilidad | (a), porque es el único disponible y verificado aquí. Si aparece (b), rescatar de ahí la pantalla de Horarios |
| DEC-02 | ¿Piloto en un local o en varios? | (a) Un local, con el modelo preparado para crecer; (b) varios desde el inicio | (b) agrega administración de locales y aislamiento | (b) alarga la fase de backend | (b) puede subir el costo de hosting | RNF-FLE-02 | (a). Era la preferencia inicial de Felipe |
| DEC-03 | ¿Quién ve y quién responde las consultas sin respuesta? | (a) Jefe responde, Asistente solo ve; (b) ambos responden; (c) solo el Jefe ve | Cambia permisos de RF-11 | Menor | Ninguno | HU-03 | (a), que es lo implementado como supuesto |
| DEC-04 | Evidencia en tareas con vencimiento: tipo, momento y quién revisa | (a) Foto al completar, revisa quien asignó; (b) foto o texto; (c) sin evidencia en la primera versión | (a) y (b) agregan cámara, almacenamiento y revisión | (a) y (b) agregan una fase | (a) y (b) requieren almacenamiento de archivos | RF-16, DAT-08, SEC-15 | (c) para la primera versión, y (a) después |
| DEC-05 | Formato de colación en la planilla oficial | Requiere revisar una planilla real sin RUT o una versión anonimizada | Sin esto no se puede validar colación | Bloquea el paso de Horarios | Ninguno | RF-22, RNF-COM-02 | Conseguir una planilla anonimizada antes de planificar Horarios |
| DEC-06 | Alcance sin conexión de la PWA | (a) Solo en línea, con mensajes claros; (b) lectura sin conexión de preguntas y tareas; (c) escritura sin conexión con sincronización | (c) agrega resolución de conflictos | (c) es la opción más cara en tiempo | Ninguno directo | RNF-FIA-03, SEC-14 | (a) para la primera versión |
| DEC-07 | Hosting, correo transaccional, almacenamiento y presupuesto | Comparar al menos 3 proveedores por costo, región, respaldos y operación | Define qué se puede desplegar | La comparación toma tiempo antes del piloto | Es la partida de costo principal | SEC-09, SEC-10, SEC-18, LEG-05 | Decidir solo si DEC-09 incluye un despliegue |
| DEC-08 | ¿Se mantiene el cambio de contraseña cada 90 días? | (a) Mantener; (b) seguir la guía NIST SP 800-63B: sin vencimiento, con revisión de contraseñas filtradas | (b) agrega esa revisión | Menor | (b) puede usar un servicio externo gratuito | RF-03 | Decide Felipe. Detalle en la matriz de seguridad, sección 6 |
| DEC-09 | ¿Qué entrega el Capstone? | (a) Prototipo validado con documentación y diseño del producto; (b) producto con backend en entorno local; (c) piloto desplegado en un local | (c) incluye todo lo de `Piloto` en el catálogo de calidad | Crece de (a) a (c) | (c) tiene costo de operación | Línea base completa | Depende de la fecha de entrega, que falta conocer |
| DEC-11 | Decisión identificada como "ID 6" | Postergada expresamente por Felipe. Su contenido no consta en este repositorio | — | — | — | — | Mantener postergada |
| DEC-17 | ¿El correo de la cuenta es personal o entregado por el local? | (a) Personal; (b) del local; (c) nombre de usuario sin correo | (c) cambia la recuperación de contraseña | Menor | (b) puede tener costo | DAT-02, LEG-03 | Por definir con la jefatura del local |
| DEC-18 | Texto definitivo de misión y visión | Validar o corregir las formulaciones propuestas | Documentación | Ninguno | Ninguno | Documentación general | Decide Felipe |

## 3. Cómo se registra una decisión nueva

1. Describir la decisión y sus opciones.
2. Anotar el impacto en alcance, tiempo y costo de cada opción.
3. Indicar qué requisitos, riesgos y fechas cambian.
4. Felipe elige. Se anota la fecha y pasa a "Decisiones tomadas".
5. Se actualizan la línea base, el catálogo de calidad y la trazabilidad afectados.
