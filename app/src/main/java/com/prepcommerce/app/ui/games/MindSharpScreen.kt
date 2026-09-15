package com.prepcommerce.app.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.prepcommerce.app.data.repository.GameRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

enum class MindPhase { IDLE, SHOWING, INPUT, GAMEOVER }

data class MindSharpState(
    val sequence: List<Int> = emptyList(), val highlightedIndex: Int = -1,
    val userIndex: Int = 0, val phase: MindPhase = MindPhase.IDLE, val score: Int = 0
)

class MindSharpViewModel(private val gameRepo: GameRepository) : ViewModel() {
    private val _state = MutableStateFlow(MindSharpState())
    val state: StateFlow<MindSharpState> = _state.asStateFlow()

    fun start() {
        _state.value = MindSharpState(sequence = listOf((1..9).random()), phase = MindPhase.IDLE)
        showSequence()
    }
    private fun showSequence() {
        viewModelScope.launch {
            _state.value = _state.value.copy(phase = MindPhase.SHOWING, userIndex = 0)
            for (i in _state.value.sequence.indices) {
                _state.value = _state.value.copy(highlightedIndex = _state.value.sequence[i])
                delay(600)
                _state.value = _state.value.copy(highlightedIndex = -1)
                delay(250)
            }
            _state.value = _state.value.copy(phase = MindPhase.INPUT)
        }
    }
    fun tap(number: Int) {
        val s = _state.value
        if (s.phase != MindPhase.INPUT) return
        if (s.sequence[s.userIndex] == number) {
            val nextIndex = s.userIndex + 1
            if (nextIndex == s.sequence.size) {
                val newScore = s.sequence.size
                _state.value = s.copy(score = newScore, sequence = s.sequence + (1..9).random())
                showSequence()
            } else {
                _state.value = s.copy(userIndex = nextIndex)
            }
        } else {
            _state.value = s.copy(phase = MindPhase.GAMEOVER)
            viewModelScope.launch { gameRepo.saveScore("MIND_SHARP", s.score, s.sequence.size) }
        }
    }
}

@Composable
fun MindSharpScreen(viewModel: MindSharpViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.start() }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Score: ${state.score}", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(when (state.phase) {
            MindPhase.SHOWING -> "Watch the sequence..."
            MindPhase.INPUT -> "Repeat the sequence!"
            MindPhase.GAMEOVER -> "Game Over!"
            else -> ""
        })
        Spacer(Modifier.height(24.dp))
        if (state.phase != MindPhase.GAMEOVER) {
            LazyVerticalGrid(columns = GridCells.Fixed(3), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items((1..9).toList()) { n ->
                    val highlighted = state.highlightedIndex == n
                    val enabled = state.phase == MindPhase.INPUT
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(if (highlighted) Color(0xFFFFCA28) else MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                            .clickable(enabled = enabled) { viewModel.tap(n) },
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) { Text("$n", style = MaterialTheme.typography.headlineMedium) }
                }
            }
        } else {
            Text("Final Score (sequence length): ${state.score}", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { viewModel.start() }) { Text("Play Again") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { navController.popBackStack() }) { Text("Back to Games") }
        }
    }
}
