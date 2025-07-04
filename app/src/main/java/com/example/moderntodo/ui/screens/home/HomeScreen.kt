package com.example.moderntodo.ui.screens.home

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moderntodo.ui.auth.AuthViewModel
import com.example.moderntodo.ui.theme.GradientEnd
import com.example.moderntodo.ui.theme.GradientStart
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "floating")

    val floatingAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    // Trigger animations on composition
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GradientStart,
                        GradientEnd,
                        MaterialTheme.colorScheme.primary
                    )
                )
            )
    ) {
        // Floating background decorations
        FloatingDecorations(floatingAnimation)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome Section with animations
            WelcomeSection(isVisible = isVisible)

            Spacer(modifier = Modifier.height(48.dp))

            // Developer Info Section
            DeveloperInfoSection(isVisible = isVisible)

            Spacer(modifier = Modifier.height(64.dp))

            // Modern Action Button
            ModernActionButton(
                isVisible = isVisible,
                onClick = { navController.navigate("lists") }
            )
        }
    }
}

@Composable
private fun FloatingDecorations(floatingAnimation: Float) {
    // Floating circles for decoration
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.1f)
    ) {
        // Top-left circle
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(
                    x = (-50).dp + (10 * floatingAnimation).dp,
                    y = (-50).dp + (15 * floatingAnimation).dp
                )
                .background(
                    Color.White,
                    RoundedCornerShape(50)
                )
        )

        // Bottom-right circle
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomEnd)
                .offset(
                    x = 50.dp - (8 * floatingAnimation).dp,
                    y = 50.dp - (12 * floatingAnimation).dp
                )
                .background(
                    Color.White,
                    RoundedCornerShape(50)
                )
        )
    }
}

@Composable
private fun WelcomeSection(isVisible: Boolean) {
    val alphaAnimation by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 200),
        label = "welcome_alpha"
    )

    val slideAnimation by animateFloatAsState(
        targetValue = if (isVisible) 0f else -50f,
        animationSpec = tween(800, delayMillis = 300),
        label = "welcome_slide"
    )

    Column(
        modifier = Modifier
            .alpha(alphaAnimation)
            .offset(y = slideAnimation.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Icon/Logo placeholder
        Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(80.dp)
                .scale(alphaAnimation)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome to",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White.copy(alpha = 0.9f),
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hasindu Nimesh Viduranga's",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "TO DO List!",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DeveloperInfoSection(isVisible: Boolean) {
    val alphaAnimation by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 600),
        label = "dev_info_alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alphaAnimation),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Developed by",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hasindu Nimesh Viduranga",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Student ID: D/BCS/23/0007",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ModernActionButton(
    isVisible: Boolean,
    onClick: () -> Unit
) {
    val scaleAnimation by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.3f,
        animationSpec = tween(800, delayMillis = 1000),
        label = "button_scale"
    )

    val alphaAnimation by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800, delayMillis = 1000),
        label = "button_alpha"
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .scale(scaleAnimation)
            .alpha(alphaAnimation)
            .fillMaxWidth(0.8f)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(28.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Go to Lists",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
