package com.prepcommerce.app.ui.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prepcommerce.app.navigation.Screen

data class GameCardInfo(val title: String, val route: String, val type: String)

@Composable
fun GamesHubScreen(viewModel: GamesHubViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()
    val games = listOf(
        GameCardInfo("Math Challenge", Screen.MathChallenge.route, "MATH_CHALLENGE"),
        GameCardInfo("Equation Game", Screen.EquationGame.route, "EQUATION_GAME"),
        GameCardInfo("Mind Sharp", Screen.MindSharp.route, "MIND_SHARP"),
        GameCardInfo("Puzzle", Screen.PuzzleGame.route, "PUZZLE_GAME"),
        GameCardInfo("Speed Challenge", Screen.SpeedChallenge.route, "SPEED_CHALLENGE")
    )
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Study Games", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Button(onClick = { navController.navigate(Screen.DailyChallenge.route) }, modifier = Modifier.fillMaxWidth()) { Text("🎯 Daily Challenge") }
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(games) { g ->
                Card(onClick = { navController.navigate(g.route) }) {
                    Column(Modifier.padding(16.dp)) {
                        Text(g.title, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("Best: ${state.bestScores[g.type] ?: 0}")
                    }
                }
            }
        }
    }
}
