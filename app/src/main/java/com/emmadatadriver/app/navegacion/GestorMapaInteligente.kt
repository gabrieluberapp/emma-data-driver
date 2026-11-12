package com.emmadatadriver.app.navegacion

data class ViajeAceptado(
    val plataforma: String,
    val direccionOrigen: String,
    val direccionDestino: String,
    val evitarCallesDeTierra: Boolean = true
)

data class PuntoMapa(
    val id: String,
    val tipo: String,
    val titulo: String,
    val detalle: String,
    val latitud: Double,
    val longitud: Double
)

class GestorMapaInteligente {

    val puntosActivos = mutableListOf<PuntoMapa>()

    fun iniciarRutaDirectaPostAceptacion(
        viaje: ViajeAceptado,
        abrirPantallaMapa: (ViajeAceptado) -> Unit,
        emmaHablar: (String) -> Unit
    ) {
        abrirPantallaMapa(viaje)
        val filtroTierra = if (viaje.evitarCallesDeTierra) "evitando calles de tierra" else ""
        emmaHablar("Emma navegación lista. Iniciando ruta de ${viaje.plataforma} hacia ${viaje.direccionOrigen} $filtroTierra.")
    }

    fun reportarPorVozEnMapa(
        comandoVoz: String,
        latActual: Double,
        lonActual: Double,
        emmaHablar: (String) -> Unit
    ) {
        val texto = comandoVoz.lowercase()
        val (tipo, titulo, respuesta) = when {
            texto.contains("control") || texto.contains("policía") ->
                Triple("CONTROL", "Control Policial", "Control policial reportado en tu ubicación.")
            texto.contains("cámara") || texto.contains("radar") ->
                Triple("CAMARA_VEL", "Radar / Cámara Móvil", "Cámara de fotomulta marcada en el mapa.")
            texto.contains("tierra") || texto.contains("inundada") ->
                Triple("CALLE_TIERRA", "Calle de tierra / Inundada", "Tramo marcado para evitar en futuras rutas.")
            texto.contains("peligrosa") || texto.contains("riesgo") ->
                Triple("RIESGO", "Zona Peligrosa", "Alerta de zona peligrosa enviada a la comunidad.")
            texto.contains("estacionar") ->
                Triple("ESTACIONAR_OK", "Espera Segura Habilitada", "Zona habilitada para estacionar guardada.")
            else -> Triple("ALERTA_GENERAL", "Reporte Vial", "Reporte agregado al mapa.")
        }

        puntosActivos.add(
            PuntoMapa(
                id = System.currentTimeMillis().toString(),
                tipo = tipo,
                titulo = titulo,
                detalle = "Reportado por voz en tiempo real",
                latitud = latActual,
                longitud = lonActual
            )
        )
        emmaHablar(respuesta)
    }

    fun anunciarDarsenaGiro(ladoDarsena: String, calleDestino: String, emmaHablar: (String) -> Unit) {
        if (ladoDarsena.uppercase() == "DERECHA") {
            emmaHablar("Atención: para girar a la izquierda hacia $calleDestino, tomá la dársena lateral a la derecha.")
        } else {
            emmaHablar("Colocate sobre la dársena izquierda para girar hacia $calleDestino.")
        }
    }
}
