package com.prepcommerce.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Student",
    val board: String = "CBSE",
    val classLevel: Int = 12,
    val xp: Int = 0,
    val level: Int = 1,
    val streak: Int = 0,
    val lastActiveDate: String = "",
    val totalTests: Int = 0,
    val bestScorePercent: Float = 0f
)

@Entity(tableName = "test_attempts")
data class TestAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: String,
    val subject: String,
    val chapterId: String? = null,
    val classLevel: Int,
    val board: String,
    val totalQuestions: Int,
    val correct: Int,
    val wrong: Int,
    val unattempted: Int,
    val scorePercent: Float,
    val accuracy: Float,
    val timeTakenSeconds: Int,
    val timestamp: Long
)

@Entity(tableName = "attempt_answers")
data class AttemptAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val attemptId: Long,
    val questionId: String,
    val selectedIndex: Int?,
    val markedForReview: Boolean,
    val isCorrect: Boolean
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val questionId: String,
    val subject: String,
    val timestamp: Long
)

@Entity(tableName = "game_scores")
data class GameScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameType: String,
    val score: Int,
    val difficultyReached: Int,
    val timestamp: Long
)

@Entity(tableName = "xp_log")
data class XpLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val source: String,
    val xpEarned: Int,
    val timestamp: Long
)

@Entity(tableName = "chapter_progress")
data class ChapterProgressEntity(
    @PrimaryKey val chapterId: String,
    val questionsAttempted: Int = 0,
    val questionsCorrect: Int = 0,
    val completionPercent: Float = 0f,
    val lastAttemptedDate: Long = 0
)

@Entity(tableName = "reported_questions")
data class ReportedQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionId: String,
    val timestamp: Long
)
