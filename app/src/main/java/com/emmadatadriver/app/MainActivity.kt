package com.emmadatadriver.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.emmadatadriver.app.energia.GestorBateria
import com.emmadatadriver.app.lector.LectorViajesService
import com.emmadatadriver.app.modelo.ConfiguracionMeta
import com.emmadatadriver.app.navegacion.GestorMapaInteligente
import com.emmadatadriver.app.navegacion.ViajeAceptado
import com.emmadatadriver.app.ui.PantallaInicio
import com.emmadatadriver.app.ui.PantallaMapa
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private lateinit var gestorBateria: GestorBateria
    private val gestorMapa = GestorMapaInteligente()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        gestorBateria = GestorBateria(this)

        setContent {
            var pantallaActual by remember { mutableStateOf("INICIO") }
            var config by remember { mutableStateOf(ConfiguracionMeta()) }
            var viajesHoy by remember { mutableIntStateOf(0) }
            var rachaVerdes by remember { mutableIntStateOf(0) }
            var facturadoHoy by remember { mutableDoubleStateOf(0.0) }
            var gastosHoy by remember { mutableDoubleStateOf(0.0) }
            var vozSilenciada by remember { mutableStateOf(false) }
            var lectorAppsActivo by remember { mutableStateOf(true) }
            var modoAhorroActivo by remember { mutableStateOf(false) }
            var evitarTierra by remember { mutableStateOf(true) }
            var viajeActual by remember { mutableStateOf<ViajeAceptado?>(null) }
            val puntosMapa = remember { mutableStateListOf(*gestorMapa.puntosActivos.toTypedArray()) }

            LaunchedEffect(Unit) {
                gestorBateria.verificarYActivarAhorro(true, this@MainActivity) { activo ->
                    modoAhorroActivo = activo
                }
            }

            if (pantallaActual == "MAPA") {
                PantallaMapa(
                    viajeActual = viajeActual,
                    puntosMapa = puntosMapa,
                    evitarTierra = evitarTierra,
                    onAlternarEvitarTierra = { evitarTierra = !evitarTierra },
                    onSimularReporteVoz = { comando ->
                        gestorMapa.reportarPorVozEnMapa(comando, -34.7700, -58.3500) { respuesta ->
                            if (!vozSilenciada) hablar(respuesta)
                        }
                        puntosMapa.clear()
                        puntosMapa.addAll(gestorMapa.puntosActivos)
                    },
                    onVolverTablero = { pantallaActual = "INICIO" }
                )
            } else {
                PantallaInicio(
                    config = config,
                    viajesHoy = viajesHoy,
                    rachaViajesVerdes = rachaVerdes,
                    facturadoHoy = facturadoHoy,
                    gastosHoy = gastosHoy,
                    vozSilenciada = vozSilenciada,
                    lectorAppsActivo = lectorAppsActivo,
                    modoAhorroActivo = modoAhorroActivo,
                    onAlternarSilencio = {
                        vozSilenciada = !vozSilenciada
                        LectorViajesService.vozSilenciada = vozSilenciada
                    },
                    onAlternarLectorApps = {
                        lectorAppsActivo = !lectorAppsActivo
                        LectorViajesService.lectorEncendido = lectorAppsActivo
                        if (lectorAppsActivo) {
                            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        }
                        if (!vozSilenciada) {
                            val estado = if (lectorAppsActivo) "Lector de Uber, DiDi y Cabify encendido" else "Lector de aplicaciones apagado"
                            hablar(estado)
                        }
                    },
                    onCambiarVehiculo = { nuevoModo -> config = config.copy(modoVehiculo = nuevoModo) },
                    onActivarEmma = {
                        if (!vozSilenciada) hablar("Emma navegación lista")
                    },
                    onCargarGastoRapido = { gastosHoy += 14000.0 },
                    onAbrirMapa = { pantallaActual = "MAPA" },
                    onAbrirChat = {},
                    onAbrirChecklist = {}
                )
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("es", "AR")
        }
    }

    private fun hablar(texto: String) {
        tts?.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
