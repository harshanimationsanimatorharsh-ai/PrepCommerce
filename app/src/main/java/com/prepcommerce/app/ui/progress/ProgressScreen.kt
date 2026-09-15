package com.prepcommerce.app.ui.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.prepcommerce.app.ui.components.EmptyState

@Composable
fun ProgressScreen(viewModel: ProgressViewModel) {
    val state by viewModel.state.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Progress & Analytics", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text("Overall Accuracy: ${state.overallAccuracy.toInt()}%", fontWeight = FontWeight.Bold)
        LinearProgressIndicator(progress = state.overallAccuracy / 100f, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Text("Subject-wise Performance", fontWeight = FontWeight.Bold)
        if (state.subjectProgress.isEmpty()) {
            EmptyState("No test attempts yet. Take a test to see your progress.")
        } else {
            LazyColumn {
                items(state.subjectProgress) { sp ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(sp.subject, fontWeight = FontWeight.Bold)
                            Text("${sp.testsCount} tests • Avg Accuracy: ${sp.avgAccuracy.toInt()}%")
                            LinearProgressIndicator(progress = sp.avgAccuracy / 100f, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)); Text("Recent Attempts", fontWeight = FontWeight.Bold) }
                items(state.recentAttempts) { a ->
                    Text("${a.subject} — ${a.scorePercent.toInt()}% (${a.mode})")
                }
            }
        }
    }
}
