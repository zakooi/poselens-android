package com.example.poselens.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Extension functions for common operations
 */

/**
 * Show toast message
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

/**
 * Show snackbar with error message
 */
fun SnackbarHostState.showError(
    scope: CoroutineScope,
    message: String
) {
    scope.launch {
        showSnackbar(message)
    }
}

/**
 * Format confidence score to percentage
 */
fun Float.toPercentageString(): String {
    return "${(this * 100).toInt()}%"
}

/**
 * Clamp value between min and max
 */
fun Float.clamp(min: Float, max: Float): Float {
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}

/**
 * Check if string is valid URL
 */
fun String.isValidUrl(): Boolean {
    return try {
        val url = java.net.URL(this)
        url.toURI()
        true
    } catch (e: Exception) {
        false
    }
}

/**
 * Capitalize first letter
 */
fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase() else it.toString() 
        }
    }
}

/**
 * Format file size to human readable
 */
fun Long.formatFileSize(): String {
    val kb = this / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    
    return when {
        gb >= 1 -> "%.2f GB".format(gb)
        mb >= 1 -> "%.2f MB".format(mb)
        kb >= 1 -> "%.2f KB".format(kb)
        else -> "$this B"
    }
}

/**
 * Safe divide to avoid division by zero
 */
fun Float.safeDivide(divisor: Float, default: Float = 0f): Float {
    return if (divisor != 0f) this / divisor else default
}

/**
 * Convert milliseconds to human readable time
 */
fun Long.toTimeString(): String {
    val seconds = this / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    
    return when {
        hours > 0 -> "${hours}h ${minutes % 60}m"
        minutes > 0 -> "${minutes}m ${seconds % 60}s"
        else -> "${seconds}s"
    }
}
