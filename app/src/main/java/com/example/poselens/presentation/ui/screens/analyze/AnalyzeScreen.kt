package com.example.poselens.presentation.ui.screens.analyze

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyzeScreen(
    imageUri: String?,
    onBack: () -> Unit,
    viewModel: AnalyzeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(imageUri) {
        imageUri?.let { viewModel.analyzeImage(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analyze") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Error: ${uiState.error}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { uiState.result?.let { viewModel.analyzeImage(it.imageUri) } }) {
                            Text("Retry")
                        }
                    }
                }
                uiState.result != null -> {
                    val result = uiState.result
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)) {

                        // Image preview
                        if (result.imageUri.isNotEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(Uri.parse(result.imageUri)),
                                contentDescription = "Selected image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(360.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentScale = ContentScale.Crop
                            )

                            // Overlay placeholder
                            LandmarkOverlay(modifier = Modifier
                                .fillMaxWidth()
                                .height(360.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        ResultCard(result = result)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Suggestions", style = MaterialTheme.typography.titleMedium)
                        FlowRow(mainAxisSpacing = 8.dp) {
                            result.suggestions.forEach { suggestion ->
                                SuggestionChip(text = suggestion)
                            }
                        }
                    }
                }
                else -> {
                    // Idle state
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No image selected")
                    }
                }
            }
        }
    }
}
