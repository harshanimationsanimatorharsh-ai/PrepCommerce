package com.prepcommerce.app.ui.mcq

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prepcommerce.app.ui.components.DifficultyChip
import com.prepcommerce.app.ui.components.EmptyState
import com.prepcommerce.app.ui.components.LoadingState
import com.prepcommerce.app.ui.components.OptionRow
import com.prepcommerce.app.ui.components.SimpleTopBar

@Composable
fun McqPracticeScreen(mode: String, subject: String, chapterId: String, viewModel: McqViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(mode, subject, chapterId) { viewModel.load(mode, subject, chapterId) }

    Column(Modifier.fillMaxSize()) {
        SimpleTopBar("$subject MCQs", onBack = { navController.popBackStack() })
        when {
            state.loading -> LoadingState()
            state.questions.isEmpty() -> EmptyState("No MCQs available for this selection yet.")
            else -> {
                val q = state.questions[state.index]
                Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Q${state.index + 1}/${state.questions.size}", fontWeight = FontWeight.Bold)
                        Row {
                            IconButton(onClick = { viewModel.toggleBookmark() }) {
                                Icon(if (state.bookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder, contentDescription = "Bookmark")
                            }
                            IconButton(onClick = { viewModel.reportQuestion() }) {
                                Icon(Icons.Filled.Flag, contentDescription = "Report")
                            }
                        }
                    }
                    if (state.reported) Text("Reported. Thank you!", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DifficultyChip(q.difficulty)
                        AssistChip(onClick = {}, label = { Text(q.topic) })
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(q.questionText, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(16.dp))
                    q.options.forEachIndexed { i, opt ->
                        OptionRow(
                            text = opt, index = i, selected = state.selected == i,
                            showResult = state.submitted, isCorrectOption = i == q.correctIndex,
                            onClick = { viewModel.selectOption(i) }
                        )
                    }
                    if (state.submitted) {
                        Spacer(Modifier.height(12.dp))
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text(if (state.selected == q.correctIndex) "✅ Correct!" else "❌ Wrong! Correct answer: ${('A' + q.correctIndex)}", fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(6.dp))
                                Text("Explanation:", fontWeight = FontWeight.Bold)
                                Text(q.explanation)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { viewModel.previous() }, enabled = state.index > 0) { Text("Previous") }
                        if (!state.submitted) {
                            Button(onClick = { viewModel.submit() }, enabled = state.selected != null) { Text("Submit") }
                        } else {
                            Button(onClick = { viewModel.next() }, enabled = state.index < state.questions.size - 1) { Text("Next") }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}
