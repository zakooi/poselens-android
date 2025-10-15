package com.example.poselens.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ConfidenceBar(confidence: Float, modifier: Modifier = Modifier) {
    Box(modifier = modifier
        .fillMaxWidth()
        .height(8.dp)
        .background(Brush.horizontalGradient(listOf(Color.Red, Color.Yellow, Color.Green)))) {
        // TODO: indicate value by overlay or clipped width
    }
}
