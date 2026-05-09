package com.example.ry0000tarodojo2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val navController = rememberNavController()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            Ry0000tarodojo2026Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { MainBottomBar(navController = navController) }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavHost(navController = navController, startDestination = Routes.SEARCH_LIST) {
                            composable(Routes.SEARCH_LIST) {
                                SearchListScreen(
                                    videoList = uiState.videoList,
                                    targetMinutes = uiState.lastMinutes,
                                    exerciseType = uiState.exerciseType,
                                    initialQuery = uiState.lastQuery,
                                    isLoading = uiState.isLoading,
                                    onSearch = { query, seconds, selectedExercise ->
                                        viewModel.searchVideos(BuildConfig.YOUTUBE_API_KEY, query, seconds, selectedExercise)
                                    },
                                    onVideoSelect = { video ->
                                        viewModel.onVideoSelect(video)
                                        navController.navigate("timer_player/${video.id}")
                                    }
                                )
                            }

                            composable("timer_player/{videoId}") { backStackEntry ->
                                val timerViewModel: TimerViewModel = hiltViewModel()
                                val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
                                val videoEntity by timerViewModel.getVideoById(videoId).collectAsState(initial = null)

                                TimerPlayerScreen(
                                    video = videoEntity,
                                    remainingSeconds = uiState.remainingSeconds,
                                    exerciseSeconds = uiState.exerciseSeconds,
                                    isExercisePhase = uiState.isExercisePhase,
                                    exerciseType = uiState.exerciseType,
                                    onBack = {
                                        viewModel.onBackToList()
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
