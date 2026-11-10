package cl.checkhelph.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.checkhelph.app.data.AppData
import cl.checkhelph.app.data.Role
import cl.checkhelph.app.data.User
import cl.checkhelph.app.security.PasswordPolicy
import cl.checkhelph.app.ui.AppViewModel
import kotlinx.coroutines.launch

/** Solo Jefe de local: crear cuentas, bloquear a personas desvinculadas y asistir en la recuperación. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UsersScreen(vm: AppViewModel, data: AppData, me: User, onBack: () -> Unit) {
    if (me.role != Role.JEFE) {
        ScreenScaffold("Usuarios") { item { Text("No tienes acceso a esta sección.") } }
        return
    }
    val scope = rememberCoroutineScope()
    var confirmBlock by remember { mutableStateOf<User?>(null) }
    var tempInfo by remember { mutableStateOf<Pair<String, String>?>(null) } // (correo, contraseña temporal)
    var creating by remember { mutableStateOf(false) }
    val now = System.currentTimeMillis()

    ScreenScaffold("Usuarios del local", "${data.users.count { it.active }} activos · ${data.users.count { !it.active }} bloqueados") {
        item {
            Button(onClick = { creating = true }, modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("Crear cuenta") }
            SectionTitle("Equipo")
        }
        items(data.users.sortedWith(compareByDescending<User> { it.active }.thenBy { it.role.ordinal }), key = { it.id }) { u ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (u.active) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
                ),
                border = CardDefaults.outlinedCardBorder(),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(u.name, style = MaterialTheme.typography.titleMedium)
                    Text("${u.role.label} · ${u.email}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (!u.active) AssistChip(onClick = {}, label = { Text("Bloqueado") })
                        if (u.lockedUntil > now) AssistChip(onClick = {}, label = { Text("Bloqueo temporal por intentos") })
                        if (u.mustChangePassword) AssistChip(onClick = {}, label = { Text("Clave temporal") })
                        else if (PasswordPolicy.isExpired(u.passwordChangedAt, now)) AssistChip(onClick = {}, label = { Text("Clave vencida") })
                    }
                    if (u.id != me.id) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (u.active) {
                                OutlinedButton(onClick = { confirmBlock = u }) { Text("Bloquear") }
                                TextButton(onClick = { scope.launch { tempInfo = u.email to vm.adminResetPassword(u.id) } }) { Text("Clave temporal") }
                            } else {
                                OutlinedButton(onClick = { vm.setActive(u.id, true) }) { Text("Reactivar") }
                            }
                        }
                    }
                }
            }
        }
        item {
            DemoNotice("Bloquear impide ingresar y cierra su sesión de inmediato; sus registros se conservan. Úsalo cuando alguien deja el local.")
            TextButton(onClick = onBack) { Text("Volver") }
        }
    }

    confirmBlock?.let { u ->
        AlertDialog(
            onDismissRequest = { confirmBlock = null },
            title = { Text("¿Bloquear a ${u.name}?") },
            text = { Text("No podrá ingresar a CheckHelph. Puedes reactivar la cuenta más adelante.") },
            confirmButton = { Button(onClick = { vm.setActive(u.id, false); confirmBlock = null }) { Text("Bloquear") } },
            dismissButton = { TextButton(onClick = { confirmBlock = null }) { Text("Cancelar") } },
        )
    }

    tempInfo?.let { (email, temp) ->
        AlertDialog(
            onDismissRequest = { tempInfo = null },
            title = { Text("Contraseña temporal") },
            text = {
                Text("Entrégala en persona a $email:\n\n$temp\n\nDeberá cambiarla al ingresar. En la versión real se enviará un enlace por correo.")
            },
            confirmButton = { Button(onClick = { tempInfo = null }) { Text("Listo") } },
        )
    }

    if (creating) {
        CreateUserDialog(
            onCancel = { creating = false },
            onCreate = { name, email, role, onError ->
                scope.launch {
                    val (error, temp) = vm.createUser(name, email, role)
                    if (error != null) onError(error) else { creating = false; tempInfo = email.trim().lowercase() to temp!! }
                }
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateUserDialog(onCancel: () -> Unit, onCreate: (String, String, Role, (String) -> Unit) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(Role.COLABORADOR) }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Crear cuenta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    email, { email = it }, label = { Text("Correo") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth(),
                )
                Text("Rol", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Role.entries.forEach { r -> FilterChip(role == r, { role = r }, { Text(r.label) }) }
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = { Button(onClick = { error = null; onCreate(name, email, role) { error = it } }) { Text("Crear") } },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancelar") } },
    )
}
