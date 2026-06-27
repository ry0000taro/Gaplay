package com.example.ry0000tarodojo2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ry0000tarodojo2026.ui.viewmodel.MainViewModel
import com.example.ry0000tarodojo2026.ui.viewmodel.TimerViewModel
import com.example.ry0000tarodojo2026.ui.screens.SearchListScreen
import com.example.ry0000tarodojo2026.ui.screens.TimerPlayerScreen
import com.example.ry0000tarodojo2026.ui.theme.Ry0000tarodojo2026Theme
import com.example.ry0000tarodojo2026.ui.components.MainBottomBar
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ry0000tarodojo2026.ui.components.PlayerOverlay
import com.example.ry0000tarodojo2026.ui.viewmodel.PlayerMode

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val navController = rememberNavController()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            Ry0000tarodojo2026Theme {
                // 全画面表示の時はボトムバーを隠す
                val showBottomBar = uiState.playerMode != PlayerMode.FULL

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            MainBottomBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    // Boxでメインコンテンツとプレイヤーを重ねる
                    Box(modifier = Modifier.fillMaxSize()) {

                        // --- 【下の層】 画面遷移エリア ---
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = Routes.SEARCH_LIST
                            ) {
                                composable(Routes.SEARCH_LIST) {
                                    SearchListScreen(
                                        videoList = uiState.videoList,
                                        targetMinutes = uiState.lastMinutes,
                                        exerciseType = uiState.exerciseType,
                                        initialQuery = uiState.lastQuery,
                                        isLoading = uiState.isLoading,
                                        onSearch = { query, seconds, selectedExercise ->
                                            viewModel.searchVideos(
                                                BuildConfig.YOUTUBE_API_KEY,
                                                query,
                                                seconds,
                                                selectedExercise
                                            )
                                        },
                                        onVideoSelect = { video ->
                                            // 画面遷移せず、状態を更新してプレイヤーを表示
                                            viewModel.onVideoSelect(video)
                                        }
                                    )
                                }
                            }
                        }

                        // --- 【上の層】 プレイヤー・オーバーレイ ---
                        if (uiState.selectedVideo != null) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                PlayerOverlay(
                                    uiState = uiState,
                                    bottomPadding = innerPadding.calculateBottomPadding(),
                                    onCollapse = { viewModel.updatePlayerMode(PlayerMode.MINI) },
                                    onExpand = { viewModel.updatePlayerMode(PlayerMode.FULL) },
                                    onClose = { viewModel.closePlayer() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}