package com.prepcommerce.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prepcommerce.app.data.local.entities.ChapterEntity
import com.prepcommerce.app.data.local.entities.QuestionEntity
import com.prepcommerce.app.data.repository.ContentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchUiState(val query: String = "", val chapters: List<ChapterEntity> = emptyList(), val questions: List<QuestionEntity> = emptyList())

class SearchViewModel(private val contentRepo: ContentRepository) : ViewModel() {
    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    fun search(query: String) {
        _state.value = _state.value.copy(query = query)
        if (query.length < 2) { _state.value = _state.value.copy(chapters = emptyList(), questions = emptyList()); return }
        viewModelScope.launch {
            val chapters = contentRepo.searchChapters(query)
            val questions = contentRepo.searchQuestions(query)
            _state.value = _state.value.copy(chapters = chapters, questions = questions)
        }
    }
}
