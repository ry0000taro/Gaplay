package com.example.ry0000tarodojo2026.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ry0000tarodojo2026.data.local.SearchPrefs
import com.example.ry0000tarodojo2026.data.model.ExerciseType
import com.example.ry0000tarodojo2026.data.model.VideoEntity
import com.example.ry0000tarodojo2026.data.repository.YouTubeRepository
import com.example.ry0000tarodojo2026.data.repository.HistoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import com.example.ry0000tarodojo2026.data.repository.NoodleRepository

/**
 * 画面のデータを管理する監督役（ViewModel）
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: YouTubeRepository,
    private val searchPrefs: SearchPrefs,
    private val historyRepository: HistoryRepository,
    private val noodleRepository: NoodleRepository
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
            _uiState.update { state ->
                // 運動フェーズに突入した瞬間にミニプレイヤーならフルスクリーンに戻す
                val newPlayerMode = if (isExercise && !state.isExercisePhase && state.playerMode == PlayerMode.MINI) {
                    PlayerMode.FULL
                } else {
                    state.playerMode
                }
                
                state.copy(
                    videoList = videos,
                    lastQuery = query,
                    lastMinutes = mins,
                    exerciseType = exType,
                    remainingSeconds = seconds,
                    isExercisePhase = isExercise,
                    playerMode = newPlayerMode
                )
            }
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
            exerciseSeconds = exerciseSec,
            playerMode = PlayerMode.FULL
        ) }
        timerManager.start(videoSeconds, exerciseSec)
        
        // 合計時間（動画時間 ＋ 運動時間）を計算
        val totalSeconds = videoSeconds + exerciseSec
        
        // 視聴履歴の保存
        viewModelScope.launch {
            val result = historyRepository.saveWatchHistory(
                videoId = video.id, 
                videoTitle = video.title, 
                videoDurationSeconds = videoSeconds,
                exerciseDurationSeconds = exerciseSec,
                totalDurationSeconds = totalSeconds,
                exerciseType = _uiState.value.exerciseType.id
            )
            
            // エラー時（未ログインや通信失敗など）の処理をここで検知できるようになった
            result.onFailure { exception ->
                // 例：ログに出力したり、SnackBarで「保存に失敗しました」と表示するための状態を更新したりする
                exception.printStackTrace()
            }
        }
    }

    fun updatePlayerMode(mode: PlayerMode){
        _uiState.update{
            it.copy(playerMode = mode) }
    }

    fun closePlayer(){
        timerManager.stop()
        _uiState.update{it.copy(
            selectedVideo = null,
            playerMode =  PlayerMode.HIDDEN
        )}
    }

    fun onBackToList() {
        _uiState.update { it.copy(selectedVideo = null) }
        timerManager.stop()
    }

    fun searchNoodle(janCode: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = noodleRepository.getNoodleInfo(janCode)
            result.onSuccess { noodle ->
                // Noodle情報が取得できたら、検索条件を更新してHome画面へ遷移させる
                searchPrefs.saveSearchConditions(noodle.name, noodle.timeMinutes.toString(), _uiState.value.exerciseType)
                onSuccess()
            }.onFailure { throwable ->
                val message = when (throwable) {
                    is retrofit2.HttpException ->
                        if (throwable.code() == 404) "カップ麺のデータが見つかりませんでした" else "サーバーエラーが発生しました (${throwable.code()})"
                    is java.io.IOException -> "通信に失敗しました。ネットワークを確認してください"
                    else -> "予期しないエラーが発生しました"
                }
                onError(message)
            }
        }
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
