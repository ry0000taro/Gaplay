package com.example.ry0000tarodojo2026.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ry0000tarodojo2026.data.model.VideoEntity
import com.example.ry0000tarodojo2026.data.repository.YouTubeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val repository: YouTubeRepository
) : ViewModel() {

    // IDを指定してRoomから動画情報を取得するメソッド
    fun getVideoById(videoId: String): Flow<VideoEntity?> {
        return repository.getVideoById(videoId)
    }
}