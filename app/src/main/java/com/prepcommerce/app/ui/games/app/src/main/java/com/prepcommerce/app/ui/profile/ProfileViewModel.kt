package com.prepcommerce.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prepcommerce.app.data.local.entities.TestAttemptEntity
import com.prepcommerce.app.data.local.entities.UserProfileEntity
import com.prepcommerce.app.data.repository.AttemptRepository
import com.prepcommerce.app.data.repository.ProfileRepository
import com.prepcommerce.app.gamification.XpEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(val profile: UserProfileEntity = UserProfileEntity(), val attempts: List<TestAttemptEntity> = emptyList())

class ProfileViewModel(private val profileRepo: ProfileRepository, private val attemptRepo: AttemptRepository) : ViewModel() {
    val state: StateFlow<ProfileUiState> = combine(profileRepo.profileFlow(), attemptRepo.allAttemptsFlow()) { p, a ->
        ProfileUiState(p ?: UserProfileEntity(), a)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState())

    fun xpToNextLevel(xp: Int) = XpEngine.xpToNextLevel(xp)
    fun updateName(name: String) = viewModelScope.launch { profileRepo.setName(name) }
    fun updateBoardClass(board: String, classLevel: Int) = viewModelScope.launch { profileRepo.setBoardClass(board, classLevel) }
}0
