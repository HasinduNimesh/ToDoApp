package com.example.moderntodo.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Background gradient blur elements
        BackgroundElements()

        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    RoundedCornerShape(24.dp)
                )
                .shadow(
                    8.dp,
                    RoundedCornerShape(24.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
                .padding(32.dp)
        ) {
            // Animated progress indicator
            PulsingProgressRings()

            Spacer(modifier = Modifier.height(36.dp))

            // Loading text with animated underline
            Text(
                text = "Loading your tasks",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedUnderline()
        }
    }
}

@Composable
private fun BackgroundElements() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_transition")

    // Extract theme colors before using in Canvas
    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    val translateX by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "translateX"
    )

    val translateY by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "translateY"
    )

    // Primary color gradient blob
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.2f),
                    primaryColor.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.7f + translateX, size.height * 0.3f + translateY),
                radius = size.minDimension * 0.5f
            ),
            radius = size.minDimension * 0.5f,
            center = Offset(size.width * 0.7f + translateX, size.height * 0.3f + translateY)
        )
    }

    // Secondary color gradient blob
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    tertiaryColor.copy(alpha = 0.2f),
                    tertiaryColor.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.3f - translateX, size.height * 0.7f - translateY),
                radius = size.minDimension * 0.4f
            ),
            radius = size.minDimension * 0.4f,
            center = Offset(size.width * 0.3f - translateX, size.height * 0.7f - translateY)
        )
    }
}

@Composable
private fun PulsingProgressRings() {
    val infiniteTransition = rememberInfiniteTransition(label = "ring_transition")

    // Extract theme colors outside of Canvas
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    // Pulse animation
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Rotation animation for outer ring
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    // Counter-rotation for inner ring
    val counterRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing)
        ),
        label = "counter_rotation"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Outer ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            rotate(rotation) {
                // Draw dashed arc
                val strokeWidth = size.width * 0.08f
                val radius = (size.minDimension - strokeWidth) / 2

                for (i in 0 until 12) {
                    val angle = i * 30f
                    val startAngle = angle - 10f
                    val sweepAngle = 20f

                    drawArc(
                        color = primaryColor,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = size.copy(
                            width = size.width - strokeWidth,
                            height = size.height - strokeWidth
                        ),
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        ),
                        alpha = 0.7f + (i / 12f) * 0.3f // Varying opacity
                    )
                }
            }
        }

        // Middle ring
        Canvas(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.Center)
        ) {
            rotate(counterRotation) {
                // Draw dashed arc
                val strokeWidth = size.width * 0.06f
                val radius = (size.minDimension - strokeWidth) / 2

                for (i in 0 until 8) {
                    val angle = i * 45f
                    val startAngle = angle - 15f
                    val sweepAngle = 30f

                    drawArc(
                        color = tertiaryColor,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = size.copy(
                            width = size.width - strokeWidth,
                            height = size.height - strokeWidth
                        ),
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        ),
                        alpha = 0.6f + (i / 8f) * 0.4f // Varying opacity
                    )
                }
            }
        }

        // Core circle with gradient
        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.9f),
                            secondaryColor.copy(alpha = 0.7f)
                        )
                    )
                )
                .blur(2.dp)
        )
    }
}

@Composable
private fun AnimatedUnderline() {
    val infiniteTransition = rememberInfiniteTransition(label = "underline_transition")
    val primaryColor = MaterialTheme.colorScheme.primary

    // Animate the width of the line
    val width by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0f at 0
                1f at 1000
                1f at 1500
                0f at 2000
            }
        ),
        label = "width"
    )

    // Animate the position of the line
    val position by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0f at 0
                0f at 1000
                1f at 2000
            }
        ),
        label = "position"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .padding(top = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(width)
                .height(2.dp)
                .offset(x = ((1f - width) * position * 150).dp)
                .clip(RoundedCornerShape(1.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.3f),
                            primaryColor,
                            primaryColor.copy(alpha = 0.3f),
                        )
                    )
                )
        )
    }
}