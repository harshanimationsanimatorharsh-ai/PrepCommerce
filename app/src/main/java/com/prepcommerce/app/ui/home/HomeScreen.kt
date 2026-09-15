package com.prepcommerce.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prepcommerce.app.navigation.Screen
import com.prepcommerce.app.ui.components.StatBox

@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavController) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Welcome, ${state.profile.name}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("CBSE", "MP").forEach { b ->
                    FilterChip(selected = state.profile.board == b, onClick = { viewModel.setBoardClass(b, state.profile.classLevel) }, label = { Text(b) })
                }
                listOf(11, 12).forEach { c ->
                    FilterChip(selected = state.profile.classLevel == c, onClick = { viewModel.setBoardClass(state.profile.board, c) }, label = { Text("Class $c") })
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatBox("XP", "${state.profile.xp}", Modifier.weight(1f))
                StatBox("Level", "${state.profile.level}", Modifier.weight(1f))
                StatBox("Streak", "${state.profile.streak}🔥", Modifier.weight(1f))
                StatBox("Best %", "${state.profile.bestScorePercent.toInt()}", Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { navController.navigate(Screen.DailyChallenge.route) }, modifier = Modifier.weight(1f)) { Text("Daily Challenge") }
                OutlinedButton(onClick = { navController.navigate(Screen.Search.route) }, modifier = Modifier.weight(1f)) { Text("Search") }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { navController.navigate(Screen.Bookmarks.route) }, modifier = Modifier.weight(1f)) { Text("Bookmarks") }
                OutlinedButton(onClick = { navController.navigate(Screen.Progress.route) }, modifier = Modifier.weight(1f)) { Text("Progress") }
            }
            Spacer(Modifier.height(16.dp))
        }
        item {
            Text("Subjects", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SUBJECTS) { subject ->
                    Card(onClick = { navController.navigate(Screen.Chapters.build(subject)) }) {
                        Box(Modifier.padding(16.dp)) { Text(subject) }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        item {
            Text("Recent Attempts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
        }
        if (state.recentAttempts.isEmpty()) {
            item { Text("No tests attempted yet. Start practicing!") }
        } else {
            items(state.recentAttempts) { attempt ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(attempt.subject, fontWeight = FontWeight.Bold)
                            Text(attempt.mode, style = MaterialTheme.typography.bodySmall)
                        }
                        Text("${attempt.scorePercent.toInt()}%", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}
