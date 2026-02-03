package com.mystic.tarot.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.util.fastForEach
import java.lang.Math.random

@Composable
fun MysticBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val deepPurple = Color(0xFF1A1A2E)
    val midPurple = Color(0xFF16213E)
    val nebulaPink = Color(0xFFB980F0).copy(alpha = 0.1f)

    val stars = remember {
        List(100) {
            Offset(
                x = random().toFloat(),
                y = random().toFloat()
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(deepPurple, midPurple)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Draw Stars
            stars.fastForEach { relativeOffset ->
                drawCircle(
                    color = Color.White.copy(alpha = (0.2f + random().toFloat() * 0.5f)),
                    radius = (1.5f + random().toFloat()),
                    center = Offset(
                        x = relativeOffset.x * width,
                        y = relativeOffset.y * height
                    )
                )
            }
            
            // Draw subtle nebula clouds (just big soft circles)
            drawCircle(
                color = nebulaPink,
                radius = width * 0.8f,
                center = Offset(width * 0.2f, height * 0.2f)
            )
            drawCircle(
                color = Color(0xFF533E85).copy(alpha = 0.1f),
                radius = width * 0.6f,
                center = Offset(width * 0.8f, height * 0.8f)
            )
        }
        
        content()
    }
}
