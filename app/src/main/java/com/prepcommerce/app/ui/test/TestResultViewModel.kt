package com.prepcommerce.app.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prepcommerce.app.data.local.dao.UserDataDao
import com.prepcommerce.app.data.local.entities.AttemptAnswerEntity
import com.prepcommerce.app.data.local.entities.QuestionEntity
import com.prepcommerce.app.data.local.entities.TestAttemptEntity
import com.prepcommerce.app.data.repository.ContentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AnswerReview(val question: QuestionEntity, val answer: AttemptAnswerEntity)

data class ResultUiState(
    val attempt: TestAttemptEntity? = null,
    val reviews: List<AnswerReview> = emptyList(),
    val loading: Boolean = true
)

class TestResultViewModel(private val userDao: UserDataDao, private val contentRepo: ContentRepository) : ViewModel() {
    private val _state = MutableStateFlow(ResultUiState())
    val state: StateFlow<ResultUiState> = _state.asStateFlow()

    fun load(attemptId: Long) {
        viewModelScope.launch {
            _state.value = ResultUiState(loading = true)
            val attempt = userDao.getAttemptById(attemptId)
            val answers = userDao.getAnswersForAttempt(attemptId)
            val reviews = answers.mapNotNull { ans ->
                contentRepo.question(ans.questionId)?.let { AnswerReview(it, ans) }
            }
            _state.value = ResultUiState(attempt, reviews, false)
        }
    }
}
