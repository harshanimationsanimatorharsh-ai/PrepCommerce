package com.prepcommerce.app.ui.samplepaper

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prepcommerce.app.navigation.Screen
import com.prepcommerce.app.ui.components.EmptyState
import com.prepcommerce.app.ui.components.LoadingState
import com.prepcommerce.app.ui.components.SimpleTopBar

@Composable
fun SamplePaperListScreen(subject: String, viewModel: SamplePaperViewModel, navController: NavController) {
    val state by viewModel.listState.collectAsState()
    LaunchedEffect(subject) { viewModel.loadPapers(subject) }

    Column(Modifier.fillMaxSize()) {
        SimpleTopBar("$subject Sample Papers", onBack = { navController.popBackStack() })
        when {
            state.loading -> LoadingState()
            state.papers.isEmpty() -> EmptyState("No sample papers added for this subject yet.")
            else -> LazyColumn(Modifier.padding(16.dp)) {
                items(state.papers) { paper ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        onClick = { navController.navigate(Screen.SamplePaperAttempt.build(paper.id)) }) {
                        Column(Modifier.padding(14.dp)) {
                            Text(paper.title, fontWeight = FontWeight.Bold)
                            Text("Duration: ${paper.durationMinutes} minutes")
                        }
                    }
                }
            }
        }
    }
}
