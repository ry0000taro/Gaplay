package com.example.ry0000tarodojo2026.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ry0000tarodojo2026.data.local.SearchPrefs
import com.example.ry0000tarodojo2026.data.model.VideoData
import com.example.ry0000tarodojo2026.data.repository.YouTubeRepository
import com.example.ry0000tarodojo2026.data.model.VideoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 画面のデータを管理する監督役（ViewModel）
 */
class MainViewModel(
    private val repository: YouTubeRepository,
    private val searchPrefs: SearchPrefs
) : ViewModel() {

    val videoList: StateFlow<List<VideoEntity>> = repository.allVideos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // アプリが閉じられて5秒後に停止
            initialValue = emptyList()
        )

    // ★2. 前回の検索ワードをStateFlowに変換
    val lastSearchQuery: StateFlow<String> = searchPrefs.lastQuery
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "カップ麺")

    // ★3. 前回の分数をStateFlowに変換
    val lastDurationMinutes: StateFlow<String> = searchPrefs.lastMinutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "3")

    // ✅ 2. 読み込み中かどうかを管理するフラグ（これがないとエラー！）
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    /**
     * 動画を検索してIDリストを更新する
     */
    fun searchVideos(apiKey: String, query: String, limitSeconds: Long = 180) {
        viewModelScope.launch {
            _isLoading.value = true // 通信開始！
            try {
                searchPrefs.saveSearchConditions(query, limitSeconds.toString())
                // ✅ 【変更】戻り値を受け取らず、DBの更新（リフレッシュ）だけを依頼する
                // これだけで、上の videoList (Flow) が自動的に最新データに切り替わります
                repository.refreshVideosWithinDuration(apiKey, query, limitSeconds)

                android.util.Log.d("RoomCheck", "保存件数: ${videoList.value.size} 件")
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "検索または保存に失敗しました（オフラインの可能性があります", e)

            } finally {
                _isLoading.value = false
            }
        }
        }

}
