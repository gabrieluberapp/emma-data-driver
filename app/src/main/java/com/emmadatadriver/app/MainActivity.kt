
package com.emmadatadriver.app

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.emmadatadriver.app.energia.GestorBateria
import com.emmadatadriver.app.modelo.ConfiguracionMeta
import com.emmadatadriver.app.ui.PantallaInicio
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private lateinit var gestorBateria: GestorBateria

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        gestorBateria = GestorBateria(this)

        setContent {
            var config by remember { mutableStateOf(ConfiguracionMeta()) }
            var viajesHoy by remember { mutableIntStateOf(0) }
            var rachaVerdes by remember { mutableIntStateOf(0) }
            var facturadoHoy by remember { mutableDoubleStateOf(0.0) }
            var gastosHoy by remember { mutableDoubleStateOf(0.0) }
            var vozSilenciada by remember { mutableStateOf(false) }
            var ahorroHabilitado by remember { mutableStateOf(true) }
            var modoAhorroActivo by remember { mutableStateOf(false) }

            LaunchedEffect(ahorroHabilitado) {
                gestorBateria.verificarYActivarAhorro(ahorroHabilitado, this@MainActivity) { activo ->
                    modoAhorroActivo = activo
                }
            }

            PantallaInicio(
                config = config,
                viajesHoy = viajesHoy,
                rachaViajesVerdes = rachaVerdes,
                facturadoHoy = facturadoHoy,
                gastosHoy = gastosHoy,
                vozSilenciada = vozSilenciada,
                modoAhorroActivo = modoAhorroActivo,
                onAlternarSilencio = { vozSilenciada = !vozSilenciada },
                onAlternarAhorro = { ahorroHabilitado = !ahorroHabilitado },
                onCambiarVehiculo = { nuevoModo -> config = config.copy(modoVehiculo = nuevoModo) },
                onActivarEmma = {
                    if (!vozSilenciada) {
                        hablar("Emma navegación lista")
                    }
                },
                onCargarGastoRapido = {
                    gastosHoy += 14000.0
                },
                onAbrirMapa = {},
                onAbrirChat = {},
                onAbrirChecklist = {}
            )
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
