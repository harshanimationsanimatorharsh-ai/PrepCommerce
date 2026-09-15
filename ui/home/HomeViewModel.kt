package com.prepcommerce.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prepcommerce.app.data.local.entities.TestAttemptEntity
import com.prepcommerce.app.data.local.entities.UserProfileEntity
import com.prepcommerce.app.data.repository.AttemptRepository
import com.prepcommerce.app.data.repository.ProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

val SUBJECTS = listOf("Accountancy", "Economics", "Business Studies", "Hindi", "English", "Mathematics")

data class HomeUiState(val profile: UserProfileEntity = UserProfileEntity(), val recentAttempts: List<TestAttemptEntity> = emptyList())

class HomeViewModel(private val profileRepo: ProfileRepository, private val attemptRepo: AttemptRepository) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = combine(profileRepo.profileFlow(), attemptRepo.recentAttemptsFlow()) { p, a ->
        HomeUiState(p ?: UserProfileEntity(), a)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        viewModelScope.launch {
            profileRepo.ensureProfile()
            profileRepo.touchStreakAndMaybeUpdateBest()
        }
    }
    fun setBoardClass(board: String, classLevel: Int) = viewModelScope.launch { profileRepo.setBoardClass(board, classLevel) }
}
