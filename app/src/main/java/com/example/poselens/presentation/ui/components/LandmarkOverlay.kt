package com.example.poselens.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun LandmarkOverlay(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Scaffold: draw a few sample landmarks for debug
            val w = size.width
            val h = size.height
            val points = listOf(
                Offset(w * 0.3f, h * 0.3f),
                Offset(w * 0.5f, h * 0.35f),
                Offset(w * 0.7f, h * 0.3f)
            )
            points.forEach { p ->
                drawCircle(color = Color.Cyan, radius = 8f, center = p, style = Stroke(width = 4f))
            }
        }
    }
}
