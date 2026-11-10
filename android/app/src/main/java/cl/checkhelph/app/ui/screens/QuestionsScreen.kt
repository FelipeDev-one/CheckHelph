package cl.checkhelph.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.NorthWest
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import cl.checkhelph.app.data.AppData
import cl.checkhelph.app.data.Moment
import cl.checkhelph.app.data.PendingQuery
import cl.checkhelph.app.data.Question
import cl.checkhelph.app.data.QuestionCategory
import cl.checkhelph.app.data.Role
import cl.checkhelph.app.data.User
import cl.checkhelph.app.domain.QuestionSearch
import cl.checkhelph.app.domain.ShiftSchedule
import cl.checkhelph.app.ui.AppViewModel
import java.time.LocalDateTime

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuestionsScreen(vm: AppViewModel, data: AppData, user: User) {
    val now by rememberNow()
    val moment = ShiftSchedule.momentAt(now)
    val focus = LocalFocusManager.current

    var tab by rememberSaveable { mutableStateOf(QuestionCategory.TURNO) }
    var query by rememberSaveable { mutableStateOf("") }
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    var sentQuery by rememberSaveable { mutableStateOf<String?>(null) }
    var answering by remember { mutableStateOf<PendingQuery?>(null) }

    val inTab = data.questions.filter { it.category == tab }
    val hasKeywords = QuestionSearch.tokens(query).isNotEmpty()
    val results = QuestionSearch.search(query, inTab)
    val selected = data.questions.firstOrNull { it.id == selectedId }

    fun choose(q: Question) {
        selectedId = q.id
        query = q.question
        vm.registerUse(q.id)
        focus.clearFocus()
    }

    ScreenScaffold("Preguntas", "Escribe tu duda o elige una sugerida.") {
        item { ShiftBanner(now, moment) }

        if (user.role == Role.JEFE || user.role == Role.ASISTENTE) {
            if (data.pending.isNotEmpty()) {
                item { SectionTitle("Consultas sin respuesta (${data.pending.size})") }
                items(data.pending, key = { it.id }) { p ->
                    PendingCard(p, canAnswer = user.role == Role.JEFE, onAnswer = { answering = p }, onDismiss = { vm.dismissPending(p.id) })
                }
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            PrimaryTabRow(selectedTabIndex = tab.ordinal) {
                QuestionCategory.entries.forEach { c ->
                    Tab(selected = tab == c, onClick = { tab = c; selectedId = null; sentQuery = null }, text = { Text(c.label) })
                }
            }
            OutlinedTextField(
                value = query,
                onValueChange = { query = it; selectedId = null; sentQuery = null },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) IconButton(onClick = { query = ""; selectedId = null; sentQuery = null }) {
                        Icon(Icons.Outlined.Close, contentDescription = "Borrar búsqueda")
                    }
                },
                label = { Text("Escribe tu duda") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { results.firstOrNull()?.let(::choose) ?: focus.clearFocus() }),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            )
        }

        when {
            // 1) Respuesta elegida
            selected != null -> item { AnswerCard(selected) }

            // 2) Autocompletado mientras se escribe
            hasKeywords && results.isNotEmpty() -> {
                item { Text("Sugerencias", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) }
                items(results.take(5), key = { it.id }) { q -> SuggestionRow(q, onClick = { choose(q) }) }
            }

            // 3) Sin resultados: Del turno -> Frecuentes -> Consultar al jefe
            hasKeywords -> item {
                NoAnswer(
                    tab = tab,
                    sent = sentQuery == query,
                    onGoToFaq = { tab = QuestionCategory.FRECUENTE },
                    onSend = { vm.addPending(query, user.name); sentQuery = query },
                )
            }

            // 4) Sin escribir: sugerencias del momento del turno y lista completa
            else -> {
                if (tab == QuestionCategory.TURNO) {
                    val suggested = QuestionSearch.forMoment(moment, data.questions)
                    item {
                        SectionTitle(moment?.let { "Sugeridas para ${it.label.lowercase()}" } ?: "Más consultadas")
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            suggested.forEach { q -> SuggestionChip(onClick = { choose(q) }, label = { Text(q.question) }) }
                        }
                        SectionTitle("Todas las preguntas del turno")
                    }
                }
                items(inTab.sortedByDescending { it.uses }, key = { it.id }) { q -> ExpandableQuestion(q, onOpen = { vm.registerUse(q.id) }) }
                if (inTab.isEmpty()) item { Text("Aún no hay preguntas en esta sección.") }
            }
        }
    }

    answering?.let { p ->
        AnswerDialog(
            pending = p,
            defaultMoment = moment,
            onCancel = { answering = null },
            onSave = { question, answer, category, m ->
                vm.answerPending(p.id, question, answer, category, m)
                answering = null
            },
        )
    }
}

@Composable
private fun ShiftBanner(now: LocalDateTime, moment: Moment?) {
    val date = now.toLocalDate()
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.padding(start = 12.dp)) {
                Text(
                    moment?.let { "Ahora: ${it.label} · ${ShiftSchedule.rangeLabel(it, date)}" } ?: "Local cerrado",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    "Hoy el cierre es a las ${ShiftSchedule.closingTime(date)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun SuggestionRow(q: Question, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(q.question, style = MaterialTheme.typography.bodyLarge)
                q.moment?.let { Text(it.label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary) }
            }
            Icon(Icons.Outlined.NorthWest, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun AnswerCard(q: Question) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).semantics { liveRegion = LiveRegionMode.Polite },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(Modifier.padding(22.dp)) {
            Text(q.moment?.label ?: q.category.label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary)
            Text(q.question, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSecondary)
            Spacer(Modifier.height(10.dp))
            Text(q.answer, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSecondary)
        }
    }
}

@Composable
private fun NoAnswer(tab: QuestionCategory, sent: Boolean, onGoToFaq: () -> Unit, onSend: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
    ) {
        Column(Modifier.padding(18.dp).semantics { liveRegion = LiveRegionMode.Polite }) {
            if (tab == QuestionCategory.TURNO) {
                Text("No encontramos respuesta en las preguntas del turno.", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onGoToFaq, modifier = Modifier.fillMaxWidth().height(48.dp)) { Text("Buscar en Preguntas frecuentes") }
            } else if (sent) {
                Text("Consulta enviada", style = MaterialTheme.typography.titleMedium)
                Text("Tu jefe de local la verá y agregará la respuesta.", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text("Consulta con tu jefe de local", style = MaterialTheme.typography.titleMedium)
                Text("Puedes dejar registrada tu duda para que agregue la respuesta.", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onSend, modifier = Modifier.fillMaxWidth().height(48.dp)) { Text("Enviar consulta al jefe") }
            }
        }
    }
}

@Composable
private fun ExpandableQuestion(q: Question, onOpen: () -> Unit) {
    var open by rememberSaveable(q.id) { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp).animateContentSize()
            .clickable { if (!open) onOpen(); open = !open },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
    ) {
        Column(Modifier.padding(16.dp)) {
            q.moment?.let { Text(it.label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary) }
            Text(q.question, style = MaterialTheme.typography.titleMedium)
            if (open) Text(q.answer, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun PendingCard(p: PendingQuery, canAnswer: Boolean, onAnswer: () -> Unit, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(p.text, style = MaterialTheme.typography.titleMedium)
            Text("Consultó: ${p.askedBy}", style = MaterialTheme.typography.bodySmall)
            if (canAnswer) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    Button(onClick = onAnswer) { Text("Responder") }
                    TextButton(onClick = onDismiss) { Text("Descartar") }
                }
            } else {
                Text("Solo el Jefe de local puede responder.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnswerDialog(
    pending: PendingQuery,
    defaultMoment: Moment?,
    onCancel: () -> Unit,
    onSave: (String, String, QuestionCategory, Moment?) -> Unit,
) {
    var question by rememberSaveable { mutableStateOf(pending.text) }
    var answer by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(QuestionCategory.TURNO) }
    var moment by rememberSaveable { mutableStateOf(defaultMoment) }

    AlertDialog(
        onDismissRequest = onCancel,
        // No se cierra al tocar fuera, para no perder la respuesta escrita.
        properties = DialogProperties(dismissOnClickOutside = false),
        title = { Text("Responder consulta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(question, { question = it }, label = { Text("Pregunta") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(answer, { answer = it }, label = { Text("Respuesta") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                Text("Publicar en", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    QuestionCategory.entries.forEach { c -> FilterChip(category == c, { category = c }, { Text(c.label) }) }
                }
                if (category == QuestionCategory.TURNO) {
                    Text("Momento del turno", style = MaterialTheme.typography.labelLarge)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(moment == null, { moment = null }, { Text("Cualquiera") })
                        Moment.entries.forEach { m -> FilterChip(moment == m, { moment = m }, { Text(m.label) }) }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(question, answer, category, if (category == QuestionCategory.TURNO) moment else null) },
                enabled = question.isNotBlank() && answer.isNotBlank(),
            ) { Text("Publicar") }
        },
        dismissButton = { OutlinedButton(onClick = onCancel) { Text("Cancelar") } },
    )
}
