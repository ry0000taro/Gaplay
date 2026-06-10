package com.example.ry0000tarodojo2026.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.movableContentOf
import androidx.compose.ui.Modifier
import com.example.ry0000tarodojo2026.ui.screens.TimerPlayerScreen
import com.example.ry0000tarodojo2026.ui.viewmodel.MainUiState
import com.example.ry0000tarodojo2026.ui.viewmodel.PlayerMode

@Composable
fun PlayerOverlay(
    uiState: MainUiState,
    onCollapse: () -> Unit,
    onExpand: () -> Unit,
    onClose: () -> Unit
) {
    val videoId = uiState.selectedVideo?.id
    val videoPlayer = remember(videoId) {
        movableContentOf {
            if (videoId != null) {
                VideoPlayerView(
                    videoId = videoId,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
    when (uiState.playerMode){
        PlayerMode.FULL -> {
            TimerPlayerScreen(
                video = uiState.selectedVideo,
                remainingSeconds = uiState.remainingSeconds,
                exerciseSeconds = uiState.exerciseSeconds,
                isExercisePhase = uiState.isExercisePhase,
                exerciseType = uiState.exerciseType,
                onBack = onCollapse,
                videoPlayerContent = videoPlayer
            )
        }
        PlayerMode.MINI -> {
            MiniPlayerBar(
                video = uiState.selectedVideo,
                onExpand = onExpand,
                onClose = onClose,
                videoPlayerContent = videoPlayer
            )
        }
        PlayerMode.HIDDEN -> {/*何も表示しない*/}
    }
}
