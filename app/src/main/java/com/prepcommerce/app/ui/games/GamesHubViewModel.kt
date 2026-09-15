package com.prepcommerce.app.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prepcommerce.app.data.repository.GameRepository
import kotlinx.coroutines.flow.*

data class GamesHubState(val bestScores: Map<String, Int> = emptyMap())

class GamesHubViewModel(private val gameRepo: GameRepository) : ViewModel() {
    val gameTypes = listOf("MATH_CHALLENGE", "EQUATION_GAME", "MIND_SHARP", "PUZZLE_GAME", "SPEED_CHALLENGE")
    val state: StateFlow<GamesHubState> = combine(gameTypes.map { type -> gameRepo.bestScore(type).map { type to (it ?: 0) } }) { pairs ->
        GamesHubState(pairs.toMap())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GamesHubState())
}
