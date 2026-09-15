package com.prepcommerce.app.ui.test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prepcommerce.app.navigation.Screen
import com.prepcommerce.app.ui.components.LoadingState
import com.prepcommerce.app.ui.components.StatBox

@Composable
fun TestResultScreen(attemptId: Long, viewModel: TestResultViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(attemptId) { viewModel.load(attemptId) }

    if (state.loading || state.attempt == null) { LoadingState(); return }
    val attempt = state.attempt!!

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Test Result", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Text("${attempt.scorePercent.toInt()}%", style = MaterialTheme.typography.displayMedium)
        Text("${attempt.correct} / ${attempt.totalQuestions} correct")
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatBox("Correct", "${attempt.correct}", Modifier.weight(1f))
            StatBox("Wrong", "${attempt.wrong}", Modifier.weight(1f))
            StatBox("Skipped", "${attempt.unattempted}", Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatBox("Accuracy", "${attempt.accuracy.toInt()}%", Modifier.weight(1f))
            StatBox("Time", "${attempt.timeTakenSeconds / 60}m ${attempt.timeTakenSeconds % 60}s", Modifier.weight(1f))
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = { navController.navigate(Screen.Solution.build(attemptId)) }, modifier = Modifier.fillMaxWidth()) { Text("View Solutions") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = {
            navController.navigate(Screen.Test.build(attempt.subject, attempt.chapterId ?: "none")) {
                popUpTo(Screen.Home.route)
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Retry Test") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } }, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Dashboard")
        }
    }
}
