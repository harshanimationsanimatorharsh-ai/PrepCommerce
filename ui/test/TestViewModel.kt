package com.prepcommerce.app.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prepcommerce.app.data.local.entities.QuestionEntity
import com.prepcommerce.app.data.local.entities.TestAttemptEntity
import com.prepcommerce.app.data.repository.AttemptRepository
import com.prepcommerce.app.data.repository.ContentRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

enum class QStatus { NOT_VISITED, NOT_ANSWERED, ANSWERED, MARKED, ANSWERED_MARKED }

data class TestUiState(
    val questions: List<QuestionEntity> = emptyList(), val currentIndex: Int = 0,
    val answers: Map<String, Int?> = emptyMap(), val marked: Set<String> = emptySet(),
    val visited: Set<String> = emptySet(), val remainingSeconds: Int = 0,
    val submitted: Boolean = false, val result: TestAttemptEntity? = null, val loading: Boolean = true
)

class TestViewModel(private val contentRepo: ContentRepository, private val attemptRepo: AttemptRepository) : ViewModel() {
    private val _state = MutableStateFlow(TestUiState())
    val state: StateFlow<TestUiState> = _state.asStateFlow()
    private var timerJob: Job? = null
    private var mode = "TEST50"; private var subject = ""; private var classLevel = 12; private var board = "CBSE"
    private var chapterId: String? = null; private var startTime = 0L

    fun start(subject: String, classLevel: Int, board: String, mode: String = "TEST50",
               chapterId: String? = null, questionCount: Int = 50, timeMinutes: Int = 60) {
        this.subject = subject; this.classLevel = classLevel; this.board = board; this.mode = mode; this.chapterId = chapterId
        viewModelScope.launch {
            _state.value = TestUiState(loading = true)
            val pool = if (chapterId != null) contentRepo.mcqsForChapter(chapterId)
                       else contentRepo.mcqsForSubject(subject, classLevel, board)
            val selected = pool.shuffled().take(questionCount)
            startTime = System.currentTimeMillis()
            _state.value = TestUiState(
                questions = selected,
                visited = if (selected.isNotEmpty()) setOf(selected[0].id) else emptySet(),
                remainingSeconds = timeMinutes * 60, loading = false
            )
            if (selected.isNotEmpty()) startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.remainingSeconds > 0 && !_state.value.submitted) {
                delay(1000); _state.value = _state.value.copy(remainingSeconds = _state.value.remainingSeconds - 1)
            }
            if (!_state.value.submitted) submit()
        }
    }

    fun goto(index: Int) {
        val s = _state.value
        if (index !in s.questions.indices) return
        _state.value = s.copy(currentIndex = index, visited = s.visited + s.questions[index].id)
    }
    fun next() = goto(_state.value.currentIndex + 1)
    fun previous() = goto(_state.value.currentIndex - 1)

    fun selectAnswer(optionIndex: Int) {
        val s = _state.value; val q = s.questions.getOrNull(s.currentIndex) ?: return
        _state.value = s.copy(answers = s.answers + (q.id to optionIndex))
    }
    fun clearAnswer() {
        val s = _state.value; val q = s.questions.getOrNull(s.currentIndex) ?: return
        _state.value = s.copy(answers = s.answers - q.id)
    }
    fun toggleMark() {
        val s = _state.value; val q = s.questions.getOrNull(s.currentIndex) ?: return
        val newMarked = if (q.id in s.marked) s.marked - q.id else s.marked + q.id
        _state.value = s.copy(marked = newMarked)
    }
    fun statusFor(q: QuestionEntity): QStatus {
        val s = _state.value
        val answered = s.answers[q.id] != null; val marked = q.id in s.marked
        return when {
            answered && marked -> QStatus.ANSWERED_MARKED
            marked -> QStatus.MARKED
            answered -> QStatus.ANSWERED
            q.id in s.visited -> QStatus.NOT_ANSWERED
            else -> QStatus.NOT_VISITED
        }
    }

    fun submit() {
        timerJob?.cancel()
        val s = _state.value
        if (s.submitted || s.questions.isEmpty()) return
        val timeTaken = ((System.currentTimeMillis() - startTime) / 1000).toInt()
        viewModelScope.launch {
            val fullAnswers = s.questions.associate { it.id to s.answers[it.id] }
            val attempt = attemptRepo.submitTest(mode, subject, chapterId, classLevel, board, fullAnswers, s.marked, timeTaken)
            _state.value = s.copy(submitted = true, result = attempt)
        }
    }
    override fun onCleared() { timerJob?.cancel(); super.onCleared() }
}
