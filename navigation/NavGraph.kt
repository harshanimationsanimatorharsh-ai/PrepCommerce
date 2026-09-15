package com.prepcommerce.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.prepcommerce.app.util.AppViewModelFactory
import com.prepcommerce.app.ui.home.HomeScreen
import com.prepcommerce.app.ui.subject.SubjectChaptersScreen
import com.prepcommerce.app.ui.chapter.ChapterDetailScreen
import com.prepcommerce.app.ui.mcq.McqPracticeScreen
import com.prepcommerce.app.ui.test.TestScreen
import com.prepcommerce.app.ui.test.TestResultScreen
import com.prepcommerce.app.ui.test.SolutionReviewScreen
import com.prepcommerce.app.ui.qa.QAScreen
import com.prepcommerce.app.ui.games.*
import com.prepcommerce.app.ui.profile.ProfileScreen
import com.prepcommerce.app.ui.bookmarks.BookmarksScreen
import com.prepcommerce.app.ui.search.SearchScreen
import com.prepcommerce.app.ui.progress.ProgressScreen

data class BottomItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
val bottomItems = listOf(
    BottomItem(Screen.Home.route, "Home", Icons.Filled.Home),
    BottomItem(Screen.Subjects.route, "Practice", Icons.Filled.MenuBook),
    BottomItem("testHub", "Tests", Icons.Filled.Assignment),
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
                        if (item.route == "testHub") navController.navigate(Screen.Subjects.route)
                        else navController.navigate(item.route) {
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
                HomeScreen(viewModel(factory = factory), navController)
            }
            composable(Screen.Subjects.route) {
                com.prepcommerce.app.ui.subject.SubjectListScreen(navController)
            }
            composable(Screen.Chapters.route, arguments = listOf(navArgument("subject") { type = NavType.StringType })) { backStackEntry ->
                val subject = backStackEntry.arguments?.getString("subject") ?: ""
                SubjectChaptersScreen(subject, viewModel(factory = factory), navController)
            }
            composable(Screen.ChapterDetail.route, arguments = listOf(navArgument("chapterId") { type = NavType.StringType })) { backStackEntry ->
                val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
                ChapterDetailScreen(chapterId, viewModel(factory = factory), navController)
            }
            composable(Screen.Mcq.route, arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("subject") { type = NavType.StringType },
                navArgument("chapterId") { type = NavType.StringType }
            )) { backStackEntry ->
                val a = backStackEntry.arguments!!
                McqPracticeScreen(a.getString("mode")!!, a.getString("subject")!!, a.getString("chapterId")!!,
                    viewModel(factory = factory), navController)
            }
            composable(Screen.Test.route, arguments = listOf(
                navArgument("subject") { type = NavType.StringType },
                navArgument("chapterId") { type = NavType.StringType }
            )) { backStackEntry ->
                val a = backStackEntry.arguments!!
                TestScreen(a.getString("subject")!!, a.getString("chapterId")!!, viewModel(factory = factory), navController)
            }
            composable(Screen.Result.route, arguments = listOf(navArgument("attemptId") { type = NavType.LongType })) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("attemptId") ?: 0L
                TestResultScreen(id, navController)
            }
            composable(Screen.Solution.route, arguments = listOf(navArgument("attemptId") { type = NavType.LongType })) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("attemptId") ?: 0L
                SolutionReviewScreen(id, viewModel(factory = factory), navController)
            }
            composable(Screen.QA.route, arguments = listOf(navArgument("chapterId") { type = NavType.StringType })) { backStackEntry ->
                QAScreen(backStackEntry.arguments?.getString("chapterId") ?: "", viewModel(factory = factory), navController)
            }
            composable(Screen.GamesHub.route) { GamesHubScreen(navController) }
            composable(Screen.MathChallenge.route) { MathChallengeScreen(viewModel(factory = factory)) }
            composable(Screen.EquationGame.route) { EquationGameScreen(viewModel(factory = factory)) }
            composable(Screen.MindSharp.route) { MindSharpScreen(viewModel(factory = factory)) }
            composable(Screen.PuzzleGame.route) { PuzzleGameScreen(viewModel(factory = factory)) }
            composable(Screen.SpeedChallenge.route) { SpeedChallengeScreen(viewModel(factory = factory)) }
            composable(Screen.DailyChallenge.route) { DailyChallengeScreen(viewModel(factory = factory)) }
            composable(Screen.Profile.route) { ProfileScreen(viewModel(factory = factory)) }
            composable(Screen.Bookmarks.route) { BookmarksScreen(viewModel(factory = factory), navController) }
            composable(Screen.Search.route) { SearchScreen(viewModel(factory = factory), navController) }
            composable(Screen.Progress.route) { ProgressScreen(viewModel(factory = factory)) }
        }
    }
}
