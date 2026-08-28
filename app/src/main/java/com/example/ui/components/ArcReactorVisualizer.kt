package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.ProtocolType
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisCyanDark
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArcReactorVisualizer(
    isListening: Boolean,
    isSpeaking: Boolean,
    isProcessing: Boolean,
    rmsLevel: Float,
    currentProtocol: ProtocolType,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ArcReactorRotation")

    // Continuous clockwise rotation
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isProcessing) 3000 else 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RingRotation"
    )

    // Counter-clockwise rotation for outer HUD ring
    val counterRotationAngle by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isProcessing) 4000 else 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CounterRotation"
    )

    // Core Pulse Animation
    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = if (isListening || isSpeaking) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isListening) 600 else if (isSpeaking) 800 else 2000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CorePulse"
    )

    val primaryColor = currentProtocol.color
    val secondaryColor = if (currentProtocol == ProtocolType.STANDARD) JarvisCyanBright else currentProtocol.color

    Box(
        modifier = modifier
            .size(240.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 120.dp),
                onClick = onClick
            )
            .testTag("arc_reactor_touch_target"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.minDimension / 2f * 0.9f
            val dynamicScale = corePulse + (rmsLevel * 0.2f)

            // 1. Outermost Holographic HUD Ring with Segments & Ticks
            val outerRadius = baseRadius * 0.95f
            drawCircle(
                color = primaryColor.copy(alpha = 0.25f),
                radius = outerRadius,
                center = center,
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f, 35f, 10f), counterRotationAngle)
                )
            )

            // 2. Rotating Arc Segments (Outer Armor Rings)
            val armorRadius = baseRadius * 0.82f
            for (i in 0 until 10) {
                val startAngle = (i * 36f) + rotationAngle
                drawArc(
                    color = primaryColor.copy(alpha = if (i % 2 == 0) 0.85f else 0.45f),
                    startAngle = startAngle,
                    sweepAngle = 24f,
                    useCenter = false,
                    topLeft = Offset(center.x - armorRadius, center.y - armorRadius),
                    size = androidx.compose.ui.geometry.Size(armorRadius * 2, armorRadius * 2),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // 3. Radial Energy Spokes (Mark VII Magnetic Coils)
            val coilInnerRadius = baseRadius * 0.48f
            val coilOuterRadius = baseRadius * 0.72f
            for (i in 0 until 12) {
                val angleRad = Math.toRadians((i * 30f + rotationAngle).toDouble())
                val start = Offset(
                    center.x + (coilInnerRadius * cos(angleRad)).toFloat(),
                    center.y + (coilInnerRadius * sin(angleRad)).toFloat()
                )
                val end = Offset(
                    center.x + (coilOuterRadius * cos(angleRad)).toFloat(),
                    center.y + (coilOuterRadius * sin(angleRad)).toFloat()
                )
                drawLine(
                    color = secondaryColor.copy(alpha = 0.75f),
                    start = start,
                    end = end,
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // 4. Middle Concentric Reactor Ring
            val midRadius = baseRadius * 0.52f
            drawCircle(
                color = primaryColor.copy(alpha = 0.6f),
                radius = midRadius,
                center = center,
                style = Stroke(
                    width = 3.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 15f), counterRotationAngle * 1.5f)
                )
            )

            // 5. Inner Triangular / Hexagonal Plasma Stabilizer
            val innerRingRadius = baseRadius * 0.38f * dynamicScale
            drawCircle(
                color = primaryColor.copy(alpha = 0.4f),
                radius = innerRingRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // 6. Central Arc Reactor Plasma Glow (Radial Gradient)
            val coreRadius = baseRadius * 0.28f * dynamicScale
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        secondaryColor,
                        primaryColor.copy(alpha = 0.6f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = coreRadius * 1.6f
                ),
                radius = coreRadius * 1.5f,
                center = center
            )

            // 7. Core Inner Ring
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = coreRadius * 0.55f,
                center = center,
                style = Stroke(width = 2.5.dp.toPx())
            )
        }
    }
}
