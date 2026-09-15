package com.prepcommerce.app.ui.qa

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prepcommerce.app.ui.components.EmptyState
import com.prepcommerce.app.ui.components.LoadingState
import com.prepcommerce.app.ui.components.SimpleTopBar

@Composable
fun QAScreen(chapterId: String, viewModel: QAViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(chapterId) { viewModel.load(chapterId) }

    Column(Modifier.fillMaxSize()) {
        SimpleTopBar("Important Questions", onBack = { navController.popBackStack() })
        when {
            state.loading -> LoadingState()
            state.questions.isEmpty() -> EmptyState("No Q&A content added for this chapter yet.")
            else -> LazyColumn(Modifier.padding(16.dp)) {
                items(state.questions) { q ->
                    val expanded = q.id in state.expandedIds
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), onClick = { viewModel.toggleExpand(q.id) }) {
                        Column(Modifier.padding(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                AssistChip(onClick = {}, label = { Text(q.type.replace("_", " ")) })
                                AssistChip(onClick = {}, label = { Text(q.difficulty) })
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(q.questionText, fontWeight = FontWeight.Bold)
                            if (expanded) {
                                Spacer(Modifier.height(8.dp))
                                Text("Answer / Solution:", fontWeight = FontWeight.Bold)
                                Text(q.explanation)
                                TextButton(onClick = { viewModel.toggleBookmark(q) }) { Text("Bookmark") }
                            }
                        }
                    }
                }
            }
        }
    }
}
