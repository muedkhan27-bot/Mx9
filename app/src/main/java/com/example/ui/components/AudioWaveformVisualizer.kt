package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import kotlin.math.sin

@Composable
fun AudioWaveformVisualizer(
    isListening: Boolean,
    isSpeaking: Boolean,
    rmsLevel: Float,
    primaryColor: Color = JarvisCyan,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveformAnimation")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        val barCount = 32
        val totalSpacing = size.width / barCount
        val barWidth = totalSpacing * 0.55f
        val centerY = size.height / 2f
        val maxAmplitude = size.height * 0.42f

        for (i in 0 until barCount) {
            val x = (i * totalSpacing) + (totalSpacing - barWidth) / 2f
            val normalizedIdx = (i.toFloat() / barCount) * Math.PI.toFloat()
            val baseHeightFactor = sin(normalizedIdx)

            val dynamicHeight = when {
                isListening || isSpeaking -> {
                    val waveMod = sin((i * 0.4f) + wavePhase).coerceAtLeast(0f)
                    val speechBoost = if (rmsLevel > 0.05f) rmsLevel * 1.5f else 0.4f
                    (maxAmplitude * baseHeightFactor * (0.3f + (waveMod * 0.7f * speechBoost))).coerceAtLeast(4f)
                }
                else -> {
                    // Idle low breathing wave
                    (maxAmplitude * 0.15f * baseHeightFactor).coerceAtLeast(2.5f)
                }
            }

            val topY = centerY - dynamicHeight
            val bottomY = centerY + dynamicHeight
            val barHeight = (bottomY - topY).coerceAtLeast(4f)

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.2f),
                        primaryColor,
                        JarvisCyanBright,
                        primaryColor,
                        primaryColor.copy(alpha = 0.2f)
                    ),
                    startY = topY,
                    endY = bottomY
                ),
                topLeft = Offset(x, topY),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}
