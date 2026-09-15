package com.prepcommerce.app.data.repository

import com.prepcommerce.app.data.local.dao.ContentDao
import com.prepcommerce.app.data.local.entities.ChapterEntity
import com.prepcommerce.app.data.local.entities.QuestionEntity
import com.prepcommerce.app.data.local.entities.SamplePaperEntity
import kotlinx.coroutines.flow.Flow

class ContentRepository(private val dao: ContentDao) {
    fun chapters(subject: String, classLevel: Int, board: String): Flow<List<ChapterEntity>> =
        dao.getChapters(subject, classLevel, board)

    suspend fun chapter(id: String): ChapterEntity? = dao.getChapter(id)
    suspend fun mcqsForChapter(chapterId: String): List<QuestionEntity> = dao.getMcqsByChapter(chapterId)
    suspend fun qaForChapter(chapterId: String): List<QuestionEntity> = dao.getQAByChapter(chapterId)
    suspend fun mcqsForSubject(subject: String, classLevel: Int, board: String): List<QuestionEntity> =
        dao.getMcqsBySubject(subject, classLevel, board)
    suspend fun importantQuestions(subject: String, classLevel: Int): List<QuestionEntity> =
        dao.getImportantQuestions(subject, classLevel)
    suspend fun question(id: String): QuestionEntity? = dao.getQuestion(id)
    suspend fun questionsByIds(ids: List<String>): List<QuestionEntity> = dao.getQuestionsByIds(ids)
    suspend fun searchQuestions(q: String) = dao.searchQuestions(q)
    suspend fun searchChapters(q: String) = dao.searchChapters(q)
    suspend fun samplePapers(subject: String, classLevel: Int, board: String): List<SamplePaperEntity> =
        dao.getSamplePapers(subject, classLevel, board)
    suspend fun samplePaper(id: String): SamplePaperEntity? = dao.getSamplePaper(id)
}
