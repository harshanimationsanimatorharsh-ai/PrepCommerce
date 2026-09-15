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
import com.prepcommerce.app.ui.components.LoadingState
import com.prepcommerce.app.ui.components.OptionRow
import com.prepcommerce.app.ui.components.SimpleTopBar

@Composable
fun SamplePaperAttemptScreen(paperId: String, viewModel: SamplePaperViewModel, navController: NavController) {
    val state by viewModel.attemptState.collectAsState()
    LaunchedEffect(paperId) { viewModel.loadPaper(paperId) }

    if (state.loading || state.paper == null) { LoadingState(); return }
    val paper = state.paper!!

    Column(Modifier.fillMaxSize()) {
        SimpleTopBar(paper.title, onBack = { navController.popBackStack() })
        if (!state.started) {
            Column(Modifier.fillMaxSize().padding(20.dp)) {
                Text("Instructions", fontWeight = FontWeight.Bold)
                Text(paper.instructions)
                Spacer(Modifier.height(8.dp))
                Text("Duration: ${paper.durationMinutes} minutes")
                Text("Objective Questions: ${state.objectiveQuestions.size}")
                Text("Subjective Questions: ${state.subjectiveQuestions.size}")
                Spacer(Modifier.height(20.dp))
                Button(onClick = { viewModel.startAttempt() }) { Text("Start Paper") }
            }
        } else if (!state.submitted) {
            Column(Modifier.fillMaxSize()) {
                Text("Time Left: ${state.remainingSeconds / 60}m ${state.remainingSeconds % 60}s", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                LazyColumn(Modifier.weight(1f).padding(horizontal = 16.dp)) {
                    item { Text("Section A — Objective", fontWeight = FontWeight.Bold) }
                    items(state.objectiveQuestions) { q ->
                        Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text(q.questionText, fontWeight = FontWeight.Bold)
                                q.options.forEachIndexed { i, opt ->
                                    OptionRow(opt, i, state.answers[q.id] == i, false, false) { viewModel.selectAnswer(q.id, i) }
                                }
                            }
                        }
                    }
                    item { Spacer(Modifier.height(12.dp)); Text("Section B — Subjective (Self-check with model answers)", fontWeight = FontWeight.Bold) }
                    items(state.subjectiveQuestions) { q ->
                        Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text(q.questionText, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(6.dp))
                                if (q.id in state.revealedSubjective) {
                                    Text("Model Answer:", fontWeight = FontWeight.Bold)
                                    Text(q.explanation)
                                } else {
                                    TextButton(onClick = { viewModel.revealSubjective(q.id) }) { Text("Show Model Answer") }
                                }
                            }
                        }
                    }
                }
                Button(onClick = { viewModel.submit() }, modifier = Modifier.fillMaxWidth().padding(16.dp)) { Text("Submit Paper") }
            }
        } else {
            Column(Modifier.fillMaxSize().padding(20.dp)) {
                Text("Objective Score: ${state.correct} / ${state.objectiveQuestions.size}", style = MaterialTheme.typography.headlineSmall)
                Text("Subjective answers must be self-evaluated using the model answers shown above.")
                Spacer(Modifier.height(20.dp))
                Button(onClick = { navController.popBackStack() }) { Text("Back") }
            }
        }
    }
}
