package com.example.ry0000tarodojo2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ry0000tarodojo2026.ui.theme.Ry0000tarodojo2026Theme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ry0000tarodojo2026.data.api.RetrofitInstance
import com.example.ry0000tarodojo2026.data.repository.YouTubeRepository
import com.example.ry0000tarodojo2026.ui.viewmodel.MainViewModel
import com.example.ry0000tarodojo2026.BuildConfig
import com.example.ry0000tarodojo2026.data.model.VideoData
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = MainViewModel(YouTubeRepository(RetrofitInstance.api))
        enableEdgeToEdge()
        setContent {
            Ry0000tarodojo2026Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        HomeScreen(viewModel = viewModel)
                    }
                }

            }
        }
    }
}


@Composable
fun HomeScreen(viewModel: MainViewModel) {

    // ① videoIds ではなく videoList を監視するように修正
    // (MainViewModel.kt で変えた名前に合わせます)
    val videoList by viewModel.videoList.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Button(
            onClick = {
                // 引数名を limitSeconds に一次的に変更
                viewModel.searchVideos(
                    apiKey = BuildConfig.YOUTUBE_API_KEY,
                    query = "3分 フィットネス",
                    limitSeconds = 180
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("YouTubeで動画を検索する")
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(videoList) { video ->
                VideoRow(videoData = video)
            }
        }
    }
}
/*
/*
    // ① ViewModel の StateFlow を Compose 用の状態に変換
// ① videoIds ではなく videoList を受け取るように修正
    val videoList by viewModel.videoList.collectAsStateWithLifecycle()
    // 読み込み状態も受け取れるようにしておくと、検索中に「待ち」がわかります
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    Button(
        onClick = {
            // APIキーと検索条件を渡して検索実行
            viewModel.searchVideos(
                apiKey = BuildConfig.YOUTUBE_API_KEY,
                query = "3分 フィットネス", // テスト用の検索ワード
                /*
                duration = "short"       // 'short' は4分未満の動画

                 */
                limitSeconds = 180
            )
        },
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Text("YouTubeで動画を検索する")
    }

    // 結果のリスト表示
    LazyColumn(modifier = Modifier) {
        items(videoIds) { id ->
            Text(text = "取得した動画ID: $id", modifier = Modifier.padding(16.dp))
            HorizontalDivider()
        }
    }
/*

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(2f) // 2/5の比率
                .fillMaxWidth()
                .background(Color.LightGray), // 地図画像が用意できるまで灰色
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(48.dp)
                )
                Text(text = "現在地: 東京都渋谷区...")
            }
        }

        // 下部 2/3: 黒い領域
        Box(
            modifier = Modifier
                .weight(5f) // 2/5の比率
                .fillMaxWidth()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(dummyVideoList) { video ->
                    VideoRow(videoData = video)
                }
            }

        }

    }

 */
}
*/

data class VideoData(
    val id: Int,
    val thumbnailUrl: String,
    val channelName: String,
    val videoTitle: String
)

val dummyVideoList = listOf(
    VideoData(1, "thumbnail_1", "ChannnelNameA", "VideoTitle"),
    VideoData(2, "thumbnail_2", "ChannnelNameB", "2-B"),
    VideoData(3, "thumbnail_3", "C", "3-C"),
    VideoData(4, "thumbnail_4", "D", "4-D"),
    VideoData(5, "thumbnail_5", "E", "5-E"),
    VideoData(6, "thumbnail_6", "F", "6-F"),
    VideoData(7, "thumbnail_7", "G", "7-G"),
)

*/

@Composable
fun VideoRow(videoData: VideoData) {
    // 外側のカード
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(bottom = 16.dp)
    ) {
        //サムネ画像
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.BottomEnd
        ) {
            AsyncImage(
                model = videoData.thumbnailUrl, //
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 再生時間
            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = videoData.duration,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    fontSize = 12.sp
                )
            }
        }

        // チャンネル情報とタイトル
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // アイコン
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB2EBF2)),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 24.sp)
            }

            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = videoData.videoTitle,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 22.sp
                )
                Text(
                    text = "${videoData.channelName} • \${videoData.viewCount}",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // + EXCISE と 入力フィールド
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "+ EXCISE",
                color = Color(0xFF2962FF),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Excise入力フィールド
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .border(
                        1.dp, Color(0xFFE0E0E0),
                        androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text("e.g. 15 Burpees", color = Color.LightGray, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Start Now ボタン
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
        ) {
            Text(text = "Start Now", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Ry0000tarodojo2026Theme {
        HomeScreen()
    }
}
 */