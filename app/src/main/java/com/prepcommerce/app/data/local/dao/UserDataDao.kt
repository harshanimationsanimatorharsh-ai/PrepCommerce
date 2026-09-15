package com.prepcommerce.app.data.local.dao

import androidx.room.*
import com.prepcommerce.app.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {
    @Query("SELECT * FROM user_profile WHERE id=1")
    fun getProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id=1")
    suspend fun getProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(p: UserProfileEntity)

    @Update
    suspend fun updateProfile(p: UserProfileEntity)

    @Insert
    suspend fun insertAttempt(a: TestAttemptEntity): Long

    @Insert
    suspend fun insertAnswers(list: List<AttemptAnswerEntity>)

    @Query("SELECT * FROM attempt_answers WHERE attemptId=:attemptId")
    suspend fun getAnswersForAttempt(attemptId: Long): List<AttemptAnswerEntity>

    @Query("SELECT * FROM test_attempts WHERE id=:id")
    suspend fun getAttemptById(id: Long): TestAttemptEntity?

    @Query("SELECT * FROM test_attempts WHERE mode='DAILY' ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastDailyAttempt(): TestAttemptEntity?

    @Query("SELECT * FROM test_attempts ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentAttempts(limit: Int = 10): Flow<List<TestAttemptEntity>>

    @Query("SELECT * FROM test_attempts ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<TestAttemptEntity>>

    @Query("SELECT * FROM test_attempts WHERE subject=:subject ORDER BY timestamp DESC")
    suspend fun getAttemptsForSubject(subject: String): List<TestAttemptEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(b: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE questionId=:qId")
    suspend fun deleteBookmark(qId: String)

    @Query("SELECT * FROM bookmarks WHERE questionId=:qId")
    suspend fun getBookmark(qId: String): BookmarkEntity?

    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert
    suspend fun insertGameScore(g: GameScoreEntity)

    @Query("SELECT MAX(score) FROM game_scores WHERE gameType=:type")
    fun bestScoreForGame(type: String): Flow<Int?>

    @Query("SELECT * FROM game_scores WHERE gameType=:type ORDER BY timestamp DESC LIMIT 20")
    fun gameHistory(type: String): Flow<List<GameScoreEntity>>

    @Insert
    suspend fun insertXpLog(x: XpLogEntity)

    @Query("SELECT * FROM chapter_progress WHERE chapterId=:id")
    suspend fun getChapterProgress(id: String): ChapterProgressEntity?

    @Query("SELECT * FROM chapter_progress")
    fun getAllChapterProgress(): Flow<List<ChapterProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChapterProgress(p: ChapterProgressEntity)

    @Insert
    suspend fun insertReport(r: ReportedQuestionEntity)
}
