package com.example.poselens.presentation.ui.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SuggestionChip(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    AssistChip(onClick = onClick, label = { Text(text) }, modifier = modifier)
}
