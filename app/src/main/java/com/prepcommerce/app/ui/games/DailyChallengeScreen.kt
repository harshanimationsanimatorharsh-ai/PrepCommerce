package com.prepcommerce.app.ui.games

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.prepcommerce.app.data.local.entities.QuestionEntity
import com.prepcommerce.app.data.repository.AttemptRepository
import com.prepcommerce.app.data.repository.ContentRepository
import com.prepcommerce.app.data.repository.ProfileRepository
import com.prepcommerce.app.ui.components.EmptyState
import com.prepcommerce.app.ui.components.LoadingState
import com.prepcommerce.app.ui.components.OptionRow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DailyState(
    val questions: List<QuestionEntity> = emptyList(), val index: Int = 0, val answers: MutableMap<String, Int> = mutableMapOf(),
    val alreadyDoneToday: Boolean = false, val previousScore: Float = 0f, val submitted: Boolean = false, val loading: Boolean = true
)

class DailyChallengeViewModel(
    private val contentRepo: ContentRepository, private val attemptRepo: AttemptRepository, private val profileRepo: ProfileRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DailyState())
    val state: StateFlow<DailyState> = _state.asStateFlow()

    fun load(userDao: com.prepcommerce.app.data.local.dao.UserDataDao) {
        viewModelScope.launch {
            _state.value = DailyState(loading = true)
            val last = userDao.getLastDailyAttempt()
            val today = LocalDate.now().toEpochDay()
            val lastDay = last?.let {
                java.time.Instant.ofEpochMilli(it.timestamp).atZone(java.time.ZoneId.systemDefault()).toLocalDate().toEpochDay()
            }
            if (lastDay != null && lastDay == today) {
                _state.value = DailyState(alreadyDoneToday = true, previousScore = last!!.scorePercent, loading = false)
                return@launch
            }
            val profile = profileRepo.profileFlow().first()
            val pool = contentRepo.allMcqs(profile?.classLevel ?: 12, profile?.board ?: "CBSE")
            if (pool.isEmpty()) { _state.value = DailyState(loading = false); return@launch }
            val seeded = kotlin.random.Random(today)
            val questions = pool.shuffled(seeded).take(10)
            _state.value = DailyState(questions = questions, loading = false)
        }
    }

    fun selectAnswer(i: Int) {
        val s = _state.value
        val q = s.questions.getOrNull(s.index) ?: return
        s.answers[q.id] = i
        _state.value = s.copy()
    }
    fun next() {
        val s = _state.value
        if (s.index < s.questions.size - 1) _state.value = s.copy(index = s.index + 1)
        else submit()
    }
    private fun submit() {
        viewModelScope.launch {
            val s = _state.value
            val profile = profileRepo.profileFlow().first()
            val fullAnswers: Map<String, Int?> = s.questions.associate { it.id to s.answers[it.id] }
            attemptRepo.submitTest("DAILY", "Mixed", null, profile?.classLevel ?: 12, profile?.board ?: "CBSE", fullAnswers, emptySet(), 0)
            _state.value = s.copy(submitted = true)
        }
    }
}

@Composable
fun DailyChallengeScreen(viewModel: DailyChallengeViewModel, navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val app = context.applicationContext as com.prepcommerce.app.PrepApp
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.load(app.database.userDataDao()) }

    when {
        state.loading -> LoadingState()
        state.alreadyDoneToday -> Column(Modifier.fillMaxSize().padding(20.dp)) {
            Text("You've already completed today's challenge!", style = MaterialTheme.typography.titleMedium)
            Text("Score: ${state.previousScore.toInt()}%")
            Spacer(Modifier.height(16.dp))
            Text("Come back tomorrow for a new challenge. 🔥")
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = { navController.popBackStack() }) { Text("Back") }
        }
        state.submitted -> Column(Modifier.fillMaxSize().padding(20.dp)) {
            Text("Daily Challenge Completed! 🎉", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = { navController.popBackStack() }) { Text("Back") }
        }
        state.questions.isEmpty() -> EmptyState("Not enough questions for today's challenge yet.")
        else -> {
            val q = state.questions[state.index]
            Column(Modifier.fillMaxSize().padding(20.dp)) {
                Text("Daily Challenge — Q${state.index + 1}/${state.questions.size}", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Text(q.questionText, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                q.options.forEachIndexed { i, opt -> OptionRow(opt, i, false, false, false) { viewModel.selectAnswer(i) } }
                Spacer(Modifier.height(16.dp))
                Button(onClick = { viewModel.next() }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (state.index == state.questions.size - 1) "Finish" else "Next")
                }
            }
        }
    }
}
