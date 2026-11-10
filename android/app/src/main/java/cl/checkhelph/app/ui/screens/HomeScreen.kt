package cl.checkhelph.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cl.checkhelph.app.ui.theme.Petroleo
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.checkhelph.app.R
import cl.checkhelph.app.data.DemoData
import cl.checkhelph.app.data.DemoTask
import cl.checkhelph.app.data.User
import cl.checkhelph.app.domain.ShiftSchedule
import androidx.compose.runtime.getValue

/**
 * Inicio inspirado en la referencia visual: marca centrada, mucho espacio en blanco,
 * un bloque protagonista (la próxima tarea) y un carrusel horizontal discreto.
 */
@Composable
fun HomeScreen(user: User, onOpenTasks: () -> Unit, onOpenQuestions: () -> Unit) {
    val now by rememberNow()
    val moment = ShiftSchedule.momentAt(now)
    // Primero las tareas del momento actual del turno.
    val pending = DemoData.tasks.filter { !it.done }.sortedBy { if (it.moment == moment) 0 else 1 }
    val next = pending.firstOrNull()

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 24.dp)) {
        item {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(color = Petroleo, shape = RoundedCornerShape(18.dp)) {
                    Icon(
                        painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(64.dp),
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text("Hola, ${user.name.substringBefore(' ')}", style = MaterialTheme.typography.titleLarge)
                Text(
                    (moment?.let { "${it.label} · ${ShiftSchedule.rangeLabel(it, now.toLocalDate())}" } ?: "Local cerrado") +
                        " · ${pending.size} tareas pendientes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(28.dp))
        }

        if (next != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Próxima tarea", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            next.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSecondary,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "${next.area} · ${next.moment.label}" + (next.dueLabel?.let { " · vence $it" } ?: ""),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(20.dp))
                        Button(onClick = onOpenTasks) { Text("Ver tareas") }
                    }
                }
            }
        }

        item {
            Text(
                "Pendientes del turno",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 20.dp, top = 28.dp, bottom = 10.dp),
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(pending, key = { it.id }) { TaskMiniCard(it) }
            }
        }

        item {
            Spacer(Modifier.height(28.dp))
            OutlinedButton(
                onClick = onOpenQuestions,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(52.dp),
            ) { Text("Tengo una duda del turno") }
        }
    }
}

@Composable
private fun TaskMiniCard(task: DemoTask) {
    Card(
        modifier = Modifier.width(260.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(task.moment.label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text(task.title, style = MaterialTheme.typography.titleMedium, minLines = 2, maxLines = 2)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                task.dueLabel?.let {
                    AssistChip(onClick = {}, label = { Text(it, maxLines = 1) }, leadingIcon = { Icon(Icons.Outlined.Schedule, null, Modifier.size(16.dp)) })
                }
                if (task.needsEvidence) {
                    AssistChip(onClick = {}, label = { Text("Evidencia", maxLines = 1) }, leadingIcon = { Icon(Icons.Outlined.CameraAlt, null, Modifier.size(16.dp)) })
                }
            }
        }
    }
}
