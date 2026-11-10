package cl.checkhelph.app

import cl.checkhelph.app.data.AppStore
import cl.checkhelph.app.data.DemoSeed
import cl.checkhelph.app.data.LoginResult
import cl.checkhelph.app.data.Moment
import cl.checkhelph.app.data.QuestionCategory
import cl.checkhelph.app.data.Role
import cl.checkhelph.app.domain.QuestionSearch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class AppStoreTest {
    @get:Rule val tmp = TemporaryFolder()
    private var clock = 1_800_000_000_000L
    private lateinit var store: AppStore
    private val pw = DemoSeed.DEMO_PASSWORD

    @Before fun setUp() {
        store = AppStore(tmp.newFile("data.json").also { it.delete() }) { clock }
        store.init()
    }

    @Test fun login_correcto_e_incorrecto() {
        assertTrue(store.login("JEFE@checkhelph.test", pw) is LoginResult.Ok)
        assertEquals(LoginResult.Invalid, store.login("jefe@checkhelph.test", "otra"))
        assertEquals(LoginResult.Invalid, store.login("nadie@checkhelph.test", pw))
    }

    @Test fun bloqueo_temporal_tras_5_intentos() {
        repeat(4) { assertEquals(LoginResult.Invalid, store.login("trabajador@checkhelph.test", "mala")) }
        assertTrue(store.login("trabajador@checkhelph.test", "mala") is LoginResult.Locked)
        assertTrue(store.login("trabajador@checkhelph.test", pw) is LoginResult.Locked)
        clock += 16 * 60_000
        assertTrue(store.login("trabajador@checkhelph.test", pw) is LoginResult.Ok)
    }

    @Test fun usuario_desvinculado_no_puede_entrar_y_pierde_la_sesion() {
        assertEquals(LoginResult.Blocked, store.login("desvinculado@checkhelph.test", pw))
        store.login("trabajador@checkhelph.test", pw)
        assertNotNull(store.data.value.currentUser)
        store.setActive("u-trab", false)
        assertNull(store.data.value.currentUser)
    }

    @Test fun clave_vencida_obliga_a_cambiar_y_no_permite_repetir() {
        val r = store.login("colaborador@checkhelph.test", pw)
        assertTrue(r is LoginResult.MustChange && r.expired)
        assertNotNull(store.changePassword("u-cola", null, pw, startSession = true)) // repetida
        assertNull(store.changePassword("u-cola", null, "Probador#Nuevo9", startSession = true))
        assertEquals("u-cola", store.data.value.currentUser?.id)
    }

    @Test fun recuperacion_con_codigo() {
        assertNull(store.requestReset("nadie@checkhelph.test"))
        val code = store.requestReset("trabajador@checkhelph.test")!!
        assertNotNull(store.resetWithCode("trabajador@checkhelph.test", "000000", "Probador#Nuevo9"))
        assertNull(store.resetWithCode("trabajador@checkhelph.test", code, "Probador#Nuevo9"))
        assertTrue(store.login("trabajador@checkhelph.test", "Probador#Nuevo9") is LoginResult.Ok)
    }

    @Test fun cuenta_nueva_con_clave_temporal() {
        val (error, temp) = store.createUser("Nueva Persona", "nueva@checkhelph.test", Role.COLABORADOR)
        assertNull(error)
        assertTrue(store.login("nueva@checkhelph.test", temp!!) is LoginResult.MustChange)
        assertNotNull(store.createUser("Otra", "nueva@checkhelph.test", Role.COLABORADOR).first)
    }

    @Test fun consulta_sin_respuesta_y_respuesta_del_jefe() {
        store.addPending("¿Dónde están las bolsas de regalo?", "Tomás")
        val p = store.data.value.pending.single()
        store.answerPending(p.id, p.text, "En el mueble bajo la caja 1.", QuestionCategory.TURNO, Moment.INTERMEDIO)
        assertTrue(store.data.value.pending.isEmpty())
        assertEquals("¿Dónde están las bolsas de regalo?", QuestionSearch.search("bolsas regalo", store.data.value.questions).first().question)
    }

    @Test fun busqueda_ignora_tildes_y_autocompleta() {
        val qs = store.data.value.questions
        assertEquals("¿Cómo abro la caja?", QuestionSearch.search("COMO ABRO caja", qs).first().question)
        assertTrue(QuestionSearch.suggestions("cola", qs).any { it.question.contains("colación") })
        assertTrue(QuestionSearch.forMoment(Moment.APERTURA, qs).all { it.moment == Moment.APERTURA })
    }

    @Test fun datos_persisten_al_recargar() {
        store.addPending("Prueba de persistencia", "Tomás")
        val again = AppStore(tmp.root.resolve("data.json")) { clock }.also { it.init() }
        assertEquals(1, again.data.value.pending.size)
    }
}
