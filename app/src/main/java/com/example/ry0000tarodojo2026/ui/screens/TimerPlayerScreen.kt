package com.example.ry0000tarodojo2026.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ry0000tarodojo2026.data.model.VideoEntity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun TimerPlayerScreen(
    video: VideoEntity?,
    remainingSeconds: Long,
    exerciseSeconds: Long,
    onPlay: () -> Unit = {},
    onPause: () -> Unit = {},
    onBack: () -> Unit
) {
    if (video == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("動画を準備中...")
        }
        return
    }

    val videoIdOnly = remember(video.id) {
        video.id.trim().split("v=").last().split("&").first().split("/").last()
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        // YouTubePlayerViewをAndroidViewでラップしてCompose内で使用
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    YouTubePlayerView(context).apply {
                        addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                youTubePlayer.cueVideo(videoIdOnly, 0f)
                                onPlay()
                            }
                        })
                    }
                },
                update = { view ->
                    // 必要に応じて更新処理を記述
                }
            )
        }

        Text(
            text = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60),
            style = MaterialTheme.typography.displayLarge,
            color = if (remainingSeconds <= 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )

        if (exerciseSeconds > 0) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "動画のあとに ${exerciseSeconds / 60}分${exerciseSeconds % 60}秒 の運動をしましょう！",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(onClick = onBack, modifier = Modifier.padding(bottom = 32.dp)) {
            Text("リストに戻る")
        }
    }
}
