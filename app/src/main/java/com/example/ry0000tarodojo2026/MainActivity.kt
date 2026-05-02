package com.example.ry0000tarodojo2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.ry0000tarodojo2026.data.api.RetrofitInstance
import com.example.ry0000tarodojo2026.data.local.AppDatabase
import com.example.ry0000tarodojo2026.data.local.SearchPrefs
import com.example.ry0000tarodojo2026.data.repository.YouTubeRepository
import com.example.ry0000tarodojo2026.ui.viewmodel.MainViewModel
import com.example.ry0000tarodojo2026.ui.viewmodel.TimerViewModel
import com.example.ry0000tarodojo2026.ui.viewmodel.TimerViewModelFactory
import com.example.ry0000tarodojo2026.ui.components.SearchHeaderCard
import com.example.ry0000tarodojo2026.ui.screens.SearchListScreen
import com.example.ry0000tarodojo2026.ui.screens.TimerPlayerScreen
import com.example.ry0000tarodojo2026.ui.theme.Ry0000tarodojo2026Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "youtube-db"
        ).build()

        val searchPrefs = SearchPrefs(applicationContext)
        val repository = YouTubeRepository(RetrofitInstance.api, db.videoDao())
        val viewModel = MainViewModel(repository, searchPrefs)
        val timerViewModel = ViewModelProvider(this, TimerViewModelFactory(repository))[TimerViewModel::class.java]

        setContent {
            val navController = rememberNavController()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val videoList = uiState.videoList
            val isLoading = uiState.isLoading
            val savedQuery = uiState.lastQuery
            val savedMinutes = uiState.lastMinutes
            val remainingSeconds = uiState.remainingSeconds
            val exerciseSeconds = uiState.exerciseSeconds
            val isExercisePhase = uiState.isExercisePhase
            val exerciseType = uiState.exerciseType

            Ry0000tarodojo2026Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SearchHeaderCard(
                            initialQuery = savedQuery,
                            initialMinutes = savedMinutes,
                            initialExerciseType = exerciseType,
                            isLoading = isLoading,
                            onSearch = { query, seconds, selectedExercise ->
                                viewModel.searchVideos(BuildConfig.YOUTUBE_API_KEY, query, seconds, selectedExercise)
                            }
                        )

                        NavHost(navController = navController, startDestination = Routes.SEARCH_LIST, modifier = Modifier.weight(1f)) {
                            composable(Routes.SEARCH_LIST) {
                                SearchListScreen(
                                    videoList = videoList,
                                    targetMinutes = savedMinutes,
                                    exerciseType = exerciseType,
                                    onVideoSelect = { video ->
                                        viewModel.onVideoSelect(video)
                                        navController.navigate("timer_player/${video.id}")
                                    }
                                )
                            }

                            composable("timer_player/{videoId}") { backStackEntry ->
                                val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
                                val videoEntity by timerViewModel.getVideoById(videoId).collectAsState(initial = null)

                                TimerPlayerScreen(
                                    video = videoEntity,
                                    remainingSeconds = remainingSeconds,
                                    exerciseSeconds = exerciseSeconds,
                                    isExercisePhase = isExercisePhase,
                                    exerciseType = exerciseType,
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
