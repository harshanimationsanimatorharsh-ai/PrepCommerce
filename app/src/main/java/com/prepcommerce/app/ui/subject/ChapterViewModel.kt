package com.prepcommerce.app.ui.subject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prepcommerce.app.data.local.dao.UserDataDao
import com.prepcommerce.app.data.local.entities.ChapterEntity
import com.prepcommerce.app.data.local.entities.ChapterProgressEntity
import com.prepcommerce.app.data.repository.ContentRepository
import com.prepcommerce.app.data.repository.ProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChapterListUiState(
    val chapters: List<ChapterEntity> = emptyList(),
    val progressMap: Map<String, ChapterProgressEntity> = emptyMap(),
    val subject: String = "",
    val loading: Boolean = true
)

data class ChapterDetailUiState(
    val chapter: ChapterEntity? = null,
    val progress: ChapterProgressEntity? = null,
    val mcqCount: Int = 0,
    val qaCount: Int = 0,
    val loading: Boolean = true
)

class ChapterViewModel(
    private val contentRepo: ContentRepository,
    private val userDao: UserDataDao,
    private val profileRepo: ProfileRepository
) : ViewModel() {
    private val _listState = MutableStateFlow(ChapterListUiState())
    val listState: StateFlow<ChapterListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(ChapterDetailUiState())
    val detailState: StateFlow<ChapterDetailUiState> = _detailState.asStateFlow()

    fun loadChapters(subject: String) {
        viewModelScope.launch {
            _listState.value = ChapterListUiState(subject = subject, loading = true)
            val profile = profileRepo.profileFlow().first()
            val classLevel = profile?.classLevel ?: 12
            val board = profile?.board ?: "CBSE"
            contentRepo.chapters(subject, classLevel, board).collect { chapters ->
                val progressAll = userDao.getAllChapterProgress().first()
                val map = progressAll.associateBy { it.chapterId }
                _listState.value = ChapterListUiState(chapters, map, subject, false)
            }
        }
    }

    fun loadChapterDetail(chapterId: String) {
        viewModelScope.launch {
            _detailState.value = ChapterDetailUiState(loading = true)
            val chapter = contentRepo.chapter(chapterId)
            val progress = userDao.getChapterProgress(chapterId)
            val mcqs = contentRepo.mcqsForChapter(chapterId)
            val qa = contentRepo.qaForChapter(chapterId)
            _detailState.value = ChapterDetailUiState(chapter, progress, mcqs.size, qa.size, false)
        }
    }
}
