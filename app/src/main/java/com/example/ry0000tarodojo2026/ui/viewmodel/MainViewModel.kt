package com.example.ry0000tarodojo2026.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ry0000tarodojo2026.data.repository.YouTubeRepository
import com.example.ry0000tarodojo2026.data.model.VideoData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 画面のデータを管理する監督役（ViewModel）
 */
class MainViewModel(private val repository: YouTubeRepository) : ViewModel() {

    // ✅ 1. UIに渡す「完成した動画リスト」を保持する箱
    private val _videoList = MutableStateFlow<List<VideoData>>(emptyList())
    val videoList: StateFlow<List<VideoData>> = _videoList.asStateFlow()

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
                // 3. IDだけでなく、時間選別された完成品を受け取る
                val result = repository.getVideosWithinDuration(apiKey, query, limitSeconds)
                _videoList.value = result
            } finally {
                _isLoading.value = false // 通信終了（成功でも失敗でも）
            }
        }
        }

}