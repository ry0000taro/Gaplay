package com.example.ry0000tarodojo2026.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ry0000tarodojo2026.data.model.VideoEntity
import com.example.ry0000tarodojo2026.data.repository.YouTubeRepository
import kotlinx.coroutines.flow.Flow

class TimerViewModel(private val repository: YouTubeRepository) : ViewModel() {

    // IDを指定してRoomから動画情報を取得するメソッド
    fun getVideoById(videoId: String): Flow<VideoEntity?> {
        return repository.getVideoById(videoId)
    }
}

// MainActivityなどでViewModelを生成するための「工場」
class TimerViewModelFactory(private val repository: YouTubeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}