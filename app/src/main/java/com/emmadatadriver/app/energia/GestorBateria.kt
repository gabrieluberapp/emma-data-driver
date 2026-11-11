package com.emmadatadriver.app.energia

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class GestorBateria(private val context: Context) {

    fun obtenerPorcentajeBateria(): Int {
        val intentStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filtro ->
            context.registerReceiver(null, filtro)
        }
        val nivel = intentStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val escala = intentStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (nivel >= 0 && escala > 0) (nivel * 100) / escala else 100
    }

    fun verificarYActivarAhorro(
        ahorroHabilitadoPorUsuario: Boolean,
        activity: Activity,
        alActivarAhorro: (Boolean) -> Unit
    ) {
        val porcentaje = obtenerPorcentajeBateria()
        val activar = ahorroHabilitadoPorUsuario && porcentaje <= 50
        val parametros = activity.window.attributes
        parametros.screenBrightness = if (activar) 0.50f else -1f
        activity.window.attributes = parametros
        alActivarAhorro(activar)
    }
}
