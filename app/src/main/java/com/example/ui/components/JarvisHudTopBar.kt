package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProtocolType
import com.example.model.SystemMetrics
import com.example.ui.theme.JarvisAlertRed
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisSurfaceBorder
import com.example.ui.theme.JarvisSurfaceDark
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun JarvisHudTopBar(
    systemMetrics: SystemMetrics,
    currentProtocol: ProtocolType,
    isBackgroundWakeActive: Boolean,
    onToggleWakeService: () -> Unit,
    onToggleTorch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        JarvisSurfaceDark.copy(alpha = 0.95f),
                        JarvisSurfaceDark.copy(alpha = 0.7f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = currentProtocol.color.copy(alpha = 0.4f),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Upper row: Stark Identity & Network status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(currentProtocol.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "J.A.R.V.I.S.",
                    color = currentProtocol.color,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "OS 9.4",
                    color = JarvisTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            // Online / Offline Status Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(JarvisSurfaceElevated)
                    .border(
                        1.dp,
                        if (systemMetrics.isOnline) JarvisCyan.copy(alpha = 0.5f) else JarvisAmber.copy(alpha = 0.5f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (systemMetrics.isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                    contentDescription = if (systemMetrics.isOnline) "Online" else "Offline",
                    tint = if (systemMetrics.isOnline) JarvisCyan else JarvisAmber,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (systemMetrics.isOnline) "ONLINE" else "OFFLINE LOGIC",
                    color = if (systemMetrics.isOnline) JarvisCyan else JarvisAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lower row: Live Telemetry Gauges & Quick Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Power level telemetry
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Battery Power",
                    tint = if (systemMetrics.batteryLevel > 20) JarvisCyan else JarvisAlertRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "ARC: ${systemMetrics.batteryLevel}%",
                    color = JarvisTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${systemMetrics.batteryTemperatureCelsius}°C",
                    color = JarvisTextSecondary,
                    fontSize = 11.sp
                )
            }

            // Right Quick Controls: Torch & Always Listening toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Flashlight Toggle Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (systemMetrics.torchOn) JarvisAmber.copy(alpha = 0.25f) else JarvisSurfaceElevated)
                        .border(
                            1.dp,
                            if (systemMetrics.torchOn) JarvisAmber else JarvisSurfaceBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable(onClick = onToggleTorch)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("torch_toggle_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (systemMetrics.torchOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                            contentDescription = "Torch",
                            tint = if (systemMetrics.torchOn) JarvisAmber else JarvisTextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (systemMetrics.torchOn) "BEAM ON" else "BEAM",
                            color = if (systemMetrics.torchOn) JarvisAmber else JarvisTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Background Wake Word Service Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isBackgroundWakeActive) JarvisGreen.copy(alpha = 0.2f) else JarvisSurfaceElevated)
                        .border(
                            1.dp,
                            if (isBackgroundWakeActive) JarvisGreen else JarvisSurfaceBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable(onClick = onToggleWakeService)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("wake_word_toggle_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isBackgroundWakeActive) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = "Wake Word Listener",
                            tint = if (isBackgroundWakeActive) JarvisGreen else JarvisTextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBackgroundWakeActive) "WAKE: ACTIVE" else "WAKE: OFF",
                            color = if (isBackgroundWakeActive) JarvisGreen else JarvisTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
