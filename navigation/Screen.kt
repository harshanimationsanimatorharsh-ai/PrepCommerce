package com.prepcommerce.app.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Subjects : Screen("subjects")
    object Chapters : Screen("chapters/{subject}") { fun build(s: String) = "chapters/$s" }
    object ChapterDetail : Screen("chapterDetail/{chapterId}") { fun build(id: String) = "chapterDetail/$id" }
    object Mcq : Screen("mcq/{mode}/{subject}/{chapterId}") {
        fun build(mode: String, subject: String, chapterId: String = "none") = "mcq/$mode/$subject/$chapterId"
    }
    object Test : Screen("test/{subject}/{chapterId}") {
        fun build(subject: String, chapterId: String = "none") = "test/$subject/$chapterId"
    }
    object Result : Screen("result/{attemptId}") { fun build(id: Long) = "result/$id" }
    object Solution : Screen("solution/{attemptId}") { fun build(id: Long) = "solution/$id" }
    object QA : Screen("qa/{chapterId}") { fun build(id: String) = "qa/$id" }
    object SamplePapers : Screen("samplePapers/{subject}")
    object SamplePaperAttempt : Screen("samplePaperAttempt/{paperId}") { fun build(id: String) = "samplePaperAttempt/$id" }
    object GamesHub : Screen("games")
    object MathChallenge : Screen("game/math")
    object EquationGame : Screen("game/equation")
    object MindSharp : Screen("game/mindsharp")
    object PuzzleGame : Screen("game/puzzle")
    object SpeedChallenge : Screen("game/speed")
    object DailyChallenge : Screen("game/daily")
    object Profile : Screen("profile")
    object Bookmarks : Screen("bookmarks")
    object Search : Screen("search")
    object Progress : Screen("progress")
}
