package com.example.moderntodo.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ModernDragHandle(
    isVisible: Boolean = true,
    isPressed: Boolean = false,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 0.8f else 0.4f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "drag_handle_alpha"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "drag_handle_scale"
    )
    
    val backgroundColor by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 0.5f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "drag_handle_bg"
    )
    
    Box(
        modifier = modifier
            .size(44.dp)
            .scale(scale)
            .alpha(alpha)
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = backgroundColor)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.DragHandle,
            contentDescription = "Drag to reorder",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                alpha = if (isPressed) 1f else 0.7f
            ),
            modifier = Modifier.size(18.dp)
        )
    }
}
