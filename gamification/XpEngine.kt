package com.prepcommerce.app.gamification

import java.time.LocalDate

object XpEngine {
    const val XP_MCQ_CORRECT = 5
    const val XP_MCQ_WRONG = 1

    fun levelForXp(xp: Int) = (xp / 500) + 1
    fun xpToNextLevel(xp: Int): Int = levelForXp(xp) * 500 - xp

    fun xpForTest(correct: Int, total: Int): Int {
        if (total == 0) return 0
        val base = correct * 8
        val bonus = if (correct.toFloat() / total >= 0.9f) 50 else 0
        return base + bonus
    }

    fun xpForGame(score: Int) = (score / 10).coerceAtLeast(1)

    /** returns Pair(newStreak, today) */
    fun updateStreak(lastActiveDate: String, currentStreak: Int): Pair<Int, String> {
        val today = LocalDate.now().toString()
        if (lastActiveDate == today) return currentStreak to today
        val yesterday = LocalDate.now().minusDays(1).toString()
        val newStreak = if (lastActiveDate == yesterday) currentStreak + 1 else 1
        return newStreak to today
    }
}
