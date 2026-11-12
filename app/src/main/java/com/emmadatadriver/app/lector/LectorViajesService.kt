package com.emmadatadriver.app.lector

import android.accessibilityservice.AccessibilityService
import android.speech.tts.TextToSpeech
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.emmadatadriver.app.modelo.ConfiguracionMeta
import java.util.Locale

class LectorViajesService : AccessibilityService(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val config = ConfiguracionMeta()
    private var ultimaLecturaTiempo: Long = 0L

    companion object {
        var lectorEncendido: Boolean = true
        var vozSilenciada: Boolean = false
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("es", "AR")
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!lectorEncendido || event == null) return

        val paquete = event.packageName?.toString() ?: return
        val plataforma = when {
            paquete.contains("uber") -> "Uber"
            paquete.contains("didi") -> "DiDi"
            paquete.contains("cabify") -> "Cabify"
            else -> return
        }

        val ahora = System.currentTimeMillis()
        if (ahora - ultimaLecturaTiempo < 4000) return

        val nodoRaiz = rootInActiveWindow ?: return
        val textosPantalla = mutableListOf<String>()
        extraerTextos(nodoRaiz, textosPantalla)

        analizarOferta(plataforma, textosPantalla)
    }

    private fun extraerTextos(nodo: AccessibilityNodeInfo?, lista: MutableList<String>) {
        if (nodo == null) return
        nodo.text?.toString()?.let { if (it.isNotBlank()) lista.add(it) }
        for (i in 0 until nodo.childCount) {
            extraerTextos(nodo.getChild(i), lista)
        }
    }

    private fun analizarOferta(plataforma: String, textos: List<String>) {
        var tarifaDetectada = 0.0
        var kmTotales = 0.0

        val regexDinero = Regex("""\$\s?([0-9.,]+)""")
        val regexKm = Regex("""([0-9.,]+)\s?km""", RegexOption.IGNORE_CASE)

        for (texto in textos) {
            regexDinero.find(texto)?.let { match ->
                val limpio = match.groupValues[1].replace(".", "").replace(",", ".")
                val valor = limpio.toDoubleOrNull() ?: 0.0
                if (valor > tarifaDetectada) tarifaDetectada = valor
            }

            regexKm.findAll(texto).forEach { match ->
                val limpio = match.groupValues[1].replace(",", ".")
                val km = limpio.toDoubleOrNull() ?: 0.0
                if (km in 0.2..150.0) kmTotales += km
            }
        }

        if (tarifaDetectada > 500.0 && kmTotales > 0.2) {
            ultimaLecturaTiempo = System.currentTimeMillis()
            val valorPorKm = tarifaDetectada / kmTotales

            val (emoji, veredicto) = when {
                valorPorKm >= config.promedioPorKm && tarifaDetectada >= config.promedioPorViaje ->
                    Pair("🟢", "Viaje rentable de $plataforma. $${tarifaDetectada.toInt()} por ${"%.1f".format(kmTotales)} kilómetros.")
                valorPorKm >= config.promedioPorKm * 0.90 ->
                    Pair("🟡", "Viaje justo de $plataforma. $${tarifaDetectada.toInt()} por ${"%.1f".format(kmTotales)} kilómetros.")
                else ->
                    Pair("🔴", "Viaje no rentable de $plataforma. $${tarifaDetectada.toInt()} por ${"%.1f".format(kmTotales)} kilómetros.")
            }

            Toast.makeText(this, "$emoji $veredicto", Toast.LENGTH_LONG).show()
            if (!vozSilenciada) {
                tts?.speak(veredicto, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
