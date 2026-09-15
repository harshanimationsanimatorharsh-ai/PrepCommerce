package com.prepcommerce.app.ui.games

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.prepcommerce.app.data.local.entities.QuestionEntity
import com.prepcommerce.app.data.repository.ContentRepository
import com.prepcommerce.app.data.repository.GameRepository
import com.prepcommerce.app.data.repository.ProfileRepository
import com.prepcommerce.app.ui.components.EmptyState
import com.prepcommerce.app.ui.components.LoadingState
import com.prepcommerce.app.ui.components.OptionRow
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class SpeedState(
    val questions: List<QuestionEntity> = emptyList(), val index: Int = 0, val score: Int = 0,
    val timeLeft: Int = 60, val finished: Boolean = false, val loading: Boolean = true
)

class SpeedChallengeViewModel(
    private val gameRepo: GameRepository, private val contentRepo: ContentRepository, private val profileRepo: ProfileRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SpeedState())
    val state: StateFlow<SpeedState> = _state.asStateFlow()
    private var timerJob: Job? = null

    fun start() {
        viewModelScope.launch {
            _state.value = SpeedState(loading = true)
            val profile = profileRepo.profileFlow().first()
            val pool = contentRepo.allMcqs(profile?.classLevel ?: 12, profile?.board ?: "CBSE").shuffled()
            if (pool.isEmpty()) { _state.value = SpeedState(loading = false); return@launch }
            _state.value = SpeedState(questions = pool, loading = false)
            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                while (_state.value.timeLeft > 0 && !_state.value.finished) { delay(1000); _state.value = _state.value.copy(timeLeft = _state.value.timeLeft - 1) }
                finish()
            }
        }
    }
    fun answer(optIndex: Int) {
        val s = _state.value
        if (s.finished || s.index >= s.questions.size) return
        val correct = s.questions[s.index].correctIndex == optIndex
        val nextIndex = s.index + 1
        val newScore = s.score + if (correct) 1 else 0
        if (nextIndex >= s.questions.size) { _state.value = s.copy(score = newScore, index = nextIndex); finish() }
        else _state.value = s.copy(score = newScore, index = nextIndex)
    }
    private fun finish() {
        timerJob?.cancel()
        if (_state.value.finished) return
        _state.value = _state.value.copy(finished = true)
        viewModelScope.launch { gameRepo.saveScore("SPEED_CHALLENGE", _state.value.score, 1) }
    }
    override fun onCleared() { timerJob?.cancel(); super.onCleared() }
}

@Composable
fun SpeedChallengeScreen(viewModel: SpeedChallengeViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.start() }

    when {
        state.loading -> LoadingState()
        state.questions.isEmpty() -> EmptyState("Not enough questions to run Speed Challenge yet.")
        else -> Column(Modifier.fillMaxSize().padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Score: ${state.score}", fontWeight = FontWeight.Bold)
                Text("Time: ${state.timeLeft}s", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(20.dp))
            if (!state.finished && state.index < state.questions.size) {
                val q = state.questions[state.index]
                Text(q.questionText, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                q.options.forEachIndexed { i, opt -> OptionRow(opt, i, false, false, false) { viewModel.answer(i) } }
            } else {
                Text("Finished! Final Score: ${state.score}", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { viewModel.start() }) { Text("Play Again") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { navController.popBackStack() }) { Text("Back to Games") }
            }
        }
    }
}
