# CheckHelph · maqueta Android

Maqueta funcional en Kotlin + Jetpack Compose (Material 3) para diseñar y validar los flujos de CheckHelph en teléfonos Android. **El producto final será un sitio web mobile-first**; esta app sirve para probar la experiencia y las reglas antes de construirlo.

## Abrir y ejecutar

1. En Android Studio: **File → Open** y selecciona esta carpeta `android/`.
2. Espera el Gradle sync (genera `local.properties` con la ruta del SDK; no se versiona).
3. Ejecuta la configuración `app` en un emulador o teléfono (Android 8.0 o superior).

Por consola: `./gradlew assembleDebug` (APK en `app/build/outputs/apk/debug/`) y `./gradlew testDebugUnitTest` (pruebas).

## Cuentas de prueba (ficticias)

Todas usan la contraseña de demostración definida en `DemoSeed.DEMO_PASSWORD` (`app/src/main/java/cl/checkhelph/app/data/DemoSeed.kt`).

| Correo | Rol | Para probar |
|---|---|---|
| jefe@checkhelph.test | Jefe de local | Usuarios, bloqueo, responder consultas |
| asistente@checkhelph.test | Asistente | Ve consultas sin respuesta, no responde |
| trabajador@checkhelph.test | Trabajador | Uso normal |
| colaborador@checkhelph.test | Colaborador | Contraseña vencida (95 días): cambio obligatorio |
| desvinculado@checkhelph.test | Trabajador | Cuenta bloqueada: no puede ingresar |

Para volver a los datos iniciales: desinstala la app o borra sus datos (Ajustes → Apps → CheckHelph → Almacenamiento).

## Qué incluye

**Inicio de sesión y contraseñas**
- Ingreso con correo y contraseña. El mensaje de error no revela si el correo existe.
- Contraseña de al menos 10 caracteres, con mayúscula, minúscula, número y símbolo, sin el correo ni palabras comunes. Los requisitos se marcan en vivo.
- Vence a los 90 días: al ingresar se obliga a cambiarla. No se pueden repetir las últimas 3.
- Bloqueo temporal de 15 minutos tras 5 intentos fallidos.
- "Olvidé mi contraseña": código de 6 dígitos válido por 15 minutos (máximo 5 intentos). **Maqueta:** el código se muestra en pantalla porque el correo aún no está conectado.
- Contraseñas guardadas con hash PBKDF2-HMAC-SHA256 y sal por usuario; nunca en texto plano.

**Usuarios (solo Jefe de local)**
- Crear cuentas con contraseña temporal, que debe cambiarse al primer ingreso.
- Bloquear a personas desvinculadas: no pueden entrar y su sesión se cierra de inmediato. Se pueden reactivar.
- Asignar una contraseña temporal para ayudar en la recuperación.

**Preguntas**
- Momento del turno según la hora:
  - Apertura: 08:30–12:00.
  - Intermedio: 12:00–18:00.
  - Cierre: 18:00–21:30 los lunes, viernes, sábados y el último día del mes; 18:00–21:00 el resto de los días.
- Sugerencias del momento actual, ordenadas por las más usadas.
- Autocompletado mientras se escribe; la búsqueda ignora mayúsculas y tildes.
- Flujo sin respuesta: Del turno → Frecuentes → "Consulta con tu jefe de local" → la consulta queda registrada.
- El Jefe ve y responde las consultas pendientes y elige dónde publicarlas. La Asistente solo las ve.

## Qué NO incluye todavía

- Servidor: todo se guarda en un archivo privado del teléfono, así que los datos no se comparten entre dispositivos.
- Envío real de correos.
- Tareas y tickets persistentes.
- Cámara, notificaciones y lectura de Excel.

En el producto real, la autenticación, el bloqueo y la recuperación deben validarse en el servidor.

## Versiones

`minSdk 26` (Android 8.0), `targetSdk/compileSdk 37`, AGP 9.4.1, Kotlin 2.4.21, Compose BOM 2026.09.00, Gradle 9.6.0.
