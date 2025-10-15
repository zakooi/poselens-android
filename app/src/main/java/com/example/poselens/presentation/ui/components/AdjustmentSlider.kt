package com.example.poselens.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Adjustment Slider Component
 * 
 * Features:
 * - Label with current value
 * - Slider with range
 * - Reset button
 * - Value formatting
 */
@Composable
fun AdjustmentSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = -1f..1f,
    defaultValue: Float = 0f,
    valueFormatter: (Float) -> String = { String.format("%.2f", it) }
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Label and value
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = valueFormatter(value),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Reset button (only show if not at default)
                if (value != defaultValue) {
                    IconButton(
                        onClick = { onValueChange(defaultValue) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset $label",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
        
        // Slider
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Percentage Adjustment Slider (0-200%)
 */
@Composable
fun PercentageAdjustmentSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    AdjustmentSlider(
        label = label,
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        valueRange = 0f..2f,
        defaultValue = 1f,
        valueFormatter = { "${(it * 100).toInt()}%" }
    )
}

/**
 * Offset Adjustment Slider (-100 to +100)
 */
@Composable
fun OffsetAdjustmentSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    AdjustmentSlider(
        label = label,
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        valueRange = -1f..1f,
        defaultValue = 0f,
        valueFormatter = { 
            val percentage = (it * 100).toInt()
            if (percentage >= 0) "+$percentage" else "$percentage"
        }
    )
}
