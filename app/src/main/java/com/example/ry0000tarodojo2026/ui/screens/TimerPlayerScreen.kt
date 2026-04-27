package com.example.ry0000tarodojo2026.ui.screens

import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ry0000tarodojo2026.data.model.VideoEntity
import java.util.Locale

@Composable
fun TimerPlayerScreen(
    video: VideoEntity?,
    remainingSeconds: Long,
    exerciseSeconds: Long,
    isExercisePhase: Boolean,
    onBack: () -> Unit
) {
    if (video == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // YouTubeの動画IDのみを抽出（URL形式でもID形式でも対応）
    val videoIdOnly = remember(video.id) {
        video.id.trim().split("v=").last().split("&").first().split("/").last()
    }

    val phaseColor by animateColorAsState(
        targetValue = if (isExercisePhase) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
        label = "phaseColor"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 動画再生セクション（16:9）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (!isExercisePhase) {
                // アプリ内でWebを開いて再生する仕様（WebView）
                AndroidView(factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        webViewClient = WebViewClient()
                        webChromeClient = WebChromeClient() // 動画再生をスムーズにするために追加
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true // YouTube再生に推奨
                        settings.mediaPlaybackRequiresUserGesture = false // 自動再生を許可
                        
                        // 埋め込み用URLをロード
                        loadUrl("https://www.youtube.com/embed/$videoIdOnly?autoplay=1")
                    }
                }, modifier = Modifier.fillMaxSize())
            } else {
                // 運動中（WORKOUT）の表示
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "EXERCISE TIME!",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Finish the training!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 現在のフェーズ表示
        Surface(
            color = phaseColor.copy(alpha = 0.1f),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = if (isExercisePhase) "WORKOUT PHASE" else "WATCHING PHASE",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                color = phaseColor,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // タイマー表示
        Text(
            text = String.format(Locale.US, "%02d:%02d", remainingSeconds / 60, remainingSeconds % 60),
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 80.sp,
                fontWeight = FontWeight.Black
            ),
            color = phaseColor
        )

        Spacer(modifier = Modifier.height(24.dp))

        // メッセージカード
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isExercisePhase) MaterialTheme.colorScheme.tertiaryContainer 
                                 else MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = if (isExercisePhase) "🔥 最後の追い込みです！" 
                       else "📺 次は ${exerciseSeconds / 60}分${exerciseSeconds % 60}秒 の運動です",
                modifier = Modifier.padding(20.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = onBack, modifier = Modifier.padding(bottom = 32.dp)) {
            Text("リストに戻る", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
