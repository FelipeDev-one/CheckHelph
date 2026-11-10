# Principios de arquitectura adaptable

| | |
|---|---|
| Versión | 0.1 (recreación) |
| Fecha | 2026-11-10 |
| Estado | **Por validar** por Felipe. El stack es un objetivo por evaluar, no una decisión tomada. |
| Origen | Recreado desde el contexto del proyecto. El archivo original no se encontró. |

## 1. Punto de partida real

| Pieza | Qué es | Qué no es |
|---|---|---|
| `mvp-web/index.html` | Prototipo web mobile-first en un archivo. Simula inicio de sesión, usuarios, preguntas, tareas y tickets con datos ficticios en `localStorage` | No tiene servidor, no comparte datos entre dispositivos y no es seguro para datos reales |
| `android/` | Maqueta nativa (Kotlin + Compose) usada para diseñar y validar flujos en Android | No es el producto final: el producto será un sitio web mobile-first |
| `CheckHelph.html` y `kit/prototipo/index.html` | Prototipos anteriores | Referencia visual; el segundo aún usa Bloqueos y Traspasos |

El producto real no existe todavía. Lo que sigue describe hacia dónde se propone ir.

## 2. Stack objetivo por evaluar

| Capa | Propuesta | Por qué | Alternativa |
|---|---|---|---|
| Frontend | React + TypeScript + Vite, como PWA | Un solo código para Android e iPhone; instalable desde el navegador | Mantener HTML y JavaScript sin framework si el alcance se reduce |
| Backend | Java + Spring Boot, monolito modular con API REST | Es el stack que Felipe ya usa en Duoc; un solo despliegue | Kotlin + Spring Boot, que comparte lógica con la maqueta Android |
| Datos | PostgreSQL + Flyway | Migraciones versionadas y reversibles | — |
| CI | GitHub Actions: compilar, probar, análisis estático, dependencias y secretos | Ya se usa GitHub | — |
| Hosting | **Sin decidir** (DEC-07) | Hay que comparar costo, región, respaldos y operación | Azure Container Apps + PostgreSQL administrado, entre otras |

## 3. Principios

**P1. El prototipo no se convierte en el producto.**
`mvp-web` sirve para validar flujos y criterios con Felipe y con usuarios. El producto se construye aparte y reutiliza las reglas ya probadas (turnos, política de contraseñas, búsqueda), no su almacenamiento.

**P2. Monolito modular primero.**
Un solo desplegable con módulos de límites claros. Un módulo se separa como servicio solo si hay evidencia operativa que lo justifique (carga, despliegues independientes, otro equipo).

**P3. Módulos con una interfaz pública cada uno.**

| Módulo | Responsabilidad | Depende de |
|---|---|---|
| `identidad` | Inicio de sesión, sesiones, contraseñas, recuperación | `notificaciones`, `auditoria` |
| `equipo` | Cuentas, roles, perfil, foto, bloqueo | `identidad`, `auditoria` |
| `tareas` | Crear, asignar, completar, evidencia | `equipo`, `archivos` |
| `preguntas` | Del turno, frecuentes, ingreso, búsqueda, consultas pendientes | `equipo` |
| `tickets` | Reporte, bandeja, estados, métricas | `equipo`, `notificaciones` |
| `horarios` | Reglas y parámetros. El análisis del Excel ocurre en el navegador | `equipo` |
| `notificaciones` | Envío por un adaptador de correo | — |
| `archivos` | Guardado por un adaptador de almacenamiento | — |
| `auditoria` | Registro de operaciones sensibles | — |

Ningún módulo usa las clases internas ni las tablas de otro. Se verifica con RNF-MAN-01.

**P4. El servidor decide; la interfaz solo muestra.**
Cada operación valida la sesión, el rol y el local en el servidor. Ocultar un botón es una ayuda de uso, no un control de acceso (SEC-02).

**P5. Preparado para varios locales, sin habilitarlo.**
Las tablas de negocio llevan `local_id` y las consultas filtran por él desde el primer día, porque agregarlo después es caro. La administración de varios locales no se construye hasta que Felipe lo decida (DEC-02).

**P6. Los proveedores externos van detrás de adaptadores.**
Correo, almacenamiento de archivos e identidad se usan mediante una interfaz propia. En desarrollo y en pruebas se usa un adaptador simulado. Cambiar de proveedor no toca los módulos de negocio (RNF-FLE-03).

**P7. Las reglas que cambian son parámetros con versión.**
Horarios del turno, días de cierre extendido, plazo de vencimiento de contraseñas y límites de la revisión de horarios se guardan como configuración versionada. Las reglas legales de horarios registran además su fuente y su fecha de vigencia.

**P8. Cambios trazables y reversibles.**
Esquema por migraciones; API documentada con OpenAPI; cada cambio cita el ID del requisito; nada llega a la rama principal con CI en rojo.

**P9. El dispositivo guarda lo mínimo.**
La PWA no guarda datos personales fuera de la sesión y limpia la caché al cerrar sesión (SEC-14). El alcance sin conexión se define antes de implementarlo (DEC-06).

**P10. Los horarios se analizan en el navegador.**
La planilla no se sube al servidor. Las columnas de RUT se descartan al leer. Se comprueba revisando el tráfico de red (SEC-13).

## 4. Vista general propuesta

```
Teléfono (navegador / PWA)
   │  HTTPS (TLS 1.3)
   ▼
API REST (Spring Boot, monolito modular)
   ├─ identidad ─ equipo ─ tareas ─ preguntas ─ tickets ─ horarios
   ├─ auditoria
   ├─ notificaciones ──► adaptador de correo ──► proveedor (por decidir)
   └─ archivos ────────► adaptador de almacenamiento ──► proveedor (por decidir)
   │
   ▼
PostgreSQL (migraciones con Flyway, respaldo diario)
```

## 5. Migración incremental propuesta

| Paso | Qué se hace | Qué queda demostrado |
|---|---|---|
| 0 | Validar historias y criterios con `mvp-web` (estado actual) | Flujos y reglas acordados |
| 1 | Backend mínimo: `identidad`, `equipo` y `auditoria`, con CI | Autenticación y autorización reales |
| 2 | Frontend React conectado a ese backend, solo con ingreso y perfil | La base del producto de punta a punta |
| 3 | `preguntas` | El diferenciador del producto con datos compartidos |
| 4 | `tareas` y `tickets`, con notificaciones por adaptador | Operación diaria |
| 5 | `horarios`, si DEC-05 se resuelve | Revisión orientativa |
| 6 | Despliegue de piloto, respaldo y monitoreo | Operación controlada en un local |

Cada paso deja algo que se puede mostrar y probar. Los pasos 5 y 6 son los primeros candidatos a quedar fuera si el tiempo no alcanza (ver línea base).

## 6. Supuestos

| ID | Supuesto | Si resulta falso |
|---|---|---|
| SUP-01 | El equipo usa teléfonos con navegadores actualizados | Habría que ampliar la compatibilidad (RNF-COM-01) |
| SUP-02 | El local tiene conexión a internet estable en sala | El alcance sin conexión (DEC-06) pasa a ser prioritario |
| SUP-03 | Un local tiene menos de 50 cuentas | Revisar RNF-PER-05 |
| SUP-04 | El piloto, si se hace, es en un solo local | Revisar P5 y DEC-02 |
| SUP-05 | Felipe desarrolla solo o con un equipo pequeño | El monolito modular sigue siendo la opción adecuada |

## 7. Decisiones pendientes que afectan la arquitectura

DEC-01 (base del prototipo), DEC-02 (un local o varios), DEC-04 (evidencia), DEC-05 (formato de colación), DEC-06 (alcance sin conexión), DEC-07 (hosting, correo y almacenamiento) y DEC-09 (alcance del Capstone). Están descritas en `docs/management/Registro_Decisiones_v0.1.md`.
