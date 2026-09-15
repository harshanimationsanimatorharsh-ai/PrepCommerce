package com.prepcommerce.app.data.repository

import com.prepcommerce.app.data.local.dao.ContentDao
import com.prepcommerce.app.data.local.dao.UserDataDao
import com.prepcommerce.app.data.local.entities.*
import com.prepcommerce.app.gamification.XpEngine

class AttemptRepository(
    private val userDao: UserDataDao,
    private val contentDao: ContentDao,
    private val profileRepository: ProfileRepository
) {
    fun recentAttemptsFlow() = userDao.getRecentAttempts(10)
    fun allAttemptsFlow() = userDao.getAllAttempts()

    suspend fun submitTest(
        mode: String, subject: String, chapterId: String?, classLevel: Int, board: String,
        answers: Map<String, Int?>, marked: Set<String>, timeTakenSeconds: Int
    ): TestAttemptEntity {
        var correct = 0; var wrong = 0; var unattempted = 0
        val answerEntities = mutableListOf<AttemptAnswerEntity>()
        for ((qId, sel) in answers) {
            val q = contentDao.getQuestion(qId) ?: continue
            val isCorrect = sel != null && sel == q.correctIndex
            when {
                sel == null -> unattempted++
                isCorrect -> correct++
                else -> wrong++
            }
            answerEntities.add(AttemptAnswerEntity(attemptId = 0, questionId = qId, selectedIndex = sel,
                markedForReview = qId in marked, isCorrect = isCorrect))
        }
        val total = answers.size
        val percent = if (total > 0) correct * 100f / total else 0f
        val accuracy = if (correct + wrong > 0) correct * 100f / (correct + wrong) else 0f
        val attempt = TestAttemptEntity(mode = mode, subject = subject, chapterId = chapterId,
            classLevel = classLevel, board = board, totalQuestions = total, correct = correct,
            wrong = wrong, unattempted = unattempted, scorePercent = percent, accuracy = accuracy,
            timeTakenSeconds = timeTakenSeconds, timestamp = System.currentTimeMillis())
        val attemptId = userDao.insertAttempt(attempt)
        userDao.insertAnswers(answerEntities.map { it.copy(attemptId = attemptId) })

        val xp = XpEngine.xpForTest(correct, total)
        userDao.insertXpLog(XpLogEntity(source = "TEST:$mode", xpEarned = xp, timestamp = System.currentTimeMillis()))
        profileRepository.addXp(xp)
        profileRepository.touchStreakAndMaybeUpdateBest(percent)
        chapterId?.let { updateChapterProgress(it, correct, total) }
        return attempt.copy(id = attemptId)
    }

    suspend fun getAnswers(attemptId: Long) = userDao.getAnswersForAttempt(attemptId)

    private suspend fun updateChapterProgress(chapterId: String, correct: Int, total: Int) {
        val existing = userDao.getChapterProgress(chapterId)
        val attempted = (existing?.questionsAttempted ?: 0) + total
        val correctSum = (existing?.questionsCorrect ?: 0) + correct
        val completion = ((existing?.completionPercent ?: 0f) + 20f).coerceAtMost(100f)
        userDao.upsertChapterProgress(ChapterProgressEntity(chapterId, attempted, correctSum, completion, System.currentTimeMillis()))
    }
}
