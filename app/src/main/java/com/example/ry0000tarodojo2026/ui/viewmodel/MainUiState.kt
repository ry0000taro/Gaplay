package com.example.ry0000tarodojo2026.ui.viewmodel

import com.example.ry0000tarodojo2026.data.model.VideoEntity

data class MainUiState(
    val videoList: List<VideoEntity> = emptyList(),
    val isLoading: Boolean = false,
    val selectedVideo: VideoEntity? = null,
    val remainingSeconds: Long = 0,
    val lastQuery: String = "K-POP",
    val lastMinutes: String = "3",
    val exerciseSeconds: Long = 0,
    val isExercisePhase: Boolean = false
)