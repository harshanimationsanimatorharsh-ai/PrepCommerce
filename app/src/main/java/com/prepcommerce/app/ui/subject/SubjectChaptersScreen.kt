package com.prepcommerce.app.ui.subject

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
fun SubjectChaptersScreen(subject: String, viewModel: ChapterViewModel, navController: NavController) {
    val state by viewModel.listState.collectAsState()
    LaunchedEffect(subject) { viewModel.loadChapters(subject) }

    Column(Modifier.fillMaxSize()) {
        SimpleTopBar(subject, onBack = { navController.popBackStack() })
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { navController.navigate(Screen.Test.build(subject)) }, modifier = Modifier.weight(1f)) { Text("50-Q Test") }
            OutlinedButton(onClick = { navController.navigate(Screen.Mcq.build("RANDOM", subject)) }, modifier = Modifier.weight(1f)) { Text("Random MCQs") }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { navController.navigate(Screen.Mcq.build("IMPORTANT", subject)) }, modifier = Modifier.weight(1f)) { Text("Important MCQs") }
            OutlinedButton(onClick = { navController.navigate(Screen.SamplePapers.build(subject)) }, modifier = Modifier.weight(1f)) { Text("Sample Papers") }
        }
        Spacer(Modifier.height(8.dp))
        when {
            state.loading -> LoadingState()
            state.chapters.isEmpty() -> EmptyState("No chapters found for this subject yet.")
            else -> LazyColumn(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
                items(state.chapters) { chapter ->
                    val progress = state.progressMap[chapter.id]
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        onClick = { navController.navigate(Screen.ChapterDetail.build(chapter.id)) }
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text("Ch ${chapter.chapterNo}: ${chapter.name}", fontWeight = FontWeight.Bold)
                            if (chapter.weightageMarks != null) {
                                Text("Weightage: ${chapter.weightageMarks} marks", style = MaterialTheme.typography.bodySmall)
                            } else if (chapter.weightageNote != null) {
                                Text(chapter.weightageNote, style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(Modifier.height(6.dp))
                            LinearProgressIndicator(progress = (progress?.completionPercent ?: 0f) / 100f, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}
