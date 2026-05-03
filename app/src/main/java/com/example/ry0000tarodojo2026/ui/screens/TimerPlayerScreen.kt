package com.example.ry0000tarodojo2026.ui.screens

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
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import android.content.Context
import android.hardware.SensorManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.ry0000tarodojo2026.R
import com.example.ry0000tarodojo2026.data.model.VideoEntity
import com.example.ry0000tarodojo2026.utils.ShakeDetector
import java.util.Locale

@Composable
fun TimerPlayerScreen(
    video: VideoEntity?,
    remainingSeconds: Long,
    exerciseSeconds: Long,
    isExercisePhase: Boolean,
    exerciseType: String,
    onBack: () -> Unit
) {
    // 画面の「寿命（ライフサイクル）」を管理するオブジェクトを取得
    val lifecycleOwner = LocalLifecycleOwner.current

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

    var shakeCount by remember { mutableIntStateOf(0) }
    var isSensorAvailable by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // 運動フェーズかつシェイク運動が選ばれている場合のみセンサーを有効化
    DisposableEffect(isExercisePhase, exerciseType) {
        var sensorManager: SensorManager? = null
        var shakeDetector: ShakeDetector? = null

        if (isExercisePhase && exerciseType == "スマホを振る") {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
            val accelerometer = sensorManager?.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER)
            
            if (accelerometer != null) {
                isSensorAvailable = true
                shakeDetector = ShakeDetector {
                    shakeCount++
                }
                
                sensorManager?.registerListener(
                    shakeDetector,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_UI
                )
            } else {
                isSensorAvailable = false
            }
        }

        onDispose {
            sensorManager?.unregisterListener(shakeDetector)
        }
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
                // --- WebViewからYouTubePlayerViewに変更 ---
                AndroidView(
                    factory = { context ->
                        YouTubePlayerView(context).apply {
                            // アプリのライフサイクル（停止・再開）とプレーヤーを連動させる
                            lifecycleOwner.lifecycle.addObserver(this)

                            // プレーヤーの準備ができたら動画をロードする
                            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                    // loadVideoで自動再生を開始
                                    youTubePlayer.loadVideo(videoIdOnly, 0f)
                                }
                            })
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
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
                    if (exerciseType == "スマホを振る") {
                        Text(
                            text = "🔥 シェイク回数: $shakeCount 回",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "Finish the training!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
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
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth(),
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

        if (!isSensorAvailable && isExercisePhase && exerciseType == "スマホを振る") {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.error_sensor_not_available),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = onBack, modifier = Modifier.padding(bottom = 32.dp)) {
            Text("リストに戻る", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}