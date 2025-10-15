package com.example.poselens.presentation.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Comparison View Component
 * 
 * Features:
 * - Side-by-side comparison
 * - Draggable divider
 * - Before/After labels
 */
@Composable
fun ComparisonView(
    beforeUri: Uri,
    afterUri: Uri,
    modifier: Modifier = Modifier
) {
    var dividerPosition by remember { mutableFloatStateOf(0.5f) }
    var viewSize by remember { mutableStateOf(IntSize.Zero) }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .clip(MaterialTheme.shapes.medium)
            .onSizeChanged { viewSize = it }
    ) {
        // Before image (full)
        AsyncImage(
            model = beforeUri,
            contentDescription = "Before",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // After image (clipped by divider)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(dividerPosition)
        ) {
            AsyncImage(
                model = afterUri,
                contentDescription = "After",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        // Divider
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(4.dp)
                .offset(x = with(LocalDensity.current) { 
                    (viewSize.width * dividerPosition).toDp() 
                })
                .background(Color.White)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        val newPosition = dividerPosition + (dragAmount / viewSize.width)
                        dividerPosition = newPosition.coerceIn(0f, 1f)
                    }
                }
        )
        
        // Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Before",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            
            Text(
                text = "After",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

/**
 * Simple Toggle Comparison (no drag)
 */
@Composable
fun ToggleComparisonView(
    beforeUri: Uri,
    afterUri: Uri,
    modifier: Modifier = Modifier
) {
    var showBefore by remember { mutableStateOf(true) }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .clip(MaterialTheme.shapes.medium)
    ) {
        AsyncImage(
            model = if (showBefore) beforeUri else afterUri,
            contentDescription = if (showBefore) "Before" else "After",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Toggle button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Button(
                onClick = { showBefore = !showBefore }
            ) {
                Text(if (showBefore) "Show After" else "Show Before")
            }
        }
    }
}
