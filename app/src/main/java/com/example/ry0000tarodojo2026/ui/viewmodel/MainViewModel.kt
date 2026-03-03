package com.example.ry0000tarodojo2026.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ry0000tarodojo2026.data.repository.YouTubeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 画面のデータを管理する監督役（ViewModel）
 */
class MainViewModel(private val repository: YouTubeRepository) : ViewModel() {

    // 動画IDのリストを保持する（外部からは書き換え不可）
    private val _videoIds = MutableStateFlow<List<String>>(emptyList())

    // 画面（UI）が監視するための「窓口」（外部からは読み取り専用）
    // videoIdをMutableStateFlowからStateFlowに変更して読み取り専用にする

    val videoIds: StateFlow<List<String>> = _videoIds.asStateFlow()
    /**
     * 動画を検索してIDリストを更新する
     */
    fun searchVideos(apiKey: String, query: String, duration: String) {
        // 通信（suspend関数）を動かすためのスコープ
        viewModelScope.launch {
            // リポジトリに依頼してIDリストを取得
            val ids = repository.fetchVideoIds(apiKey, query, duration)

            // 取得した結果で動画IDリストの中身を更新する
            _videoIds.value = ids
        }
    }
}