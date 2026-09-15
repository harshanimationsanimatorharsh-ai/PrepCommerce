package com.prepcommerce.app.data.local.dao

import androidx.room.*
import com.prepcommerce.app.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    @Query("SELECT COUNT(*) FROM chapters")
    suspend fun chapterCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSamplePapers(papers: List<SamplePaperEntity>)

    @Query("SELECT * FROM chapters WHERE subject=:subject AND classLevel=:classLevel AND (board=:board OR board='BOTH') ORDER BY chapterNo")
    fun getChapters(subject: String, classLevel: Int, board: String): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE id=:id")
    suspend fun getChapter(id: String): ChapterEntity?

    @Query("SELECT * FROM questions WHERE chapterId=:chapterId AND type='MCQ'")
    suspend fun getMcqsByChapter(chapterId: String): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE chapterId=:chapterId AND type!='MCQ'")
    suspend fun getQAByChapter(chapterId: String): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE subject=:subject AND classLevel=:classLevel AND (board=:board OR board='BOTH') AND type='MCQ'")
    suspend fun getMcqsBySubject(subject: String, classLevel: Int, board: String): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE classLevel=:classLevel AND (board=:board OR board='BOTH') AND type='MCQ'")
    suspend fun getAllMcqs(classLevel: Int, board: String): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE isImportant=1 AND subject=:subject AND classLevel=:classLevel")
    suspend fun getImportantQuestions(subject: String, classLevel: Int): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE id=:id")
    suspend fun getQuestion(id: String): QuestionEntity?

    @Query("SELECT * FROM questions WHERE id IN (:ids)")
    suspend fun getQuestionsByIds(ids: List<String>): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE questionText LIKE '%'||:q||'%' LIMIT 30")
    suspend fun searchQuestions(q: String): List<QuestionEntity>

    @Query("SELECT * FROM chapters WHERE name LIKE '%'||:q||'%' LIMIT 30")
    suspend fun searchChapters(q: String): List<ChapterEntity>

    @Query("SELECT * FROM sample_papers WHERE subject=:subject AND classLevel=:classLevel AND (board=:board OR board='BOTH')")
    suspend fun getSamplePapers(subject: String, classLevel: Int, board: String): List<SamplePaperEntity>

    @Query("SELECT * FROM sample_papers WHERE id=:id")
    suspend fun getSamplePaper(id: String): SamplePaperEntity?

    @Query("SELECT COUNT(*) FROM sample_papers")
    suspend fun samplePaperCount(): Int
}
