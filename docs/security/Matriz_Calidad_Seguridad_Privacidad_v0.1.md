# Matriz de seguridad y privacidad

| | |
|---|---|
| Versión | 0.1 (recreación) |
| Fecha | 2026-11-10 |
| Estado | **Por validar** por Felipe |
| Origen | Recreado desde el contexto del proyecto. El archivo original con este nombre no se encontró en GitHub ni en el equipo de trabajo; si aparece, hay que reconciliar ambos. |

## 1. Alcance y advertencias

- Este documento es una propuesta de controles con su amenaza, prueba y evidencia. **No afirma cumplimiento legal, certificación ISO/IEC 27001 ni aplicabilidad jurídica.** Eso requiere una evaluación formal.
- Las referencias a las leyes 21.719 y 21.663 son orientación de diseño, no interpretación legal. Antes de usarlas en la documentación del Capstone hay que revisar el texto vigente en una fuente oficial (bcn.cl, Diario Oficial).
- Los prototipos actuales (`mvp-web/` y `android/`) guardan todo en el dispositivo y **no deben usarse con datos reales**.

## 2. Qué hay que definir antes de hablar de cumplimiento

| ID | Pregunta | Por qué importa | Estado |
|---|---|---|---|
| LEG-01 | ¿Qué entidad operaría CheckHelph: Felipe como persona, Duoc UC en contexto académico o la empresa dueña del local? | Define quién es el responsable del tratamiento de datos y quién responde ante incidentes | Pendiente |
| LEG-02 | ¿El piloto usará datos de trabajadores reales o solo datos sintéticos? | Con datos sintéticos, gran parte de las obligaciones de datos personales no se activa | Pendiente |
| LEG-03 | ¿Cuál es la base de licitud para tratar nombre, correo y foto de los trabajadores? | Los datos de trabajadores los entrega el empleador; el consentimiento en una relación laboral es una base débil | Pendiente, requiere asesoría |
| LEG-04 | ¿La Ley 21.663 aplica al operador? | La ley se dirige a prestadores de servicios esenciales y operadores de importancia vital. Una herramienta interna de un local de retail probablemente no califica, pero no se debe afirmar sin revisar la calificación vigente | Pendiente de revisión |
| LEG-05 | ¿Dónde se alojarán los datos (país y proveedor)? | Afecta transferencias internacionales, contratos con encargados y costo | Pendiente (DEC-07) |

**Vigencia de la Ley 21.719:** según la información del contexto del proyecto, entra en vigencia el 1 de diciembre de 2026. Conviene volver a comprobarlo en la fuente oficial, porque esa fecha está a pocas semanas de la de este documento.

## 3. Inventario de datos (propuesta)

| ID | Dato | Finalidad | Quién accede | Conservación propuesta | Notas |
|---|---|---|---|---|---|
| DAT-01 | Nombre | Identificar a la persona en tareas, tickets y consultas | Todo el equipo del local | Mientras la cuenta exista; anonimizar 90 días después de la baja (?) | Dato personal |
| DAT-02 | Correo | Iniciar sesión, recuperar la contraseña y recibir notificaciones | La persona y el Jefe | Igual que DAT-01 | El contexto original no lo incluía en el perfil mínimo; se agrega porque el inicio de sesión lo exige. **Por validar** si debe ser correo personal o uno entregado por el local |
| DAT-03 | Rol | Autorización | Todo el equipo | Igual que DAT-01 | |
| DAT-04 | Foto de perfil | Reconocer a la persona | Todo el equipo; solo el Jefe la cambia | Eliminar al dar de baja la cuenta | Opcional. Evaluar si es necesaria: minimizar datos |
| DAT-05 | Hash de contraseña e historial de hashes | Autenticación y evitar reutilización | Solo el sistema | Hash actual y los 3 anteriores | Nunca la contraseña |
| DAT-06 | Registro de auditoría | Trazar operaciones sensibles | Jefe (lectura) y administración técnica | 12 meses (?) | Contiene identificadores de usuario |
| DAT-07 | Texto de tickets | Gestionar fallas e incidentes | Autor, Jefe y Asistente | 12 meses (?) | **Riesgo:** un ticket de accidente puede incluir datos de salud, que son datos sensibles. Ver PRI-05 |
| DAT-08 | Fotos de evidencia | Comprobar tareas con vencimiento | Autor y quien revisa | 30 días (?) | Pueden mostrar personas o clientes. Pendiente DEC-04 |
| DAT-09 | Consultas sin respuesta | Que el Jefe agregue respuestas | Jefe y Asistente | Hasta responder o descartar | Texto libre: puede contener datos personales |
| DAT-10 | Planillas de horarios | Revisión orientativa | Solo en el navegador de quien las carga | No se conservan | Contienen RUT: no leer, guardar ni registrar esas columnas |

**Regla permanente:** no incorporar RUT, planillas reales, correos personales, incidentes reales ni capturas identificables en el repositorio, los ejemplos, las pruebas o la documentación.

## 4. Controles de seguridad

Cada control indica la amenaza que atiende, cómo se prueba y qué evidencia deja. "Estado en prototipos" describe lo que ya existe en `mvp-web/` y `android/`, que es una simulación del lado del cliente.

### 4.1 Identidad y acceso

| ID | Amenaza | Control propuesto | Prueba | Evidencia | Estado en prototipos |
|---|---|---|---|---|---|
| SEC-01 | Uso de la aplicación sin identificarse | Sesión emitida por el servidor en cookie `HttpOnly`, `Secure` y `SameSite=Lax`; expira por inactividad (8 h) y por tiempo máximo (12 h) (?); protección CSRF | Prueba negativa por endpoint sin cookie, con cookie vencida y con cookie alterada | Reporte de pruebas automatizadas | No aplica: no hay servidor |
| SEC-02 | Un usuario hace algo que su rol no permite, o ve datos de otro local | El servidor valida rol y local en cada operación; la interfaz solo oculta opciones | Matriz rol × operación automatizada; intento de acceder a un recurso de otro local | Reporte con la matriz completa | Solo oculta opciones; no es un control real |
| SEC-03 | Contraseñas débiles o reutilizadas | Mínimo 10 caracteres con mayúscula, minúscula, número y símbolo; sin el correo ni palabras comunes; sin repetir las 3 anteriores; revisar contra una lista de contraseñas filtradas | Pruebas unitarias de la política | Reporte de pruebas | Implementado, salvo la lista de filtradas (EV-002, EV-003) |
| SEC-04 | Robo de la base de datos expone las contraseñas | Hash lento con sal individual: **Argon2id** en el backend, calibrado para tardar entre 250 y 500 ms en el servidor real. Las contraseñas nunca se cifran de forma reversible | Revisión del formato guardado; prueba de tiempo de cálculo; búsqueda de texto plano en base y logs | Reporte de calibración y de búsqueda | Los prototipos usan PBKDF2-HMAC-SHA256 porque es lo disponible en navegador y Android. Es aceptable para una maqueta, no es el objetivo |
| SEC-05 | Adivinar contraseñas por fuerza bruta | Bloqueo de 15 minutos tras 5 intentos fallidos por cuenta, más límite de intentos por IP; el mensaje de error no revela si el correo existe | Prueba automatizada de 5 intentos y de espera; prueba de mensajes iguales | Reporte de pruebas | Implementado por cuenta (EV-002). Sin límite por IP |
| SEC-06 | Persona desvinculada sigue entrando | El Jefe bloquea la cuenta; el servidor invalida sus sesiones de inmediato; la cuenta se conserva para no perder el historial | Prueba: sesión abierta, bloqueo, la siguiente petición es rechazada | Reporte de pruebas | Implementado en simulación (EV-002) |
| SEC-07 | Tomar control de una cuenta por la recuperación de contraseña | Token aleatorio de al menos 128 bits, de un solo uso, vence en 15 minutos, se guarda solo su hash y se envía al correo verificado; la respuesta es igual exista o no la cuenta; la recuperación asistida por el Jefe entrega una contraseña temporal de un solo uso | Pruebas: token reutilizado, vencido, alterado; respuesta idéntica para correo inexistente | Reporte de pruebas | Simulado con código de 6 dígitos mostrado en pantalla. **No es seguro**: es solo para la demostración |
| SEC-08 | No saber quién hizo una operación sensible | Registro de auditoría de solo inserción: quién, qué, sobre qué, cuándo (hora del servidor) y resultado. Sin contraseñas ni contenido sensible | Prueba por operación sensible; intento de modificar o borrar un registro | Reporte de pruebas y muestra del registro con datos sintéticos | No implementado |

### 4.2 Criptografía

| ID | Amenaza | Control propuesto | Prueba | Evidencia | Estado en prototipos |
|---|---|---|---|---|---|
| SEC-09 | Intercepción del tráfico | HTTPS obligatorio con TLS 1.3; TLS 1.2 solo si un dispositivo del equipo lo exige y con suites robustas; HSTS; redirección de HTTP a HTTPS | Escaneo de configuración TLS (por ejemplo, SSL Labs o `testssl.sh`) | Reporte del escaneo | No aplica |
| SEC-10 | Acceso a discos o respaldos | Cifrado en reposo administrado por el proveedor para base, archivos y respaldos. Cifrado adicional a nivel de aplicación (AES-256-GCM) solo si un campo lo justifica; hoy el candidato es el texto de tickets de accidente | Revisión de la configuración del proveedor | Captura de la configuración sin secretos | No aplica |
| SEC-10b | Fuga de claves | Claves en un servicio de gestión de claves (KMS o Key Vault), separadas de los datos, con mínimo privilegio, rotación y recuperación probada. Nunca en Git, en el frontend, en logs ni en imágenes de contenedor | Revisión de accesos; simulacro de rotación | Acta del simulacro | No aplica |

No se usará criptografía propia ni algoritmos no estándar.

### 4.3 Aplicación y datos

| ID | Amenaza | Control propuesto | Prueba | Evidencia | Estado en prototipos |
|---|---|---|---|---|---|
| SEC-11 | Dependencias con vulnerabilidades conocidas | Análisis de dependencias en CI en cada push | Revisión de la ejecución de CI | Historial de CI | No implementado |
| SEC-12 | Secretos en el repositorio | Detección de secretos en CI; `.gitignore` para keystores, `.env` y `local.properties` | Ejecutar el detector sobre todo el historial | Reporte del detector | `.gitignore` de Android listo; sin detector |
| SEC-13 | Exposición de RUT u horarios al analizar planillas | El Excel se procesa solo en el navegador; las columnas de RUT se descartan al leer; nada se envía al servidor ni se guarda | Revisar el tráfico de red durante la carga (0 peticiones con contenido de la planilla); buscar patrones de RUT en almacenamiento y logs | Captura del panel Red y resultado de la búsqueda | No implementado en los prototipos actuales |
| SEC-14 | Datos que quedan en un teléfono compartido o perdido | Guardar en el dispositivo lo mínimo; al cerrar sesión, borrar caché y datos locales; no tratar `localStorage` como almacenamiento seguro | Prueba: cerrar sesión y revisar el almacenamiento del navegador | Captura del almacenamiento vacío | **No cumple**: el MVP guarda todo en `localStorage`, que es justamente lo que una versión real debe evitar |
| SEC-15 | Archivos maliciosos o fotos con datos de más | Validar tipo y tamaño en el servidor; quitar metadatos EXIF (incluida la ubicación); servir los archivos con URL firmadas de corta duración | Pruebas con archivos de tipo falso y con EXIF | Reporte de pruebas | No aplica |
| SEC-16 | Inyección de código en textos libres (XSS, inyección SQL) | Escapar todo texto al mostrarlo; consultas parametrizadas; política de seguridad de contenido (CSP) | Pruebas con cargas de XSS en preguntas, tickets y nombres | Reporte de pruebas | El MVP escapa los textos; sin CSP |
| SEC-17 | Datos personales en logs | Los logs no incluyen contraseñas, tokens, correos completos ni texto de tickets | Revisión de logs tras ejecutar los flujos críticos | Muestra de logs con datos sintéticos | No aplica |

### 4.4 Operación

| ID | Amenaza | Control propuesto | Prueba | Evidencia | Estado |
|---|---|---|---|---|---|
| SEC-18 | Pérdida de datos | Respaldo diario automático, cifrado, con retención de 14 días (?) y restauración probada | Simulacro de restauración (RNF-FIA-02) | Acta del simulacro | Pendiente |
| SEC-19 | Incidente de seguridad sin respuesta | Procedimiento breve: a quién avisar, cómo contener, qué registrar y cuándo notificar a los afectados | Simulacro de mesa | Acta del simulacro | Pendiente |

## 5. Privacidad

| ID | Requisito | Prueba | Evidencia | Estado |
|---|---|---|---|---|
| PRI-01 | Minimización: solo se piden nombre, correo, rol y foto opcional | Revisión del modelo de datos y de los formularios | Modelo de datos | Por validar |
| PRI-02 | Información clara: al primer ingreso, la persona ve qué datos se tratan, para qué y quién los ve | Revisión de la pantalla | Captura | Pendiente |
| PRI-03 | Derechos de la persona: existe un procedimiento para pedir acceso, rectificación y eliminación, y un responsable de responder | Revisión del procedimiento | Procedimiento escrito | Pendiente (depende de LEG-01) |
| PRI-04 | Conservación: cada dato tiene un plazo y un proceso de eliminación o anonimización | Prueba del proceso con datos sintéticos | Reporte | Pendiente |
| PRI-05 | Tickets de accidente: el formulario pide describir el hecho sin diagnósticos ni datos de salud de terceros; el acceso se limita a autor, Jefe y Asistente | Revisión de la pantalla y prueba de acceso por rol | Captura y reporte | Pendiente |
| PRI-06 | Ningún dato real en el repositorio ni en la evidencia | Búsqueda automática de patrones de RUT y correos no ficticios | Reporte de la búsqueda | Ver EV-004 |

## 6. Punto que merece una decisión: cambio de contraseña cada 90 días

Felipe pidió que la contraseña se cambie cada 3 meses, y así está implementado en los prototipos. La guía NIST SP 800-63B recomienda lo contrario: no forzar cambios periódicos y exigir el cambio solo cuando hay indicios de que la contraseña se filtró, porque los cambios forzados llevan a contraseñas predecibles. ISO/IEC 27001 no fija un plazo.

Opciones:

- **Mantener los 90 días:** es simple de explicar y es común en políticas internas. Costo: más recuperaciones de contraseña y más carga para el Jefe.
- **Seguir la guía NIST:** sin vencimiento, pero con revisión contra contraseñas filtradas y cambio obligatorio ante sospecha. Costo: hay que integrar esa revisión.

Queda registrado como DEC-08 en el registro de decisiones. El plazo es un parámetro, así que cambiarlo no afecta el resto del diseño.
