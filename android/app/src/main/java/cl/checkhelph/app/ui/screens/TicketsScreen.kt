package cl.checkhelph.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.checkhelph.app.data.DemoData
import cl.checkhelph.app.data.Role

@Composable
fun TicketsScreen(role: Role) {
    val manages = role == Role.JEFE || role == Role.ASISTENTE
    ScreenScaffold(
        "Tickets",
        if (manages) "Bandeja de fallas e incidentes del local." else "Reporta fallas, accidentes o incidentes.",
    ) {
        item {
            Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("Reportar incidente (próximamente)")
            }
            SectionTitle("Recientes")
        }
        items(DemoData.tickets, key = { it.id }) { t ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(t.type.label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text(t.context, style = MaterialTheme.typography.titleMedium)
                    Text(t.status, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item { DemoNotice("Las notificaciones (push o correo) al Jefe y la Asistente aún no están conectadas.") }
    }
}
