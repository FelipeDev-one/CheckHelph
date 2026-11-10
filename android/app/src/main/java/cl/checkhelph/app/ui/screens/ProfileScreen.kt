package cl.checkhelph.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cl.checkhelph.app.data.Role
import cl.checkhelph.app.data.User
import cl.checkhelph.app.security.PasswordPolicy
import cl.checkhelph.app.ui.AppViewModel
import cl.checkhelph.app.ui.auth.PasswordField
import cl.checkhelph.app.ui.auth.PasswordRules
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    user: User,
    assistantCanSeeSchedules: Boolean,
    onAssistantSchedulesChange: (Boolean) -> Unit,
    canSeeSchedules: Boolean,
    onOpenSchedules: () -> Unit,
    onOpenUsers: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit,
) {
    val days = PasswordPolicy.daysLeft(user.passwordChangedAt, System.currentTimeMillis())
    ScreenScaffold(user.name, "${user.role.label} · ${user.email}") {
        item {
            SectionTitle("Seguridad")
            Text(
                if (days <= 7) "Tu contraseña vence en $days días. Cámbiala pronto." else "Tu contraseña vence en $days días.",
                style = MaterialTheme.typography.bodyMedium,
                color = if (days <= 7) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedButton(onClick = onChangePassword, modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("Cambiar contraseña") }

            if (user.role == Role.JEFE) {
                SectionTitle("Administración")
                OutlinedButton(onClick = onOpenUsers, modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("Usuarios del local") }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Asistente puede ver Horarios", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    Switch(checked = assistantCanSeeSchedules, onCheckedChange = onAssistantSchedulesChange)
                }
            }
            if (canSeeSchedules) {
                SectionTitle("Gestión")
                OutlinedButton(onClick = onOpenSchedules, modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("Revisión de horarios") }
            }

            SectionTitle("Sesión")
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("Cerrar sesión") }
            DemoNotice("Maqueta: los datos se guardan solo en este teléfono. La versión real validará todo en el servidor.")
        }
    }
}

@Composable
fun ChangePasswordScreen(vm: AppViewModel, user: User, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var current by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var done by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }

    ScreenScaffold("Cambiar contraseña") {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (done) {
                    Text("Contraseña actualizada.", style = MaterialTheme.typography.titleMedium)
                    Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("Volver") }
                } else {
                    PasswordField(current, { current = it }, "Contraseña actual")
                    PasswordField(newPassword, { newPassword = it }, "Nueva contraseña")
                    PasswordField(confirm, { confirm = it }, "Repite la nueva contraseña", ImeAction.Done)
                    PasswordRules(newPassword, user.email, confirm)
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Button(
                        onClick = {
                            busy = true; error = null
                            scope.launch {
                                error = vm.changePassword(user.id, current, newPassword, startSession = false)
                                done = error == null
                                busy = false
                            }
                        },
                        enabled = !busy && current.isNotEmpty() && newPassword == confirm && PasswordPolicy.isValid(newPassword, user.email),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                    ) { Text("Guardar") }
                    TextButton(onClick = onBack) { Text("Cancelar") }
                }
            }
        }
    }
}
