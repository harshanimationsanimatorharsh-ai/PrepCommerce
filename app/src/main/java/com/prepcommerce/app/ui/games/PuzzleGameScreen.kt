package com.prepcommerce.app.ui.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.prepcommerce.app.data.repository.GameRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

data class PuzzleState(
    val sequenceText: String = "", val answer: Int = 0, val options: List<Int> = emptyList(),
    val score: Int = 0, val level: Int = 1, val lives: Int = 3, val finished: Boolean = false
)

class PuzzleGameViewModel(private val gameRepo: GameRepository) : ViewModel() {
    private val _state = MutableStateFlow(PuzzleState())
    val state: StateFlow<PuzzleState> = _state.asStateFlow()

    fun start() { _state.value = PuzzleState(); nextPuzzle() }

    private fun nextPuzzle() {
        val level = _state.value.level
        val start = Random.nextInt(1, 10)
        val diff = Random.nextInt(2, 5 + level)
        val isArithmetic = Random.nextBoolean()
        val seq: List<Int>
        val answer: Int
        if (isArithmetic) {
            seq = (0..3).map { start + it * diff }
            answer = start + 4 * diff
        } else {
            seq = (0..3).map { start * Math.pow(diff.toDouble(), it.toDouble()).toInt() }
            answer = start * Math.pow(diff.toDouble(), 4.0).toInt()
        }
        val options = mutableSetOf(answer)
        while (options.size < 4) { val d = Random.nextInt(-diff, diff + 1); options.add(answer + if (d == 0) 1 else d) }
        _state.value = _state.value.copy(sequenceText = seq.joinToString(", ") + ", ?", answer = answer, options = options.shuffled())
    }

    fun answer(opt: Int) {
        val s = _state.value
        if (opt == s.answer) {
            val newScore = s.score + 20
            _state.value = s.copy(score = newScore, level = 1 + newScore / 60)
            nextPuzzle()
        } else {
            val newLives = s.lives - 1
            if (newLives <= 0) {
                _state.value = s.copy(lives = 0, finished = true)
                viewModelScope.launch { gameRepo.saveScore("PUZZLE_GAME", s.score, s.level) }
            } else {
                _state.value = s.copy(lives = newLives)
                nextPuzzle()
            }
        }
    }
}

@Composable
fun PuzzleGameScreen(viewModel: PuzzleGameViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.start() }
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Score: ${state.score}", fontWeight = FontWeight.Bold)
            Text("Lives: ${"❤️".repeat(state.lives)}")
        }
        Spacer(Modifier.height(40.dp))
        if (!state.finished) {
            Text("Find the next number:", style = MaterialTheme.typography.titleMedium)
            Text(state.sequenceText, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(30.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.options) { opt -> Button(onClick = { viewModel.answer(opt) }, modifier = Modifier.fillMaxWidth()) { Text("$opt") } }
            }
        } else {
            Text("Game Over! Final Score: ${state.score}", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { viewModel.start() }) { Text("Play Again") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { navController.popBackStack() }) { Text("Back to Games") }
        }
    }
}
