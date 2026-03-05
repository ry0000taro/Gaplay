package com.example.ry0000tarodojo2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import coil.compose.AsyncImage
import com.example.ry0000tarodojo2026.data.api.RetrofitInstance
import com.example.ry0000tarodojo2026.data.local.AppDatabase
import com.example.ry0000tarodojo2026.data.model.VideoEntity
import com.example.ry0000tarodojo2026.data.repository.YouTubeRepository
import com.example.ry0000tarodojo2026.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. データベース・リポジトリ・ViewModelを準備
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "youtube-db"
        ).build()

        val repository = YouTubeRepository(
            apiService = RetrofitInstance.api,
            dao = db.videoDao()
        )

        val viewModel = MainViewModel(repository)

        setContent {
            // 2. ViewModelの状態をComposeで監視できるように変換
            val videoList by viewModel.videoList.collectAsStateWithLifecycle()
            val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // ★ ここが重要！ 作成したMainScreenをここで呼び出します
                    MainScreen(
                        videoList = videoList,
                        isLoading = isLoading,
                        onSearchClick = { query, seconds ->
                            // 検索実行時にViewModelの関数を呼ぶ
                            viewModel.searchVideos(BuildConfig.YOUTUBE_API_KEY, query, seconds)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    videoList: List<VideoEntity>,
    isLoading: Boolean,
    onSearchClick: (String, Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf("K-POP") }
    var durationMinutes by remember { mutableStateOf("3") }

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

        // --- 2. 動画リストエリア（LazyColumn） ---
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(videoList) { video ->
                VideoItemRow(video = video)
            }
        }
    }
}

@Composable
fun VideoItemRow(video: VideoEntity) {
    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(modifier = Modifier.height(200.dp).fillMaxWidth()) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                ) {
                    Text(
                        text = video.duration ?: "0:00",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = video.channelTitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                    Text("調理開始（動画を再生）", fontWeight = FontWeight.Bold)
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