package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SystemMetrics
import com.example.ui.theme.JarvisAlertRed
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisSurfaceBorder
import com.example.ui.theme.JarvisSurfaceDark
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import com.example.ui.theme.JarvisTextTertiary

@Composable
fun StarkTelemetryPanel(
    systemMetrics: SystemMetrics,
    onRunScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STARK SYSTEM TELEMETRY",
                color = JarvisCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Button(
                onClick = onRunScan,
                colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceElevated),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .border(1.dp, JarvisCyan.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .testTag("run_scan_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Scan",
                    tint = JarvisCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "RUN SCAN",
                    color = JarvisCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 1. Device Hardware Identification Card
        TelemetryCard(title = "DEVICE & NEURAL ARCHITECTURE") {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                MetricRow(label = "HARDWARE MODEL", value = systemMetrics.deviceModel, valueColor = JarvisCyanBright)
                MetricRow(label = "FIRMWARE PLATFORM", value = systemMetrics.androidVersion, valueColor = JarvisTextPrimary)
                MetricRow(label = "COMPUTE CORE MATRIX", value = "${systemMetrics.cpuCores} Quantum Threads", valueColor = JarvisGreen)
            }
        }

        // 2. Arc Power & Thermal Status Card
        TelemetryCard(title = "ARC REACTOR POWER & THERMALS") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "ENERGY CAPACITY", color = JarvisTextSecondary, fontSize = 11.sp)
                    Text(
                        text = "${systemMetrics.batteryLevel}% ${if (systemMetrics.isCharging) "(CHARGING)" else ""}",
                        color = if (systemMetrics.batteryLevel > 20) JarvisCyanBright else JarvisAlertRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                LinearProgressIndicator(
                    progress = { (systemMetrics.batteryLevel / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (systemMetrics.batteryLevel > 20) JarvisCyan else JarvisAlertRed,
                    trackColor = JarvisSurfaceBorder
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricMiniBadge(icon = Icons.Default.Thermostat, label = "CORE TEMP", value = "${systemMetrics.batteryTemperatureCelsius}°C")
                    MetricMiniBadge(icon = Icons.Default.Speed, label = "VOLTAGE", value = "${systemMetrics.batteryVoltage}V")
                }
            }
        }

        // 3. Memory & Computational Matrix Card
        val usedRamMb = (systemMetrics.totalRamMb - systemMetrics.availableRamMb).coerceAtLeast(0)
        val ramProgress = if (systemMetrics.totalRamMb > 0) usedRamMb.toFloat() / systemMetrics.totalRamMb else 0.5f

        TelemetryCard(title = "RAM COMPUTATIONAL BUFFER") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "ALLOCATED MEMORY", color = JarvisTextSecondary, fontSize = 11.sp)
                    Text(
                        text = "$usedRamMb MB / ${systemMetrics.totalRamMb} MB",
                        color = JarvisCyanBright,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                LinearProgressIndicator(
                    progress = { ramProgress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = JarvisAmber,
                    trackColor = JarvisSurfaceBorder
                )

                MetricRow(label = "FREE BUFFER", value = "${systemMetrics.availableRamMb} MB available", valueColor = JarvisGreen)
            }
        }

        // 4. Storage Flash Storage Matrix
        val usedStorageGb = (systemMetrics.totalStorageGb - systemMetrics.freeStorageGb).coerceAtLeast(0.0)
        val storageProgress = if (systemMetrics.totalStorageGb > 0) (usedStorageGb / systemMetrics.totalStorageGb).toFloat() else 0.5f

        TelemetryCard(title = "ONBOARD FLASH STORAGE") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "STORAGE UTILIZATION", color = JarvisTextSecondary, fontSize = 11.sp)
                    Text(
                        text = "${String.format("%.1f", usedStorageGb)} GB / ${String.format("%.1f", systemMetrics.totalStorageGb)} GB",
                        color = JarvisCyanBright,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                LinearProgressIndicator(
                    progress = { storageProgress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = JarvisCyan,
                    trackColor = JarvisSurfaceBorder
                )

                MetricRow(label = "FREE CAPACITY", value = "${systemMetrics.freeStorageGb} GB available", valueColor = JarvisGreen)
            }
        }

        // 5. Network & Communications Uplink
        TelemetryCard(title = "SATELLITE & TELEMETRY UPLINK") {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                MetricRow(label = "CARRIER GRID", value = systemMetrics.networkType, valueColor = JarvisCyanBright)
                MetricRow(
                    label = "STARK AI CLOUD",
                    value = if (systemMetrics.isOnline) "GEMINI QUANTUM UPLINK ACTIVE" else "OFFLINE LOCAL LOGIC ENGINE",
                    valueColor = if (systemMetrics.isOnline) JarvisGreen else JarvisAmber
                )
            }
        }
    }
}

@Composable
private fun TelemetryCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(JarvisSurfaceDark)
            .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Text(
            text = title,
            color = JarvisTextTertiary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        content()
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String,
    valueColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = JarvisTextSecondary, fontSize = 11.sp)
        Text(text = value, color = valueColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MetricMiniBadge(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(JarvisSurfaceElevated)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = JarvisCyan, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "$label: ", color = JarvisTextSecondary, fontSize = 10.sp)
        Text(text = value, color = JarvisCyanBright, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
