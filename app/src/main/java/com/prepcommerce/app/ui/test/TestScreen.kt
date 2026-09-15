package com.prepcommerce.app.ui.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prepcommerce.app.navigation.Screen
import com.prepcommerce.app.ui.components.EmptyState
import com.prepcommerce.app.ui.components.LoadingState
import com.prepcommerce.app.ui.components.OptionRow

@Composable
fun TestScreen(subject: String, chapterId: String, viewModel: TestViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()
    var showPalette by remember { mutableStateOf(false) }
    var showSubmitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(subject, chapterId) {
        viewModel.start(subject, chapterId, mode = if (chapterId == "none") "TEST50" else "CHAPTER_TEST",
            questionCount = if (chapterId == "none") 50 else 15, timeMinutes = if (chapterId == "none") 60 else 20)
    }

    LaunchedEffect(state.submitted) {
        if (state.submitted && state.result != null) {
            navController.navigate(Screen.Result.build(state.result!!.id)) {
                popUpTo(Screen.Test.route) { inclusive = true }
            }
        }
    }

    when {
        state.loading -> LoadingState()
        state.questions.isEmpty() -> EmptyState("Not enough questions available to start this test.")
        else -> {
            val q = state.questions[state.currentIndex]
            val minutes = state.remainingSeconds / 60
            val seconds = state.remainingSeconds % 60

            Column(Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text("Q${state.currentIndex + 1}/${state.questions.size}") },
                    actions = {
                        Text(String.format("%02d:%02d", minutes, seconds), modifier = Modifier.padding(end = 16.dp))
                        TextButton(onClick = { showPalette = !showPalette }) { Text("Palette") }
                    }
                )
                if (showPalette) {
                    LazyVerticalGrid(columns = GridCells.Fixed(6), modifier = Modifier.padding(8.dp).heightIn(max = 220.dp)) {
                        items(state.questions.size) { i ->
                            val status = viewModel.statusFor(state.questions[i])
                            val color = when (status) {
                                QStatus.ANSWERED -> Color(0xFF66BB6A)
                                QStatus.MARKED -> Color(0xFFAB47BC)
                                QStatus.ANSWERED_MARKED -> Color(0xFF7E57C2)
                                QStatus.NOT_ANSWERED -> Color(0xFFEF5350)
                                QStatus.NOT_VISITED -> Color(0xFFBDBDBD)
                            }
                            Button(
                                onClick = { viewModel.goto(i); showPalette = false },
                                colors = ButtonDefaults.buttonColors(containerColor = color),
                                modifier = Modifier.padding(3.dp).size(40.dp), contentPadding = PaddingValues(0.dp)
                            ) { Text("${i + 1}") }
                        }
                    }
                    Divider()
                }
                Column(Modifier.weight(1f).padding(16.dp)) {
                    Text(q.questionText, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(16.dp))
                    q.options.forEachIndexed { i, opt ->
                        OptionRow(text = opt, index = i, selected = state.answers[q.id] == i, showResult = false, isCorrectOption = false,
                            onClick = { viewModel.selectAnswer(i) })
                    }
                }
                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { viewModel.clearAnswer() }, modifier = Modifier.weight(1f)) { Text("Clear") }
                    OutlinedButton(onClick = { viewModel.toggleMark() }, modifier = Modifier.weight(1f)) { Text("Mark") }
                    OutlinedButton(onClick = { viewModel.previous() }, enabled = state.currentIndex > 0, modifier = Modifier.weight(1f)) { Text("Prev") }
                    if (state.currentIndex < state.questions.size - 1) {
                        Button(onClick = { viewModel.next() }, modifier = Modifier.weight(1f)) { Text("Next") }
                    } else {
                        Button(onClick = { showSubmitDialog = true }, modifier = Modifier.weight(1f)) { Text("Submit") }
                    }
                }
            }

            if (showSubmitDialog) {
                val answered = state.answers.count { it.value != null }
                AlertDialog(
                    onDismissRequest = { showSubmitDialog = false },
                    title = { Text("Submit Test?") },
                    text = { Text("Answered: $answered / ${state.questions.size}\nUnattempted: ${state.questions.size - answered}\n\nYou cannot change answers after submitting.") },
                    confirmButton = { TextButton(onClick = { showSubmitDialog = false; viewModel.submit() }) { Text("Submit") } },
                    dismissButton = { TextButton(onClick = { showSubmitDialog = false }) { Text("Cancel") } }
                )
            }
        }
    }
}
