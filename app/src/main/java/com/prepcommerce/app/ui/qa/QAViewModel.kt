package com.prepcommerce.app.ui.qa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prepcommerce.app.data.local.entities.QuestionEntity
import com.prepcommerce.app.data.repository.BookmarkRepository
import com.prepcommerce.app.data.repository.ContentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QAUiState(val questions: List<QuestionEntity> = emptyList(), val loading: Boolean = true, val expandedIds: Set<String> = emptySet())

class QAViewModel(private val contentRepo: ContentRepository, private val bookmarkRepo: BookmarkRepository) : ViewModel() {
    private val _state = MutableStateFlow(QAUiState())
    val state: StateFlow<QAUiState> = _state.asStateFlow()

    fun load(chapterId: String) {
        viewModelScope.launch {
            _state.value = QAUiState(loading = true)
            val qs = contentRepo.qaForChapter(chapterId)
            _state.value = QAUiState(qs, false)
        }
    }

    fun toggleExpand(id: String) {
        val s = _state.value
        _state.value = s.copy(expandedIds = if (id in s.expandedIds) s.expandedIds - id else s.expandedIds + id)
    }

    fun toggleBookmark(question: QuestionEntity) {
        viewModelScope.launch { bookmarkRepo.toggle(question.id, question.subject) }
    }
}
