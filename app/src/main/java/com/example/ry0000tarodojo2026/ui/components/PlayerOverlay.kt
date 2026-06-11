package com.example.ry0000tarodojo2026.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.movableContentOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import com.example.ry0000tarodojo2026.ui.screens.TimerPlayerScreen
import com.example.ry0000tarodojo2026.ui.viewmodel.MainUiState
import com.example.ry0000tarodojo2026.ui.viewmodel.PlayerMode

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PlayerOverlay(
    uiState: MainUiState,
    bottomPadding: Dp,
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

    SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = uiState.playerMode,
            label = "playerModeTransition"
        ) { targetMode ->
            when (targetMode) {
                PlayerMode.FULL -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) { detectTapGestures() }
                    ) {
                        TimerPlayerScreen(
                            video = uiState.selectedVideo,
                            remainingSeconds = uiState.remainingSeconds,
                            exerciseSeconds = uiState.exerciseSeconds,
                            isExercisePhase = uiState.isExercisePhase,
                            exerciseType = uiState.exerciseType,
                            onBack = onCollapse,
                            videoPlayerContent = {
                                Box(
                                    modifier = Modifier
                                        .sharedBounds(
                                            sharedContentState = rememberSharedContentState(key = "video-$videoId"),
                                            animatedVisibilityScope = this@AnimatedContent
                                        )
                                        .fillMaxSize()
                                ) {
                                    if (targetMode == uiState.playerMode) {
                                        videoPlayer()
                                    } else {
                                        Box(modifier = Modifier.fillMaxSize().background(Color.Black))
                                    }
                                }
                            }
                        )
                    }
                }
                PlayerMode.MINI -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = bottomPadding),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        MiniPlayerBar(
                            video = uiState.selectedVideo,
                            onExpand = onExpand,
                            onClose = onClose,
                            videoPlayerContent = {
                                Box(
                                    modifier = Modifier
                                        .sharedBounds(
                                            sharedContentState = rememberSharedContentState(key = "video-$videoId"),
                                            animatedVisibilityScope = this@AnimatedContent
                                        )
                                        .fillMaxSize()
                                ) {
                                    if (targetMode == uiState.playerMode) {
                                        videoPlayer()
                                    } else {
                                        Box(modifier = Modifier.fillMaxSize().background(Color.Black))
                                    }
                                }
                            }
                        )
                    }
                }
                PlayerMode.HIDDEN -> { /*何も表示しない*/ }
            }
        }
    }
}
