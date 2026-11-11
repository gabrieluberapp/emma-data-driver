package com.emmadatadriver.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emmadatadriver.app.modelo.ConfiguracionMeta

@Composable
fun PantallaInicio(
    config: ConfiguracionMeta,
    viajesHoy: Int,
    rachaViajesVerdes: Int,
    facturadoHoy: Double,
    gastosHoy: Double,
    vozSilenciada: Boolean,
    modoAhorroActivo: Boolean,
    onAlternarSilencio: () -> Unit,
    onAlternarAhorro: () -> Unit,
    onCambiarVehiculo: (String) -> Unit,
    onActivarEmma: () -> Unit,
    onCargarGastoRapido: () -> Unit,
    onAbrirMapa: () -> Unit,
    onAbrirChat: () -> Unit,
    onAbrirChecklist: () -> Unit
) {
    val netoHoy = facturadoHoy - gastosHoy
    val progresoMeta = (netoHoy / config.metaNetaDiaria).toFloat().coerceIn(0f, 1f)
    val porcentajeMeta = (progresoMeta * 100).toInt()
    val estadoSemaforo = config.evaluarSemaforoJornada(viajesHoy, facturadoHoy)

    val fondoNoche = Color(0xFF0A0E17)
    val tarjetaOscura = Color(0xFF141B2D)
    val cianEmma = Color(0xFF00F5D4)
    val verdeNeon = Color(0xFF00E676)
    val ambarSolar = Color(0xFFFFD600)
    val coralAlerta = Color(0xFFFF2A5F)

    val (gradienteSemaforo, textoSemaforo, colorBorde) = when (estadoSemaforo) {
        "VERDE_EXCELENTE" -> Triple(
            Brush.horizontalGradient(listOf(Color(0xFF00C853), Color(0xFF00E676))),
            "🟢 EXCELENTE RITMO",
            verdeNeon
        )
        "AMARILLO_EN_META" -> Triple(
            Brush.horizontalGradient(listOf(Color(0xFFFF8F00), Color(0xFFFFD600))),
            "🟡 EN META DIARIA",
            ambarSolar
        )
        else -> Triple(
            Brush.horizontalGradient(listOf(Color(0xFFD50000), Color(0xFFFF2A5F))),
            "🔴 DEBAJO DE META",
            coralAlerta
        )
    }

    Scaffold(
        containerColor = fondoNoche,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onActivarEmma,
                containerColor = cianEmma,
                shape = CircleShape,
                modifier = Modifier
                    .size(74.dp)
                    .border(3.dp, Color.White, CircleShape)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎙️", fontSize = 22.sp)
                    Text("EMMA", color = fondoNoche, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = tarjetaOscura) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("🏠") },
                    label = { Text("Tablero", color = cianEmma, fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onAbrirMapa,
                    icon = { Text("🗺️") },
                    label = { Text("Mapa", color = Color.White, fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onAbrirChat,
                    icon = { Text("💬") },
                    label = { Text("Comunidad", color = Color.White, fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onAbrirChecklist,
                    icon = { Text("✅") },
                    label = { Text("Equipo", color = Color.White, fontSize = 11.sp) }
                )
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("EMMA DATA DRIVER", color = cianEmma, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Text("📍 ${config.provincia} • ${config.zona}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalButton(
                        onClick = onAlternarAhorro,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (modoAhorroActivo) ambarSolar else tarjetaOscura
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            if (modoAhorroActivo) "🔋 50%" else "🔋 ON",
                            color = if (modoAhorroActivo) fondoNoche else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onAlternarSilencio,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (vozSilenciada) coralAlerta else Color(0xFF1E293B)
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Text(if (vozSilenciada) "🔇" else "🔊", fontSize = 15.sp)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = config.modoVehiculo == "AUTO",
                        onClick = { onCambiarVehiculo("AUTO") },
                        label = { Text("🚗 Auto", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = cianEmma, selectedLabelColor = fondoNoche)
                    )
                    FilterChip(
                        selected = config.modoVehiculo == "MOTO",
                        onClick = { onCambiarVehiculo("MOTO") },
                        label = { Text("🏍️ Moto", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = cianEmma, selectedLabelColor = fondoNoche)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF2A1B3D))
                        .border(1.dp, Color(0xFFBB86FC), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text("🔥 Racha: $rachaViajesVerdes", color = Color(0xFFE0AAFF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(gradienteSemaforo)
                    .padding(16.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(textoSemaforo, color = fondoNoche, fontSize = 19.sp, fontWeight = FontWeight.Black)
                        Text("$porcentajeMeta%", color = fondoNoche, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progresoMeta },
                        modifier = Modifier.fillMaxWidth().height(9.dp).clip(CircleShape),
                        color = fondoNoche,
                        trackColor = Color.Black.copy(alpha = 0.25f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Viajes: $viajesHoy/${config.viajesDiariosObjetivo} • Meta: $${config.metaNetaDiaria.toInt()} netos",
                        color = fondoNoche,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = tarjetaOscura),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, colorBorde.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("GANANCIA LIMPIA HOY", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
