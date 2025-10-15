package com.example.poselens.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.poselens.presentation.ui.screens.analyze.AnalyzeResult

@Composable
fun ResultCard(result: AnalyzeResult) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Analysis Summary", style = MaterialTheme.typography.titleMedium)
            Text(text = "Pose available: ${result.poseAvailable}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Suggestions: ${result.suggestions.size}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
