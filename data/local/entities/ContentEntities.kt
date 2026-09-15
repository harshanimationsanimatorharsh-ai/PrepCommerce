package com.prepcommerce.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey val id: String,
    val board: String,        // CBSE, MP, BOTH
    val classLevel: Int,      // 11 / 12
    val subject: String,
    val chapterNo: Int,
    val name: String,
    val weightageMarks: Int? = null,
    val weightageNote: String? = null,
    val topics: List<String> = emptyList()
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val board: String,
    val classLevel: Int,
    val subject: String,
    val chapterId: String,
    val topic: String,
    val questionText: String,
    val options: List<String>,      // empty for LONG/SHORT answer types
    val correctIndex: Int,          // -1 for non-MCQ
    val explanation: String,        // model answer for non-MCQ types
    val difficulty: String,         // EASY, MEDIUM, HARD
    val type: String,               // MCQ, SHORT_ANSWER, LONG_ANSWER, NUMERICAL, CONCEPTUAL
    val isImportant: Boolean = false
)

@Entity(tableName = "sample_papers")
data class SamplePaperEntity(
    @PrimaryKey val id: String,
    val board: String,
    val classLevel: Int,
    val subject: String,
    val title: String,
    val durationMinutes: Int,
    val instructions: String,
    val objectiveQuestionIds: List<String>,
    val subjectiveQuestionIds: List<String>
)
