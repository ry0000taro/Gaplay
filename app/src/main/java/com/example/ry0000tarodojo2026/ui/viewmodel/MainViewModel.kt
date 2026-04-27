package com.example.ry0000tarodojo2026.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ry0000tarodojo2026.data.local.SearchPrefs
import com.example.ry0000tarodojo2026.data.model.VideoEntity
import com.example.ry0000tarodojo2026.data.repository.YouTubeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 画面のデータを管理する監督役（ViewModel）
 */
class MainViewModel(
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
        combine(
            repository.allVideos,
            searchPrefs.lastQuery,
            searchPrefs.lastMinutes,
            timerManager.remainingSeconds,
            timerManager.isExercisePhase
        ) { videos, query, mins, seconds, isExercise ->
            _uiState.update { it.copy(
                videoList = videos,
                lastQuery = query,
                lastMinutes = mins,
                remainingSeconds = seconds,
                isExercisePhase = isExercise
            ) }
        }.launchIn(viewModelScope)
    }

    fun searchVideos(apiKey: String, query: String, limitSeconds: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // 秒数を「分」の文字列に戻して保存（SearchPrefsがStringを期待しているため）
            val minutesString = (limitSeconds / 60).toString()
            searchPrefs.saveSearchConditions(query, minutesString)

            // 検索実行（すでに秒数になっているのでそのまま渡す）
            repository.refreshVideosWithinDuration(apiKey, query, limitSeconds)

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onVideoSelect(video: VideoEntity) {
        // 1. 動画の長さを秒に変換
        val videoSeconds = parseDurationToSeconds(video.id, video.duration)
        // 2. ユーザーが設定した目標時間を秒に変換 (uiStateから現在の値を取得)
        val targetSeconds = (_uiState.value.lastMinutes.toLongOrNull() ?: 3L) * 60
        // 3. 差分（エクササイズ時間）を計算
        val exerciseSec = (targetSeconds - videoSeconds).coerceAtLeast(0L)
        // 4. UiState を一気に更新！
        _uiState.update { it.copy(
            selectedVideo = video,
            exerciseSeconds = exerciseSec // ★ここを忘れずに追加！
        ) }
        timerManager.start(videoSeconds, exerciseSec)
    }
    fun onBackToList() {
        _uiState.update { it.copy(selectedVideo = null) }
        timerManager.stop()
    }

    /**
     * "M:SS" 形式の文字列を秒数(Long)に変換するヘルパー関数
     */
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
