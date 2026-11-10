package cl.checkhelph.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.checkhelph.app.data.AppData
import cl.checkhelph.app.data.AppStore
import cl.checkhelph.app.data.LoginResult
import cl.checkhelph.app.data.Moment
import cl.checkhelph.app.data.QuestionCategory
import cl.checkhelph.app.data.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/** Punto único entre la interfaz y los datos. El hash de contraseñas corre fuera del hilo principal. */
class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val store = AppStore(File(app.filesDir, "checkhelph-demo.json"))
    val data: StateFlow<AppData> = store.data

    init {
        viewModelScope.launch(Dispatchers.IO) { store.init() }
    }

    private suspend fun <T> bg(block: () -> T): T = withContext(Dispatchers.Default) { block() }
    private fun fire(block: () -> Unit) { viewModelScope.launch(Dispatchers.IO) { block() } }

    suspend fun login(email: String, password: String): LoginResult = bg { store.login(email, password) }
    suspend fun changePassword(userId: String, current: String?, new: String, startSession: Boolean) =
        bg { store.changePassword(userId, current, new, startSession) }
    suspend fun requestReset(email: String) = bg { store.requestReset(email) }
    suspend fun resetWithCode(email: String, code: String, new: String) = bg { store.resetWithCode(email, code, new) }
    suspend fun createUser(name: String, email: String, role: Role) = bg { store.createUser(name, email, role) }
    suspend fun adminResetPassword(userId: String) = bg { store.adminResetPassword(userId) }

    fun logout() = fire { store.logout() }
    fun setActive(userId: String, active: Boolean) = fire { store.setActive(userId, active) }
    fun setAssistantCanSeeSchedules(v: Boolean) = fire { store.setAssistantCanSeeSchedules(v) }
    fun registerUse(questionId: String) = fire { store.registerUse(questionId) }
    fun addPending(text: String, askedBy: String) = fire { store.addPending(text, askedBy) }
    fun answerPending(id: String, question: String, answer: String, category: QuestionCategory, moment: Moment?) =
        fire { store.answerPending(id, question, answer, category, moment) }
    fun dismissPending(id: String) = fire { store.dismissPending(id) }
}
