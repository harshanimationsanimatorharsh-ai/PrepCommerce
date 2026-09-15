package com.prepcommerce.app.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prepcommerce.app.data.repository.GameRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

data class MathGameState(
    val question: String = "", val answer: Int = 0, val options: List<Int> = emptyList(),
    val score: Int = 0, val streak: Int = 0, val timeLeft: Int = 60, val level: Int = 1,
    val finished: Boolean = false, val feedback: String? = null
)

class MathChallengeViewModel(private val gameRepo: GameRepository) : ViewModel() {
    private val _state = MutableStateFlow(MathGameState())
    val state: StateFlow<MathGameState> = _state.asStateFlow()
    private var timerJob: Job? = null

    fun start() {
        _state.value = MathGameState()
        nextQuestion()
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.timeLeft > 0 && !_state.value.finished) {
                delay(1000); _state.value = _state.value.copy(timeLeft = _state.value.timeLeft - 1)
            }
            finish()
        }
    }
    private fun nextQuestion() {
        val level = 1 + _state.value.score / 30
        val max = 10 * level
        val a = Random.nextInt(1, max); val b = Random.nextInt(1, max)
        val op = listOf("+", "-", "×").random()
        val (q, ans) = when (op) { "+" -> "$a + $b" to a + b; "-" -> "$a - $b" to a - b; else -> "$a × $b" to a * b }
        val options = mutableSetOf(ans)
        while (options.size < 4) { val d = Random.nextInt(-10, 11); options.add(ans + if (d == 0) 1 else d) }
        _state.value = _state.value.copy(question = q, answer = ans, options = options.shuffled(), level = level, feedback = null)
    }
    fun answer(opt: Int) {
        val correct = opt == _state.value.answer
        val newStreak = if (correct) _state.value.streak + 1 else 0
        val bonus = if (newStreak > 0 && newStreak % 5 == 0) 20 else 0
        val newScore = _state.value.score + if (correct) 10 + bonus else 0
        _state.value = _state.value.copy(score = newScore, streak = newStreak, feedback = if (correct) "correct" else "wrong")
        nextQuestion()
    }
    private fun finish() {
        _state.value = _state.value.copy(finished = true)
        viewModelScope.launch { gameRepo.saveScore("MATH_CHALLENGE", _state.value.score, _state.value.level) }
    }
    override fun onCleared() { timerJob?.cancel(); super.onCleared() }
}
