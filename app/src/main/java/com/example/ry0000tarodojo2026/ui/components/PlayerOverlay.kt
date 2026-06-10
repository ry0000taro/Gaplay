package com.example.ry0000tarodojo2026.ui.components

import androidx.compose.runtime.Composable
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
    when (uiState.playerMode){
        PlayerMode.FULL -> {
            TimerPlayerScreen(
                video = uiState.selectedVideo,
                remainingSeconds = uiState.remainingSeconds,
                exerciseSeconds = uiState.exerciseSeconds,
                isExercisePhase = uiState.isExercisePhase,
                exerciseType = uiState.exerciseType,
                onBack = onCollapse
            )
        }
        PlayerMode.MINI -> {
            MiniPlayerBar(
                video = uiState.selectedVideo,
                onExpand = onExpand,
                onClose = onClose
            )
        }
        PlayerMode.HIDDEN -> {/*何も表示しない*/}
    }
}
