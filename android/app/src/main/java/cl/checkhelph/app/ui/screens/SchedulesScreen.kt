package cl.checkhelph.app.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SchedulesScreen(allowed: Boolean, onBack: () -> Unit) {
    ScreenScaffold("Horarios", "Revisión orientativa de planillas Excel.") {
        item {
            if (!allowed) {
                Text("No tienes acceso a esta sección.")
            } else {
                Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text("Cargar planillas (próximamente)")
                }
                DemoNotice(
                    "Pendiente: formato de colación y validación de reglas legales vigentes. " +
                        "El resultado será una herramienta de apoyo, no asesoría jurídica. No se leerán ni guardarán RUT."
                )
            }
            TextButton(onClick = onBack) { Text("Volver") }
        }
    }
}
