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
import androidx.room.Room
import com.example.ry0000tarodojo2026.data.api.RetrofitInstance
import com.example.ry0000tarodojo2026.data.local.AppDatabase
import com.example.ry0000tarodojo2026.data.local.SearchPrefs
import com.example.ry0000tarodojo2026.data.repository.YouTubeRepository
import com.example.ry0000tarodojo2026.ui.viewmodel.MainViewModel
import com.example.ry0000tarodojo2026.ui.components.SearchHeaderCard
import com.example.ry0000tarodojo2026.ui.screens.SearchListScreen
import com.example.ry0000tarodojo2026.ui.screens.TimerPlayerScreen
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 各種インスタンスの初期化
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "youtube-db"
        ).build()

        val searchPrefs = SearchPrefs(applicationContext)

        val repository = YouTubeRepository(
            apiService = RetrofitInstance.api,
            dao = db.videoDao()
        )

        val viewModel = MainViewModel(repository, searchPrefs)

        setContent {
            // ナビゲーションの司令塔
            val navController = rememberNavController()

            // --- ★ 修正ポイント1: uiState を監視し、各変数を以前と同じ名前で再定義します ---
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val videoList = uiState.videoList
            val isLoading = uiState.isLoading
            val savedQuery = uiState.lastQuery
            val savedMinutes = uiState.lastMinutes
            val selectedVideo = uiState.selectedVideo
            val remainingSeconds = uiState.remainingSeconds
            // --- ★ ここまで ---

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 全体のレイアウト構成
                    Column(modifier = Modifier.fillMaxSize()) {

                        // --- A. 固定表示される検索バー（ui/components配下） ---
                        SearchHeaderCard(
                            initialQuery = savedQuery,
                            initialMinutes = savedMinutes,
                            isLoading = isLoading,
                            onSearch = { query, seconds ->
                                // --- ★ 修正ポイント2: 赤波線の解消（名前と型を合わせる） ---
                                // seconds (Long) をそのまま ViewModel に渡します
                                viewModel.searchVideos(
                                    apiKey = BuildConfig.YOUTUBE_API_KEY,
                                    query = query,
                                    limitSeconds = seconds
                                )
                            }
                        )

                        NavHost(
                            navController = navController,
                            startDestination = Routes.SEARCH_LIST,
                            modifier = Modifier.weight(1f)
                        ) {
                            composable(Routes.SEARCH_LIST) {
                                SearchListScreen(
                                    videoList = videoList,
                                    onVideoSelect = { video ->
                                        viewModel.onVideoSelect(video)
                                        navController.navigate(Routes.TIMER_PLAYER)
                                    }
                                )
                            }

                            // 【画面2】タイマー ＋ 動画再生画面
                            composable(Routes.TIMER_PLAYER) {
                                TimerPlayerScreen(
                                    video = selectedVideo,
                                    remainingSeconds = remainingSeconds,
                                    onBack = {
                                        viewModel.onBackToList() // タイマー停止などの後処理
                                        navController.popBackStack() // リスト画面に戻る
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