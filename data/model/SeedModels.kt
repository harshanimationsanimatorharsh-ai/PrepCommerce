package com.prepcommerce.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChapterSeed(
    val id: String, val board: String, val classLevel: Int, val subject: String,
    val chapterNo: Int, val name: String, val weightageMarks: Int? = null,
    val weightageNote: String? = null, val topics: List<String> = emptyList()
)

@Serializable
data class QuestionSeed(
    val id: String, val board: String, val classLevel: Int, val subject: String,
    val chapterId: String, val topic: String, val questionText: String,
    val options: List<String> = emptyList(), val correctIndex: Int = -1,
    val explanation: String, val difficulty: String = "MEDIUM",
    val type: String = "MCQ", val isImportant: Boolean = false
)

@Serializable
data class SamplePaperSeed(
    val id: String, val board: String, val classLevel: Int, val subject: String,
    val title: String, val durationMinutes: Int, val instructions: String,
    val objectiveQuestionIds: List<String>, val subjectiveQuestionIds: List<String> = emptyList()
)
