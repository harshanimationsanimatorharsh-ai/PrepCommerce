package com.prepcommerce.app.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prepcommerce.app.data.local.dao.UserDataDao
import com.prepcommerce.app.data.local.entities.TestAttemptEntity
import com.prepcommerce.app.data.repository.ContentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SubjectProgress(val subject: String, val avgAccuracy: Float, val testsCount: Int)

data class ProgressUiState(
    val recentAttempts: List<TestAttemptEntity> = emptyList(),
    val subjectProgress: List<SubjectProgress> = emptyList(),
    val overallAccuracy: Float = 0f
)

class ProgressViewModel(private val userDao: UserDataDao, private val contentRepo: ContentRepository) : ViewModel() {
    private val _state = MutableStateFlow(ProgressUiState())
    val state: StateFlow<ProgressUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userDao.getAllAttempts().collect { attempts ->
                val bySubject = attempts.groupBy { it.subject }.map { (subject, list) ->
                    SubjectProgress(subject, list.map { it.accuracy }.average().toFloat(), list.size)
                }
                val overall = if (attempts.isNotEmpty()) attempts.map { it.accuracy }.average().toFloat() else 0f
                _state.value = ProgressUiState(attempts.take(10), bySubject, overall)
            }
        }
    }
}
