package com.prepcommerce.app.data.local.seed

import android.content.Context
import com.prepcommerce.app.data.local.AppDatabase
import com.prepcommerce.app.data.local.entities.*
import com.prepcommerce.app.data.model.ChapterSeed
import com.prepcommerce.app.data.model.QuestionSeed
import com.prepcommerce.app.data.model.SamplePaperSeed
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

object ContentSeeder {
    suspend fun seedIfNeeded(context: Context, db: AppDatabase) {
        if (db.contentDao().chapterCount() > 0) return
        val json = Json { ignoreUnknownKeys = true }

        val chaptersJson = context.assets.open("content/chapters.json").bufferedReader().use { it.readText() }
        val questionsJson = context.assets.open("content/questions.json").bufferedReader().use { it.readText() }
        val papersJson = context.assets.open("content/sample_papers.json").bufferedReader().use { it.readText() }

        val chapters = json.decodeFromString<List<ChapterSeed>>(chaptersJson).map {
            ChapterEntity(it.id, it.board, it.classLevel, it.subject, it.chapterNo, it.name, it.weightageMarks, it.weightageNote, it.topics)
        }
        val questions = json.decodeFromString<List<QuestionSeed>>(questionsJson).map {
            QuestionEntity(it.id, it.board, it.classLevel, it.subject, it.chapterId, it.topic, it.questionText,
                it.options, it.correctIndex, it.explanation, it.difficulty, it.type, it.isImportant)
        }
        val papers = json.decodeFromString<List<SamplePaperSeed>>(papersJson).map {
            SamplePaperEntity(it.id, it.board, it.classLevel, it.subject, it.title, it.durationMinutes,
                it.instructions, it.objectiveQuestionIds, it.subjectiveQuestionIds)
        }

        db.contentDao().insertChapters(chapters)
        db.contentDao().insertQuestions(questions)
        db.contentDao().insertSamplePapers(papers)
    }
}
