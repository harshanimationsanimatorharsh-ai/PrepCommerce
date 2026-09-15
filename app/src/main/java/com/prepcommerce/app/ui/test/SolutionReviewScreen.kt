package com.prepcommerce.app.ui.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prepcommerce.app.ui.components.LoadingState
import com.prepcommerce.app.ui.components.SimpleTopBar

@Composable
fun SolutionReviewScreen(attemptId: Long, viewModel: TestResultViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(attemptId) { viewModel.load(attemptId) }

    Column(Modifier.fillMaxSize()) {
        SimpleTopBar("Solutions", onBack = { navController.popBackStack() })
        if (state.loading) { LoadingState() } else {
            LazyColumn(Modifier.padding(16.dp)) {
                solutionItems(state.reviews)
            }
        }
    }
}

private fun LazyListScope.solutionItems(reviews: List<AnswerReview>) {
    items(reviews.size) { i ->
        val r = reviews[i]
        Card(modifier = Modifier.padding(vertical = 6.dp)) {
            Column(Modifier.padding(12.dp)) {
                Text("Q${i + 1}. ${r.question.questionText}", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                r.question.options.forEachIndexed { idx, opt ->
                    val label = when {
                        idx == r.question.correctIndex -> "✅ $opt (Correct Answer)"
                        idx == r.answer.selectedIndex && idx != r.question.correctIndex -> "❌ $opt (Your Answer)"
                        else -> opt
                    }
                    Text(label)
                }
                if (r.answer.selectedIndex == null) Text("You did not attempt this question.")
                Spacer(Modifier.height(6.dp))
                Text("Explanation:", fontWeight = FontWeight.Bold)
                Text(r.question.explanation)
            }
        }
    }
}
