package cl.checkhelph.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cl.checkhelph.app.data.AppData
import cl.checkhelph.app.data.Role
import cl.checkhelph.app.data.User
import cl.checkhelph.app.ui.auth.AuthFlow
import cl.checkhelph.app.ui.screens.ChangePasswordScreen
import cl.checkhelph.app.ui.screens.HomeScreen
import cl.checkhelph.app.ui.screens.ProfileScreen
import cl.checkhelph.app.ui.screens.QuestionsScreen
import cl.checkhelph.app.ui.screens.SchedulesScreen
import cl.checkhelph.app.ui.screens.TasksScreen
import cl.checkhelph.app.ui.screens.TicketsScreen
import cl.checkhelph.app.ui.screens.UsersScreen

/**
 * Cinco destinos en la barra inferior (máximo recomendado por Material 3).
 * Horarios, Usuarios y Cambiar contraseña se abren desde Perfil.
 */
enum class Dest(val route: String, val label: String, val icon: ImageVector) {
    INICIO("inicio", "Inicio", Icons.Outlined.Home),
    TAREAS("tareas", "Tareas", Icons.Outlined.TaskAlt),
    PREGUNTAS("preguntas", "Preguntas", Icons.Outlined.QuestionAnswer),
    TICKETS("tickets", "Tickets", Icons.Outlined.ConfirmationNumber),
    PERFIL("perfil", "Perfil", Icons.Outlined.Person),
}

private const val HORARIOS = "horarios"
private const val USUARIOS = "usuarios"
private const val CLAVE = "clave"

@Composable
fun CheckHelphApp(vm: AppViewModel = viewModel()) {
    val data by vm.data.collectAsState()
    val user = data.currentUser
    when {
        !data.loaded -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        user == null -> AuthFlow(vm)
        else -> MainApp(vm, data, user)
    }
}

@Composable
private fun MainApp(vm: AppViewModel, data: AppData, user: User) {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route
    val role = user.role
    val canSeeSchedules = role == Role.JEFE || (role == Role.ASISTENTE && data.assistantCanSeeSchedules)
    val pendingCount = if (role == Role.JEFE || role == Role.ASISTENTE) data.pending.size else 0

    fun go(route: String) {
        // Si ya estás en esa pestaña (o en una subpantalla de ella), vuelve a su pantalla principal.
        if (nav.popBackStack(route, inclusive = false)) return
        nav.navigate(route) {
            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                Dest.entries.forEach { d ->
                    NavigationBarItem(
                        selected = current == d.route || (d == Dest.PERFIL && current in setOf(HORARIOS, USUARIOS, CLAVE)),
                        onClick = { go(d.route) },
                        icon = {
                            if (d == Dest.PREGUNTAS && pendingCount > 0) {
                                BadgedBox(badge = { Badge { Text("$pendingCount") } }) { Icon(d.icon, null) }
                            } else {
                                Icon(d.icon, null)
                            }
                        },
                        label = { Text(d.label) },
                    )
                }
            }
        },
    ) { inner ->
        NavHost(nav, startDestination = Dest.INICIO.route, modifier = Modifier.padding(inner)) {
            composable(Dest.INICIO.route) {
                HomeScreen(user, onOpenTasks = { go(Dest.TAREAS.route) }, onOpenQuestions = { go(Dest.PREGUNTAS.route) })
            }
            composable(Dest.TAREAS.route) { TasksScreen(role) }
            composable(Dest.PREGUNTAS.route) { QuestionsScreen(vm, data, user) }
            composable(Dest.TICKETS.route) { TicketsScreen(role) }
            composable(Dest.PERFIL.route) {
                ProfileScreen(
                    user = user,
                    assistantCanSeeSchedules = data.assistantCanSeeSchedules,
                    onAssistantSchedulesChange = vm::setAssistantCanSeeSchedules,
                    canSeeSchedules = canSeeSchedules,
                    onOpenSchedules = { nav.navigate(HORARIOS) },
                    onOpenUsers = { nav.navigate(USUARIOS) },
                    onChangePassword = { nav.navigate(CLAVE) },
                    onLogout = vm::logout,
                )
            }
            composable(HORARIOS) { SchedulesScreen(canSeeSchedules, onBack = { nav.popBackStack() }) }
            composable(USUARIOS) { UsersScreen(vm, data, user, onBack = { nav.popBackStack() }) }
            composable(CLAVE) { ChangePasswordScreen(vm, user, onBack = { nav.popBackStack() }) }
        }
    }
}
