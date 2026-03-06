package com.example.ry0000tarodojo2026.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
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
    exerciseSeconds: Long, // ★ 追加：計算された運動時間
    onBack: () -> Unit
) {
    if (video == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("動画が選択されていません")
        }
        return
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        // YouTubeプレイヤー表示エリア
        AndroidView(
            factory = { context ->
                YouTubePlayerView(context).apply {
                    lifecycleOwner.lifecycle.addObserver(this)
                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.loadVideo(video.id, 0f)
                        }
                    })
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .padding(16.dp)
        )

        // カウントダウン表示
        Text(
            text = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = if (remainingSeconds <= 10) Color.Red else MaterialTheme.colorScheme.primary
        )

        // --- ★ ここにエクササイズメッセージを追加！ ---
        if (exerciseSeconds > 0) {
            val exMin = exerciseSeconds / 60
            val exSec = exerciseSeconds % 60

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "動画のあとに ${exMin}分${exSec}秒 の運動をしましょう！",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(onClick = onBack, modifier = Modifier.padding(bottom = 32.dp)) {
            Text("リストに戻る")
        }
    }
}