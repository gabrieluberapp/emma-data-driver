package com.emmadatadriver.app.modelo

data class ConfiguracionMeta(
    val modoVehiculo: String = "AUTO",
    val provincia: String = "Buenos Aires",
    val zona: String = "AMBA / GBA",
    val horasDiarias: Int = 8,
    val diasSemana: Int = 7,
    val metaNetaSemanal: Double = 1000000.0,
    val gastoCombustibleDiarioEstimado: Double = 14000.0,
    val promedioPorViaje: Double = 7027.0,
    val promedioPorKm: Double = 848.0
) {
    val metaNetaDiaria: Double
        get() = if (diasSemana > 0) metaNetaSemanal / diasSemana else 0.0

    val metaBrutaDiaria: Double
        get() = metaNetaDiaria + gastoCombustibleDiarioEstimado

    val viajesDiariosObjetivo: Int
        get() = if (promedioPorViaje > 0) (metaBrutaDiaria / promedioPorViaje).toInt() else 0

    fun evaluarSemaforoJornada(viajesActuales: Int, facturadoActual: Double): String {
        return when {
            facturadoActual >= metaBrutaDiaria * 1.02 || viajesActuales >= viajesDiariosObjetivo + 2 -> "VERDE_EXCELENTE"
            facturadoActual >= metaBrutaDiaria * 0.95 || viajesActuales >= viajesDiariosObjetivo -> "AMARILLO_EN_META"
            else -> "ROJO_DEBAJO_META"
        }
    }
}
