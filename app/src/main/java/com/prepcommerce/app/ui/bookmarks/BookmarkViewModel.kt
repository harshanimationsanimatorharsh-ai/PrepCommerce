 package com.prepcommerce.app.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prepcommerce.app.data.local.entities.QuestionEntity
import com.prepcommerce.app.data.repository.BookmarkRepository
import com.prepcommerce.app.data.repository.ContentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BookmarkUiState(val questions: List<QuestionEntity> = emptyList(), val loading: Boolean = true)

class BookmarkViewModel(private val bookmarkRepo: BookmarkRepository, private val contentRepo: ContentRepository) : ViewModel() {
    private val _state = MutableStateFlow(BookmarkUiState())
    val state: StateFlow<BookmarkUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            bookmarkRepo.allBookmarks().collect { bookmarks ->
                _state.value = BookmarkUiState(loading = true)
                val qs = bookmarks.mapNotNull { contentRepo.question(it.questionId) }
                _state.value = BookmarkUiState(qs, false)
            }
        }
    }
    fun remove(questionId: String, subject: String) = viewModelScope.launch { bookmarkRepo.toggle(questionId, subject) }
}
