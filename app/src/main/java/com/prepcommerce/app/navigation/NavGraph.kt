package com.prepcommerce.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.prepcommerce.app.util.AppViewModelFactory
import com.prepcommerce.app.ui.home.HomeScreen
import com.prepcommerce.app.ui.home.HomeViewModel
import com.prepcommerce.app.ui.subject.SubjectListScreen
import com.prepcommerce.app.ui.subject.SubjectChaptersScreen
import com.prepcommerce.app.ui.subject.ChapterViewModel
import com.prepcommerce.app.ui.chapter.ChapterDetailScreen
import com.prepcommerce.app.ui.mcq.McqPracticeScreen
import com.prepcommerce.app.ui.mcq.McqViewModel
import com.prepcommerce.app.ui.test.TestScreen
import com.prepcommerce.app.ui.test.TestViewModel
import com.prepcommerce.app.ui.test.TestResultScreen
import com.prepcommerce.app.ui.test.SolutionReviewScreen
import com.prepcommerce.app.ui.test.TestResultViewModel
import com.prepcommerce.app.ui.qa.QAScreen
import com.prepcommerce.app.ui.qa.QAViewModel
import com.prepcommerce.app.ui.samplepaper.SamplePaperListScreen
import com.prepcommerce.app.ui.samplepaper.SamplePaperAttemptScreen
import com.prepcommerce.app.ui.samplepaper.SamplePaperViewModel
import com.prepcommerce.app.ui.games.*
import com.prepcommerce.app.ui.profile.ProfileScreen
import com.prepcommerce.app.ui.profile.ProfileViewModel
import com.prepcommerce.app.ui.bookmarks.BookmarksScreen
import com.prepcommerce.app.ui.bookmarks.BookmarkViewModel
import com.prepcommerce.app.ui.search.SearchScreen
import com.prepcommerce.app.ui.search.SearchViewModel
import com.prepcommerce.app.ui.progress.ProgressScreen
import com.prepcommerce.app.ui.progress.ProgressViewModel

data class BottomItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
val bottomItems = listOf(
    BottomItem(Screen.Home.route, "Home", Icons.Filled.Home),
    BottomItem(Screen.Subjects.route, "Practice", Icons.Filled.MenuBook),
    BottomItem(Screen.GamesHub.route, "Games", Icons.Filled.SportsEsports),
    BottomItem(Screen.Profile.route, "Profile", Icons.Filled.Person)
)

@Composable
fun AppNavGraph(factory: AppViewModelFactory) {
    val navController = rememberNavController()
    Scaffold(bottomBar = {
        val backStack by navController.currentBackStackEntryAsState()
        val current = backStack?.destination?.route
        NavigationBar {
            bottomItems.forEach { item ->
                NavigationBarItem(
                    selected = current == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) }
                )
            }
        }
    }) { padding ->
        NavHost(navController, startDestination = Screen.Home.route, modifier = Modifier.padding(padding)) {
            composable(Screen.Home.route) {
                val vm: HomeViewModel = viewModel(factory = factory)
                HomeScreen(vm, navController)
            }
            composable(Screen.Subjects.route) {
                SubjectListScreen(navController)
            }
            composable(Screen.Chapters.route, arguments = listOf(navArgument("subject") { type = NavType.StringType })) { backStackEntry ->
                val subject = backStackEntry.arguments?.getString("subject") ?: ""
                val vm: ChapterViewModel = viewModel(factory = factory)
                SubjectChaptersScreen(subject, vm, navController)
            }
            composable(Screen.ChapterDetail.route, arguments = listOf(navArgument("chapterId") { type = NavType.StringType })) { backStackEntry ->
                val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
                val vm: ChapterViewModel = viewModel(factory = factory)
                ChapterDetailScreen(chapterId, vm, navController)
            }
            composable(Screen.Mcq.route, arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("subject") { type = NavType.StringType },
                navArgument("chapterId") { type = NavType.StringType }
            )) { backStackEntry ->
                val a = backStackEntry.arguments!!
                val vm: McqViewModel = viewModel(factory = factory)
                McqPracticeScreen(a.getString("mode")!!, a.getString("subject")!!, a.getString("chapterId")!!, vm, navController)
            }
            composable(Screen.Test.route, arguments = listOf(
                navArgument("subject") { type = NavType.StringType },
                navArgument("chapterId") { type = NavType.StringType }
            )) { backStackEntry ->
                val a = backStackEntry.arguments!!
                val vm: TestViewModel = viewModel(factory = factory)
                TestScreen(a.getString("subject")!!, a.getString("chapterId")!!, vm, navController)
            }
            composable(Screen.Result.route, arguments = listOf(navArgument("attemptId") { type = NavType.LongType })) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("attemptId") ?: 0L
                val vm: TestResultViewModel = viewModel(factory = factory)
                TestResultScreen(id, vm, navController)
            }
            composable(Screen.Solution.route, arguments = listOf(navArgument("attemptId") { type = NavType.LongType })) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("attemptId") ?: 0L
                val vm: TestResultViewModel = viewModel(factory = factory)
                SolutionReviewScreen(id, vm, navController)
            }
            composable(Screen.QA.route, arguments = listOf(navArgument("chapterId") { type = NavType.StringType })) { backStackEntry ->
                val vm: QAViewModel = viewModel(factory = factory)
                QAScreen(backStackEntry.arguments?.getString("chapterId") ?: "", vm, navController)
            }
            composable(Screen.SamplePapers.route, arguments = listOf(navArgument("subject") { type = NavType.StringType })) { backStackEntry ->
                val subject = backStackEntry.arguments?.getString("subject") ?: ""
                val vm: SamplePaperViewModel = viewModel(factory = factory)
                SamplePaperListScreen(subject, vm, navController)
            }
            composable(Screen.SamplePaperAttempt.route, arguments = listOf(navArgument("paperId") { type = NavType.StringType })) { backStackEntry ->
                val paperId = backStackEntry.arguments?.getString("paperId") ?: ""
                val vm: SamplePaperViewModel = viewModel(factory = factory)
                SamplePaperAttemptScreen(paperId, vm, navController)
            }
            composable(Screen.GamesHub.route) {
                val vm: GamesHubViewModel = viewModel(factory = factory)
                GamesHubScreen(vm, navController)
            }
            composable(Screen.MathChallenge.route) { MathChallengeScreen(viewModel(factory = factory), navController) }
            composable(Screen.EquationGame.route) { EquationGameScreen(viewModel(factory = factory), navController) }
            composable(Screen.MindSharp.route) { MindSharpScreen(viewModel(factory = factory), navController) }
            composable(Screen.PuzzleGame.route) { PuzzleGameScreen(viewModel(factory = factory), navController) }
            composable(Screen.SpeedChallenge.route) { SpeedChallengeScreen(viewModel(factory = factory), navController) }
            composable(Screen.DailyChallenge.route) { DailyChallengeScreen(viewModel(factory = factory), navController) }
            composable(Screen.Profile.route) {
                val vm: ProfileViewModel = viewModel(factory = factory)
                ProfileScreen(vm, navController)
            }
            composable(Screen.Bookmarks.route) {
                val vm: BookmarkViewModel = viewModel(factory = factory)
                BookmarksScreen(vm, navController)
            }
            composable(Screen.Search.route) {
                val vm: SearchViewModel = viewModel(factory = factory)
                SearchScreen(vm, navController)
            }
            composable(Screen.Progress.route) {
                val vm: ProgressViewModel = viewModel(factory = factory)
                ProgressScreen(vm)
            }
        }
    }
}
