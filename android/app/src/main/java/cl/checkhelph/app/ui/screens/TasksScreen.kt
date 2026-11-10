package cl.checkhelph.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.checkhelph.app.data.DemoData
import cl.checkhelph.app.data.Role

@Composable
fun TasksScreen(role: Role) {
    // Estado solo en memoria: se pierde al cerrar la app (aún sin Room ni backend).
    val done = remember { mutableStateMapOf<Int, Boolean>().apply { DemoData.tasks.forEach { put(it.id, it.done) } } }
    val permission = when (role) {
        Role.JEFE -> "Puedes crear, asignar y eliminar tareas de cualquier rol."
        Role.ASISTENTE -> "Puedes asignar tareas a Trabajadores y Colaboradores."
        Role.TRABAJADOR -> "Puedes completar tus tareas y crear tareas propias."
        Role.COLABORADOR -> "Puedes completar las tareas que te asignen."
    }

    ScreenScaffold("Tareas", permission) {
        items(DemoData.tasks, key = { it.id }) { task ->
            val checked = done[task.id] == true
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
            ) {
                Row(Modifier.padding(start = 4.dp, end = 16.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = checked, onCheckedChange = { done[task.id] = it })
                    Column(Modifier.weight(1f)) {
                        Text(task.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            buildString {
                                append("${task.area} · ${task.moment.label}")
                                task.dueLabel?.let { append(" · vence $it") }
                                if (task.needsEvidence) append(" · requiere evidencia")
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        item { DemoNotice("Pendiente de definir: tipo de evidencia, cuándo se pide y quién la revisa.") }
    }
}
