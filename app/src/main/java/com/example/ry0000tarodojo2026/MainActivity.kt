package com.example.ry0000tarodojo2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import com.example.ry0000tarodojo2026.data.api.RetrofitInstance
import com.example.ry0000tarodojo2026.data.local.AppDatabase
import com.example.ry0000tarodojo2026.data.repository.YouTubeRepository
import com.example.ry0000tarodojo2026.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 【手動初期化】本来はHiltで自動化しますが、まずは流れを理解するために手動で繋ぎます
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "youtube-db"
        ).build()

        val repository = YouTubeRepository(
            apiService = RetrofitInstance.api, // 既存のRetrofit
            dao = db.videoDao()// ステップ2のDAO
        )

        val viewModel = MainViewModel(repository)

        setContent {
            // 2. 【Observation】ViewModelのStateFlowをComposeの状態に変換して監視
            // collectAsStateWithLifecycleを使うのがAndroid開発の最新ベストプラクティスです
            val videoList by viewModel.videoList.collectAsStateWithLifecycle()
            val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

            Surface(modifier = Modifier.fillMaxSize()) {
                Column {
                    // 検索ボタン（例として「カップ麺 3分」で検索）
                    Button(
                        onClick = {
                            // APIキーはBuildConfigなどから渡してください
                            viewModel.searchVideos(BuildConfig.YOUTUBE_API_KEY, "カップ麺 3分", 180)
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(if (isLoading) "検索中..." else "YouTubeで動画を検索する")
                    }

                    // 3. 【表示】DBから流れてきたリストを表示
                    // Repositoryで保存（insert）した瞬間に、ここが勝手に描き変わります
                    LazyColumn {
                        items(videoList) { video ->
                            VideoItemRow(video) // 各動画の見た目を作るComposable（別で作るか、ここに記述）
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoItemRow(video: com.example.ry0000tarodojo2026.data.model.VideoEntity) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = video.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "${video.channelTitle} • ${video.duration}", style = MaterialTheme.typography.bodySmall)
        }
    }
}