package com.example.ry0000tarodojo2026.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ry0000tarodojo2026.data.local.SearchPrefs
import com.example.ry0000tarodojo2026.data.model.ExerciseType
import com.example.ry0000tarodojo2026.data.model.VideoEntity
import com.example.ry0000tarodojo2026.data.repository.YouTubeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 画面のデータを管理する監督役（ViewModel）
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: YouTubeRepository,
    private val searchPrefs: SearchPrefs
) : ViewModel() {

    // タイマーの専門家を用意（工程1で作成予定）
    private val timerManager = ExerciseTimerManager(viewModelScope)

    // 全ての情報を一つの StateFlow で管理（工程2で作成）
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        // 各データソースを統合して UI 状態を自動更新する
        val prefsFlow = combine(
            searchPrefs.lastQuery,
            searchPrefs.lastMinutes,
            searchPrefs.lastExerciseType
        ) { query, mins, exType ->
            Triple(query, mins, exType)
        }

        combine(
            repository.allVideos,
            prefsFlow,
            timerManager.remainingSeconds,
            timerManager.isExercisePhase
        ) { videos, (query, mins, exType), seconds, isExercise ->
            _uiState.update { it.copy(
                videoList = videos,
                lastQuery = query,
                lastMinutes = mins,
                exerciseType = exType,
                remainingSeconds = seconds,
                isExercisePhase = isExercise
            ) }
        }.launchIn(viewModelScope)
    }

    // 引数を ExerciseType に変更
    fun searchVideos(apiKey: String, query: String, limitSeconds: Long, exerciseType: ExerciseType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val minutesString = (limitSeconds / 60).toString()
            searchPrefs.saveSearchConditions(query, minutesString, exerciseType)
            repository.refreshVideosWithinDuration(apiKey, query, limitSeconds)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onVideoSelect(video: VideoEntity) {
        val videoSeconds = parseDurationToSeconds(video.id, video.duration)
        val targetSeconds = (_uiState.value.lastMinutes.toLongOrNull() ?: 3L) * 60
        val exerciseSec = (targetSeconds - videoSeconds).coerceAtLeast(0L)
        _uiState.update { it.copy(
            selectedVideo = video,
            exerciseSeconds = exerciseSec
        ) }
        timerManager.start(videoSeconds, exerciseSec)
    }

    fun onBackToList() {
        _uiState.update { it.copy(selectedVideo = null) }
        timerManager.stop()
    }

    private fun parseDurationToSeconds(id: String, duration: String?): Long {
        if (duration == null) return 180L
        return try {
            val parts = duration.split(":")
            val minutes = parts[0].toLong()
            val seconds = parts[1].toLong()
            (minutes * 60) + seconds
        } catch (e: Exception) {
            180L
        }
    }
}
