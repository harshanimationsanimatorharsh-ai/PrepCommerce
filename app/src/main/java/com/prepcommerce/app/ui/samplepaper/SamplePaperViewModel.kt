package com.prepcommerce.app.ui.samplepaper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prepcommerce.app.data.local.entities.QuestionEntity
import com.prepcommerce.app.data.local.entities.SamplePaperEntity
import com.prepcommerce.app.data.repository.AttemptRepository
import com.prepcommerce.app.data.repository.ContentRepository
import com.prepcommerce.app.data.repository.ProfileRepository
import com.prepcommerce.app.data.repository.SamplePaperRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class SamplePaperListState(val papers: List<SamplePaperEntity> = emptyList(), val loading: Boolean = true)

data class SamplePaperAttemptState(
    val paper: SamplePaperEntity? = null,
    val objectiveQuestions: List<QuestionEntity> = emptyList(),
    val subjectiveQuestions: List<QuestionEntity> = emptyList(),
    val answers: Map<String, Int?> = emptyMap(),
    val revealedSubjective: Set<String> = emptySet(),
    val remainingSeconds: Int = 0,
    val started: Boolean = false,
    val submitted: Boolean = false,
    val correct: Int = 0,
    val loading: Boolean = true
)

class SamplePaperViewModel(
    private val paperRepo: SamplePaperRepository,
    private val contentRepo: ContentRepository,
    private val attemptRepo: AttemptRepository,
    private val profileRepo: ProfileRepository
) : ViewModel() {
    private val _listState = MutableStateFlow(SamplePaperListState())
    val listState: StateFlow<SamplePaperListState> = _listState.asStateFlow()

    private val _attemptState = MutableStateFlow(SamplePaperAttemptState())
    val attemptState: StateFlow<SamplePaperAttemptState> = _attemptState.asStateFlow()
    private var timerJob: Job? = null
    private var startTime = 0L

    fun loadPapers(subject: String) {
        viewModelScope.launch {
            _listState.value = SamplePaperListState(loading = true)
            val profile = profileRepo.profileFlow().first()
            val papers = paperRepo.papersFor(subject, profile?.classLevel ?: 12, profile?.board ?: "CBSE")
            _listState.value = SamplePaperListState(papers, false)
        }
    }

    fun loadPaper(paperId: String) {
        viewModelScope.launch {
            _attemptState.value = SamplePaperAttemptState(loading = true)
            val paper = paperRepo.paper(paperId) ?: return@launch
            val obj = contentRepo.questionsByIds(paper.objectiveQuestionIds)
            val subj = contentRepo.questionsByIds(paper.subjectiveQuestionIds)
            _attemptState.value = SamplePaperAttemptState(paper = paper, objectiveQuestions = obj, subjectiveQuestions = subj, loading = false)
        }
    }

    fun startAttempt() {
        val s = _attemptState.value
        val paper = s.paper ?: return
        startTime = System.currentTimeMillis()
        _attemptState.value = s.copy(started = true, remainingSeconds = paper.durationMinutes * 60)
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_attemptState.value.remainingSeconds > 0 && !_attemptState.value.submitted) {
                delay(1000)
                _attemptState.value = _attemptState.value.copy(remainingSeconds = _attemptState.value.remainingSeconds - 1)
            }
            if (!_attemptState.value.submitted) submit()
        }
    }

    fun selectAnswer(qId: String, index: Int) {
        val s = _attemptState.value
        _attemptState.value = s.copy(answers = s.answers + (qId to index))
    }

    fun revealSubjective(qId: String) {
        val s = _attemptState.value
        _attemptState.value = s.copy(revealedSubjective = s.revealedSubjective + qId)
    }

    fun submit() {
        timerJob?.cancel()
        val s = _attemptState.value
        if (s.submitted || s.paper == null) return
        val timeTaken = ((System.currentTimeMillis() - startTime) / 1000).toInt()
        viewModelScope.launch {
            val profile = profileRepo.profileFlow().first()
            val fullAnswers = s.objectiveQuestions.associate { it.id to s.answers[it.id] }
            attemptRepo.submitTest("SAMPLE_PAPER", s.paper.subject, null, profile?.classLevel ?: 12, profile?.board ?: "CBSE", fullAnswers, emptySet(), timeTaken)
            val correct = s.objectiveQuestions.count { s.answers[it.id] == it.correctIndex }
            _attemptState.value = s.copy(submitted = true, correct = correct)
        }
    }
    override fun onCleared() { timerJob?.cancel(); super.onCleared() }
}
