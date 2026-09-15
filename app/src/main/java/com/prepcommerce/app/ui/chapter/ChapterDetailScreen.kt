package com.prepcommerce.app.ui.chapter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prepcommerce.app.navigation.Screen
import com.prepcommerce.app.ui.components.LoadingState
import com.prepcommerce.app.ui.components.SimpleTopBar
import com.prepcommerce.app.ui.subject.ChapterViewModel

@Composable
fun ChapterDetailScreen(chapterId: String, viewModel: ChapterViewModel, navController: NavController) {
    val state by viewModel.detailState.collectAsState()
    LaunchedEffect(chapterId) { viewModel.loadChapterDetail(chapterId) }

    if (state.loading || state.chapter == null) { LoadingState(); return }
    val chapter = state.chapter!!

    Column(Modifier.fillMaxSize()) {
        SimpleTopBar(chapter.name, onBack = { navController.popBackStack() })
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Chapter ${chapter.chapterNo}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(chapter.weightageNote ?: chapter.weightageMarks?.let { "Weightage: $it marks" } ?: "Weightage not officially published for this unit.", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
            Text("Topics", fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(chapter.topics) { topic -> AssistChip(onClick = {}, label = { Text(topic) }) }
            }
            Spacer(Modifier.height(12.dp))
            Text("Your Progress", fontWeight = FontWeight.Bold)
            LinearProgressIndicator(progress = (state.progress?.completionPercent ?: 0f) / 100f, modifier = Modifier.fillMaxWidth())
            Text("${state.progress?.questionsCorrect ?: 0} correct out of ${state.progress?.questionsAttempted ?: 0} attempted")
            Spacer(Modifier.height(20.dp))

            Button(onClick = { navController.navigate(Screen.Mcq.build("CHAPTER", chapter.subject, chapter.id)) }, modifier = Modifier.fillMaxWidth()) {
                Text("Practice MCQs (${state.mcqCount})")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { navController.navigate(Screen.QA.build(chapter.id)) }, modifier = Modifier.fillMaxWidth()) {
                Text("Important Questions (${state.qaCount})")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { navController.navigate(Screen.Test.build(chapter.subject, chapter.id)) }, modifier = Modifier.fillMaxWidth()) {
                Text("Take Chapter Test")
            }
        }
    }
}
