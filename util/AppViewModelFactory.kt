package com.prepcommerce.app.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prepcommerce.app.PrepApp
import com.prepcommerce.app.data.repository.*
import com.prepcommerce.app.ui.home.HomeViewModel
import com.prepcommerce.app.ui.subject.ChapterViewModel
import com.prepcommerce.app.ui.mcq.McqViewModel
import com.prepcommerce.app.ui.test.TestViewModel
import com.prepcommerce.app.ui.profile.ProfileViewModel
import com.prepcommerce.app.ui.bookmarks.BookmarkViewModel
import com.prepcommerce.app.ui.search.SearchViewModel
import com.prepcommerce.app.ui.progress.ProgressViewModel
import com.prepcommerce.app.ui.samplepaper.SamplePaperViewModel
import com.prepcommerce.app.ui.games.*

class AppViewModelFactory(private val app: PrepApp) : ViewModelProvider.Factory {
    private val db = app.database
    private val contentRepo = ContentRepository(db.contentDao())
    private val userDao = db.userDataDao()
    val profileRepo = ProfileRepository(userDao)
    private val attemptRepo = AttemptRepository(userDao, db.contentDao(), profileRepo)
    private val bookmarkRepo = BookmarkRepository(userDao)
    private val gameRepo = GameRepository(userDao, profileRepo)
    private val paperRepo = SamplePaperRepository(contentRepo)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        HomeViewModel::class.java -> HomeViewModel(profileRepo, attemptRepo) as T
        ChapterViewModel::class.java -> ChapterViewModel(contentRepo, userDao) as T
        McqViewModel::class.java -> McqViewModel(contentRepo, bookmarkRepo, profileRepo) as T
        TestViewModel::class.java -> TestViewModel(contentRepo, attemptRepo) as T
        ProfileViewModel::class.java -> ProfileViewModel(profileRepo) as T
        BookmarkViewModel::class.java -> BookmarkViewModel(bookmarkRepo, contentRepo) as T
        SearchViewModel::class.java -> SearchViewModel(contentRepo) as T
        ProgressViewModel::class.java -> ProgressViewModel(userDao, contentRepo) as T
        SamplePaperViewModel::class.java -> SamplePaperViewModel(paperRepo, contentRepo, attemptRepo) as T
        MathChallengeViewModel::class.java -> MathChallengeViewModel(gameRepo) as T
        EquationGameViewModel::class.java -> EquationGameViewModel(gameRepo) as T
        MindSharpViewModel::class.java -> MindSharpViewModel(gameRepo) as T
        PuzzleGameViewModel::class.java -> PuzzleGameViewModel(gameRepo) as T
        SpeedChallengeViewModel::class.java -> SpeedChallengeViewModel(gameRepo, contentRepo) as T
        DailyChallengeViewModel::class.java -> DailyChallengeViewModel(contentRepo, attemptRepo) as T
        else -> throw IllegalArgumentException("Unknown ViewModel: $modelClass")
    }
}
