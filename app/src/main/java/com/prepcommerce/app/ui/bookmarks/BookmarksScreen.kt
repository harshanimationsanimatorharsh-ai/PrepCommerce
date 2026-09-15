package com.prepcommerce.app.ui.bookmarks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.prepcommerce.app.ui.components.EmptyState
import com.prepcommerce.app.ui.components.LoadingState

@Composable
fun BookmarksScreen(viewModel: BookmarkViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Bookmarks", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        when {
            state.loading -> LoadingState()
            state.questions.isEmpty() -> EmptyState("No bookmarks yet. Bookmark MCQs/Questions to see them here.")
            else -> LazyColumn {
                items(state.questions) { q ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(q.questionText, fontWeight = FontWeight.Bold)
                            Text(q.explanation, style = MaterialTheme.typography.bodySmall)
                            TextButton(onClick = { viewModel.remove(q.id, q.subject) }) { Text("Remove Bookmark") }
                        }
                    }
                }
            }
        }
    }
}
