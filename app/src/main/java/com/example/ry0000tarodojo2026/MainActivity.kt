package com.example.ry0000tarodojo2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import coil.compose.AsyncImage
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

            // ViewModel からの状態を監視
            val videoList by viewModel.videoList.collectAsStateWithLifecycle()
            val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
            val savedQuery by viewModel.lastSearchQuery.collectAsStateWithLifecycle()
            val savedMinutes by viewModel.lastDurationMinutes.collectAsStateWithLifecycle()
            val selectedVideo by viewModel.selectedVideo.collectAsStateWithLifecycle()
            val remainingSeconds by viewModel.remainingSeconds.collectAsStateWithLifecycle()

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
                                viewModel.searchVideos(BuildConfig.YOUTUBE_API_KEY, query, seconds.toString())
                            }
                        )

@Composable
fun MainScreen(
    videoList: List<VideoEntity>,
    isLoading: Boolean,
    initialQuery: String,
    initialMinutes: String,
    onSearchClick: (String, Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf("K-POP") }
    var durationMinutes by remember { mutableStateOf("3") }
// ★ 重要：DataStoreから値が届いたら、入力欄の文字を更新する
    LaunchedEffect(initialQuery, initialMinutes) {
        searchQuery = initialQuery
        durationMinutes = initialMinutes
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- 1. 検索設定エリア ---
        ElevatedCard(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "タイマー設定",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Youtubeを検索") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = durationMinutes,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) durationMinutes = it
                        },
                        label = { Text("分") },
                        modifier = Modifier.width(80.dp),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        shape = MaterialTheme.shapes.medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val mins = durationMinutes.toLongOrNull() ?: 3L
                        onSearchClick(searchQuery, mins * 60)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && durationMinutes.isNotEmpty(),
                    shape = CircleShape
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("動画を探しています...")
                    } else {
                        Text("${durationMinutes}分以内の動画を検索")
                    }
                }
            }
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

                Spacer(modifier = Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "+ EXERCISE",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF2196F3),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    var exerciseText by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = exerciseText,
                        onValueChange = { exerciseText = it },
                        placeholder = { Text("例: 腕立て20回", style = MaterialTheme.typography.bodySmall) },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = CircleShape,
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /* TODO: タイマーと連携 */ },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = CircleShape
                ) {
                    Text("動画を再生・タイマー開始", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "OneShotPJ ビデオカードプレビュー")
@Composable
fun PreviewVideoItem() {
    MaterialTheme {
        val sample = VideoEntity(
            id = "preview_id",
            title = "【カップ麺3分】本格エクササイズで待ち時間を有効活用！",
            thumbnailUrl = "",
            channelTitle = "東出トレーニングラボ",
            duration = "3:00",
            savedAt = System.currentTimeMillis()
        )
        VideoItemRow(video = sample)
    }
}