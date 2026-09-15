package com.prepcommerce.app.ui.mcq

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prepcommerce.app.data.local.entities.QuestionEntity
import com.prepcommerce.app.data.repository.BookmarkRepository
import com.prepcommerce.app.data.repository.ContentRepository
import com.prepcommerce.app.data.repository.ProfileRepository
import com.prepcommerce.app.gamification.XpEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class McqUiState(
    val questions: List<QuestionEntity> = emptyList(), val index: Int = 0, val selected: Int? = null,
    val submitted: Boolean = false, val correctCount: Int = 0, val bookmarked: Boolean = false,
    val loading: Boolean = true, val reported: Boolean = false
)

class McqViewModel(private val contentRepo: ContentRepository, private val bookmarkRepo: BookmarkRepository,
                    private val profileRepo: ProfileRepository) : ViewModel() {
    private val _state = MutableStateFlow(McqUiState())
    val state: StateFlow<McqUiState> = _state.asStateFlow()

    fun load(mode: String, subject: String, classLevel: Int, board: String, chapterId: String? = null) {
        viewModelScope.launch {
            _state.value = McqUiState(loading = true)
            val qs = when (mode) {
                "CHAPTER" -> chapterId?.let { contentRepo.mcqsForChapter(it) } ?: emptyList()
                "IMPORTANT" -> contentRepo.importantQuestions(subject, classLevel)
                "BOARD" -> contentRepo.mcqsForSubject(subject, classLevel, board)
                else -> contentRepo.mcqsForSubject(subject, classLevel, board).shuffled()
            }
            if (qs.isEmpty()) { _state.value = McqUiState(loading = false); return@launch }
            _state.value = McqUiState(questions = qs, loading = false)
            checkBookmark()
        }
    }

    fun selectOption(i: Int) { if (!_state.value.submitted) _state.value = _state.value.copy(selected = i) }

    fun submit() {
        val s = _state.value
        val q = s.questions.getOrNull(s.index) ?: return
        if (s.selected == null) return
        val correct = s.selected == q.correctIndex
        viewModelScope.launch { profileRepo.addXp(if (correct) XpEngine.XP_MCQ_CORRECT else XpEngine.XP_MCQ_WRONG) }
        _state.value = s.copy(submitted = true, correctCount = s.correctCount + if (correct) 1 else 0)
    }

    fun next() {
        val s = _state.value
        if (s.index < s.questions.size - 1) { _state.value = s.copy(index = s.index + 1, selected = null, submitted = false, reported = false); checkBookmark() }
    }
    fun previous() {
        val s = _state.value
        if (s.index > 0) { _state.value = s.copy(index = s.index - 1, selected = null, submitted = false, reported = false); checkBookmark() }
    }
    fun toggleBookmark() {
        val q = _state.value.questions.getOrNull(_state.value.index) ?: return
        viewModelScope.launch { bookmarkRepo.toggle(q.id, q.subject); checkBookmark() }
    }
    fun reportQuestion() {
        val q = _state.value.questions.getOrNull(_state.value.index) ?: return
        viewModelScope.launch { bookmarkRepo.report(q.id); _state.value = _state.value.copy(reported = true) }
    }
    private fun checkBookmark() {
        val q = _state.value.questions.getOrNull(_state.value.index) ?: return
        viewModelScope.launch { _state.value = _state.value.copy(bookmarked = bookmarkRepo.isBookmarked(q.id)) }
    }
}
