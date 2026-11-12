package com.emmadatadriver.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emmadatadriver.app.navegacion.PuntoMapa
import com.emmadatadriver.app.navegacion.ViajeAceptado

@Composable
fun PantallaMapa(
    viajeActual: ViajeAceptado?,
    puntosMapa: List<PuntoMapa>,
    evitarTierra: Boolean,
    onAlternarEvitarTierra: () -> Unit,
    onSimularReporteVoz: (String) -> Unit,
    onVolverTablero: () -> Unit
) {
    val fondoNoche = Color(0xFF0A0E17)
    val tarjetaOscura = Color(0xFF141B2D)
    val cianEmma = Color(0xFF00F5D4)
    val verdeNeon = Color(0xFF00E676)
    val ambarSolar = Color(0xFFFFD600)
    val coralAlerta = Color(0xFFFF2A5F)

    Scaffold(
        containerColor = fondoNoche,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tarjetaOscura)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("EMMA NAVEGACIÓN", color = cianEmma, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Text(
                        if (viajeActual != null) "Viaje ${viajeActual.plataforma} activo" else "Exploración libre • GPS Activo",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onVolverTablero,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Text("⬅ Tablero", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tarjeta de Ruta y Dársenas
            Card(
                colors = CardDefaults.cardColors(containerColor = tarjetaOscura),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, cianEmma.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🧭 RUTA INTELIGENTE", color = verdeNeon, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        FilterChip(
                            selected = evitarTierra,
                            onClick = onAlternarEvitarTierra,
                            label = { Text(if (evitarTierra) "🚫 Sin tierra" else "⚠️ Con tierra", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = cianEmma,
                                selectedLabelColor = fondoNoche
                            )
                        )
                    }

                    Text(
                        text = viajeActual?.let { "Origen: ${it.direccionOrigen}\nDestino: ${it.direccionDestino}" }
                            ?: "Esperando aceptación de viaje en Uber, DiDi o Cabify...",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }

            // Botonera rápida de Reportes Viales por Voz / Toque
            Text("REPORTAR ALERTA EN EL MAPA", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 12.sp)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onSimularReporteVoz("cámara") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ambarSolar)
                ) {
                    Text("📸 Cámara", color = fondoNoche, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Button(
                    onClick = { onSimularReporteVoz("control") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = cianEmma)
                ) {
                    Text("🚓 Control", color = fondoNoche, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Button(
                    onClick = { onSimularReporteVoz("peligrosa") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = coralAlerta)
                ) {
                    Text("🚨 Riesgo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // Lista de Alertas Activas en la Zona
            Text("ALERTAS ACTIVAS EN TU RUTA (${puntosMapa.size})", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 12.sp)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(puntosMapa) { punto ->
                    val icono = when (punto.tipo) {
                        "CAMARA_VEL" -> "📸"
                        "CONTROL" -> "🚓"
                        "RIESGO" -> "🚨"
                        "CALLE_TIERRA" -> "🚧"
                        else -> "📍"
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(tarjetaOscura)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(icono, fontSize = 18.sp)
                        }
                        Column {
                            Text(punto.titulo, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(punto.detalle, color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
