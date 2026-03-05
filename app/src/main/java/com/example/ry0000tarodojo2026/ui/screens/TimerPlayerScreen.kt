package com.example.ry0000tarodojo2026.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ry0000tarodojo2026.data.model.VideoEntity
import com.pierfrancescosoffritti.androidyoutubeplayer.compose.YouTubePlayer

@Composable
fun TimerPlayerScreen(
    video: VideoEntity?,
    remainingSeconds: Long,
    onBack: () -> Unit
) {
    if (video == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("動画が選択されていません")
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        // YouTubeプレイヤー
        com.pierfrancescosoffritti.androidyoutubeplayer.compose.YouTubePlayer(
            youtubeVideoId = video.id,
            lifecycleOwner = LocalLifecycleOwner.current,
            modifier = Modifier.fillMaxWidth().aspectRatio(16/9f).padding(16.dp)
        )

        // カウントダウン表示
        Text(
            text = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = if (remainingSeconds <= 10) Color.Red else MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(onClick = onBack, modifier = Modifier.padding(bottom = 32.dp)) {
            Text("リストに戻る")
        }
    }
}