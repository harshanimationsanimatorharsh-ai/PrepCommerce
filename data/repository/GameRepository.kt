package com.prepcommerce.app.data.repository

import com.prepcommerce.app.data.local.dao.UserDataDao
import com.prepcommerce.app.data.local.entities.GameScoreEntity
import com.prepcommerce.app.data.local.entities.XpLogEntity
import com.prepcommerce.app.gamification.XpEngine

class GameRepository(private val dao: UserDataDao, private val profileRepository: ProfileRepository) {
    suspend fun saveScore(gameType: String, score: Int, difficulty: Int) {
        dao.insertGameScore(GameScoreEntity(gameType = gameType, score = score, difficultyReached = difficulty, timestamp = System.currentTimeMillis()))
        val xp = XpEngine.xpForGame(score)
        dao.insertXpLog(XpLogEntity(source = "GAME:$gameType", xpEarned = xp, timestamp = System.currentTimeMillis()))
        profileRepository.addXp(xp)
        profileRepository.touchStreakAndMaybeUpdateBest()
    }
    fun bestScore(gameType: String) = dao.bestScoreForGame(gameType)
    fun history(gameType: String) = dao.gameHistory(gameType)
}
