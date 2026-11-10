package cl.checkhelph.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cl.checkhelph.app.R
import cl.checkhelph.app.data.LoginResult
import cl.checkhelph.app.data.User
import cl.checkhelph.app.security.PasswordPolicy
import cl.checkhelph.app.ui.AppViewModel
import cl.checkhelph.app.ui.screens.DemoNotice
import cl.checkhelph.app.ui.theme.Petroleo
import kotlinx.coroutines.launch

/** Flujo sin sesión: inicio de sesión, recuperación y cambio obligatorio de contraseña. */
@Composable
fun AuthFlow(vm: AppViewModel) {
    var screen by rememberSaveable { mutableStateOf("login") }
    var forced by remember { mutableStateOf<Pair<User, Boolean>?>(null) }

    val f = forced
    when {
        f != null -> ForcedChangeScreen(vm, f.first, expired = f.second, onCancel = { forced = null })
        screen == "forgot" -> ForgotPasswordScreen(vm, onDone = { screen = "login" })
        else -> LoginScreen(vm, onForgot = { screen = "forgot" }, onMustChange = { u, exp -> forced = u to exp })
    }
}

@Composable
private fun AuthLayout(title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(color = Petroleo, shape = RoundedCornerShape(20.dp)) {
            Icon(painterResource(R.drawable.ic_launcher_foreground), null, tint = Color.Unspecified, modifier = Modifier.size(72.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(28.dp))
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) { content() }
    }
}

@Composable
fun PasswordField(value: String, onChange: (String) -> Unit, label: String, imeAction: ImeAction = ImeAction.Next) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña",
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun ErrorText(message: String?) {
    if (message != null) {
        Text(
            message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
    }
}

@Composable
private fun PrimaryButton(text: String, busy: Boolean, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = enabled && !busy, modifier = Modifier.fillMaxWidth().height(52.dp)) {
        if (busy) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp) else Text(text)
    }
}

@Composable
private fun LoginScreen(vm: AppViewModel, onForgot: () -> Unit, onMustChange: (User, Boolean) -> Unit) {
    val scope = rememberCoroutineScope()
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    AuthLayout("CheckHelph", "Ingresa con tu correo del local") {
        OutlinedTextField(
            value = email, onValueChange = { email = it; error = null }, label = { Text("Correo") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
        )
        PasswordField(password, { password = it; error = null }, "Contraseña", ImeAction.Done)
        ErrorText(error)
        PrimaryButton("Ingresar", busy, enabled = email.isNotBlank() && password.isNotEmpty()) {
            busy = true; error = null
            scope.launch {
                when (val r = vm.login(email, password)) {
                    is LoginResult.Ok -> Unit
                    is LoginResult.MustChange -> onMustChange(r.user, r.expired)
                    LoginResult.Invalid -> error = "Correo o contraseña incorrectos."
                    LoginResult.Blocked -> error = "Tu cuenta está desactivada. Habla con tu jefe de local."
                    is LoginResult.Locked -> error = "Demasiados intentos fallidos. Intenta de nuevo en ${r.minutes} min."
                }
                password = ""
                busy = false
            }
        }
        TextButton(onClick = onForgot, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Olvidé mi contraseña") }
        DemoNotice("Maqueta: las cuentas de prueba están en android/README.md. No ingreses datos reales.")
    }
}

@Composable
private fun ForgotPasswordScreen(vm: AppViewModel, onDone: () -> Unit) {
    val scope = rememberCoroutineScope()
    var step by rememberSaveable { mutableStateOf(1) }
    var email by rememberSaveable { mutableStateOf("") }
    var demoCode by rememberSaveable { mutableStateOf<String?>(null) }
    var code by rememberSaveable { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(false) }

    AuthLayout("Recuperar contraseña", if (step == 1) "Te enviaremos un código a tu correo" else "Ingresa el código y tu nueva contraseña") {
        when {
            done -> {
                Text("Tu contraseña fue actualizada. Ya puedes ingresar.", style = MaterialTheme.typography.bodyLarge)
                PrimaryButton("Volver a ingresar", false, onClick = onDone)
            }
            step == 1 -> {
                OutlinedTextField(
                    value = email, onValueChange = { email = it }, label = { Text("Correo") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                )
                PrimaryButton("Enviar código", busy, enabled = email.isNotBlank()) {
                    busy = true
                    scope.launch { demoCode = vm.requestReset(email); step = 2; busy = false }
                }
                TextButton(onClick = onDone, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Volver") }
                Text(
                    "Si no recuerdas tu correo, tu jefe de local puede asignarte una contraseña temporal.",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            else -> {
                // Mensaje igual exista o no la cuenta, para no revelar qué correos están registrados.
                Text("Si el correo pertenece a una cuenta activa, recibirás un código válido por 15 minutos.", style = MaterialTheme.typography.bodyMedium)
                DemoNotice(
                    demoCode?.let { "Maqueta: el correo aún no está conectado. Tu código es $it." }
                        ?: "Maqueta: no hay una cuenta activa con ese correo, así que no se generó código."
                )
                OutlinedTextField(
                    value = code, onValueChange = { if (it.length <= 6) code = it.filter(Char::isDigit) },
                    label = { Text("Código de 6 dígitos") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                )
                PasswordField(newPassword, { newPassword = it }, "Nueva contraseña")
                PasswordField(confirm, { confirm = it }, "Repite la nueva contraseña", ImeAction.Done)
                PasswordRules(newPassword, email, confirm)
                ErrorText(error)
                PrimaryButton(
                    "Cambiar contraseña", busy,
                    enabled = code.length == 6 && newPassword == confirm && PasswordPolicy.isValid(newPassword, email),
                ) {
                    busy = true; error = null
                    scope.launch {
                        error = vm.resetWithCode(email, code, newPassword)
                        if (error == null) done = true
                        busy = false
                    }
                }
                TextButton(onClick = { step = 1; code = ""; error = null }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Solicitar otro código")
                }
            }
        }
    }
}

@Composable
private fun ForcedChangeScreen(vm: AppViewModel, user: User, expired: Boolean, onCancel: () -> Unit) {
    val scope = rememberCoroutineScope()
    var newPassword by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    AuthLayout(
        "Crea una nueva contraseña",
        if (expired) "Tu contraseña tiene más de ${PasswordPolicy.MAX_AGE_DAYS} días." else "Estás usando una contraseña temporal.",
    ) {
        PasswordField(newPassword, { newPassword = it }, "Nueva contraseña")
        PasswordField(confirm, { confirm = it }, "Repite la nueva contraseña", ImeAction.Done)
        PasswordRules(newPassword, user.email, confirm)
        ErrorText(error)
        PrimaryButton("Guardar y entrar", busy, enabled = newPassword == confirm && PasswordPolicy.isValid(newPassword, user.email)) {
            busy = true; error = null
            scope.launch {
                error = vm.changePassword(user.id, null, newPassword, startSession = true)
                busy = false
            }
        }
        TextButton(onClick = onCancel, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Cancelar") }
    }
}

/** Lista en vivo de requisitos de la contraseña, con marca verde al cumplirse. */
@Composable
fun PasswordRules(password: String, email: String, confirm: String? = null) {
    val rules = PasswordPolicy.rules(password, email).toMutableList()
    if (confirm != null) rules += PasswordPolicy.Rule("Ambas contraseñas coinciden", password.isNotEmpty() && password == confirm)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        rules.forEach { r ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (r.ok) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = if (r.ok) "Cumple" else "Falta",
                    tint = if (r.ok) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(18.dp),
                )
                Text(r.label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 8.dp))
            }
        }
        Text(
            "Deberás cambiarla cada ${PasswordPolicy.MAX_AGE_DAYS} días y no podrás repetir las últimas ${PasswordPolicy.HISTORY_SIZE}.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
