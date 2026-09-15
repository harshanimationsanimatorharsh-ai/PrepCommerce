package com.prepcommerce.app.data.repository

import com.prepcommerce.app.data.local.dao.UserDataDao
import com.prepcommerce.app.data.local.entities.UserProfileEntity
import com.prepcommerce.app.gamification.XpEngine

class ProfileRepository(private val dao: UserDataDao) {
    fun profileFlow() = dao.getProfileFlow()

    suspend fun ensureProfile() {
        if (dao.getProfileOnce() == null) dao.insertProfile(UserProfileEntity())
    }

    suspend fun addXp(xp: Int) {
        val p = dao.getProfileOnce() ?: UserProfileEntity()
        val newXp = p.xp + xp
        dao.updateProfile(p.copy(xp = newXp, level = XpEngine.levelForXp(newXp)))
    }

    suspend fun touchStreakAndMaybeUpdateBest(scorePercent: Float? = null) {
        val p = dao.getProfileOnce() ?: UserProfileEntity()
        val (newStreak, today) = XpEngine.updateStreak(p.lastActiveDate, p.streak)
        val best = if (scorePercent != null) maxOf(p.bestScorePercent, scorePercent) else p.bestScorePercent
        val totalTests = if (scorePercent != null) p.totalTests + 1 else p.totalTests
        dao.updateProfile(p.copy(streak = newStreak, lastActiveDate = today, bestScorePercent = best, totalTests = totalTests))
    }

    suspend fun setBoardClass(board: String, classLevel: Int) {
        val p = dao.getProfileOnce() ?: UserProfileEntity()
        dao.updateProfile(p.copy(board = board, classLevel = classLevel))
    }

    suspend fun setName(name: String) {
        val p = dao.getProfileOnce() ?: UserProfileEntity()
        dao.updateProfile(p.copy(name = name))
    }
}
