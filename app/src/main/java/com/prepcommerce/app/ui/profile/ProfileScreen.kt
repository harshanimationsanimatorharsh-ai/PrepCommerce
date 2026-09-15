package com.prepcommerce.app.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.prepcommerce.app.ui.components.StatBox

@Composable
fun ProfileScreen(viewModel: ProfileViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()
    var editingName by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(state.profile.name) }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        if (editingName) {
            OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }, label = { Text("Name") })
            Row {
                TextButton(onClick = { viewModel.updateName(nameInput); editingName = false }) { Text("Save") }
                TextButton(onClick = { editingName = false }) { Text("Cancel") }
            }
        } else {
            Text(state.profile.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            TextButton(onClick = { editingName = true }) { Text("Edit Name") }
        }
        Text("${state.profile.board} • Class ${state.profile.classLevel}")
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatBox("Level", "${state.profile.level}", Modifier.weight(1f))
            StatBox("XP", "${state.profile.xp}", Modifier.weight(1f))
            StatBox("Streak", "${state.profile.streak}", Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatBox("Tests Taken", "${state.profile.totalTests}", Modifier.weight(1f))
            StatBox("Best Score", "${state.profile.bestScorePercent.toInt()}%", Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(progress = (500f - viewModel.xpToNextLevel(state.profile.xp)) / 500f, modifier = Modifier.fillMaxWidth())
        Text("${viewModel.xpToNextLevel(state.profile.xp)} XP to next level")
        Spacer(Modifier.height(20.dp))
        Text("Achievements", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        val achievements = buildList {
            if (state.attempts.isNotEmpty()) add("🎯 First Test Completed")
            if (state.attempts.size >= 10) add("📚 10 Tests Completed")
            if (state.profile.streak >= 3) add("🔥 3-Day Streak")
            if (state.profile.streak >= 7) add("🔥🔥 7-Day Streak")
            if (state.profile.bestScorePercent >= 90f) add("🏆 90%+ Scorer")
        }
        if (achievements.isEmpty()) Text("Keep practicing to unlock achievements!")
        achievements.forEach { Text(it) }
    }
}
