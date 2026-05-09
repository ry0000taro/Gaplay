package com.example.ry0000tarodojo2026.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.example.ry0000tarodojo2026.R
import com.example.ry0000tarodojo2026.data.model.ExerciseType
import com.example.ry0000tarodojo2026.data.model.VideoEntity
import com.example.ry0000tarodojo2026.utils.ShakeDetector
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerPlayerScreen(
    video: VideoEntity?,
    remainingSeconds: Long,
    exerciseSeconds: Long,
    isExercisePhase: Boolean,
    exerciseType: ExerciseType,
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

        if (isExercisePhase && exerciseType == ExerciseType.SHAKE) {
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

    val videoSeconds = remember(video.duration) {
        if (video.duration == null) 180L
        else try {
            val parts = video.duration.split(":")
            if (parts.size == 2) {
                val minutes = parts[0].toLong()
                val seconds = parts[1].toLong()
                (minutes * 60) + seconds
            } else {
                180L
            }
        } catch (e: Exception) { 180L }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = { Text("Home", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Circular Timer
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    val strokeWidth = 12.dp.toPx()
                    
                    // Background track (Gray) for passed time
                    drawCircle(
                        color = Color(0xFFF0F0F0),
                        style = Stroke(strokeWidth)
                    )
                    
                    val totalSeconds = videoSeconds + exerciseSeconds
                    val totalTimeFloat = if (totalSeconds > 0) totalSeconds.toFloat() else 1f
                    
                    if (!isExercisePhase) {
                        // Video phase
                        val remainingVideoSweep = 360f * remainingSeconds / totalTimeFloat
                        val elapsedVideoSweep = 360f * (videoSeconds - remainingSeconds) / totalTimeFloat
                        val exerciseSweep = 360f * exerciseSeconds / totalTimeFloat
                        val videoSweepAngle = 360f * videoSeconds / totalTimeFloat
                        
                        // Remaining Video (Blue)
                        if (remainingVideoSweep > 0) {
                            drawArc(
                                color = Color(0xFF5A72A0),
                                startAngle = -90f + elapsedVideoSweep,
                                sweepAngle = remainingVideoSweep,
                                useCenter = false,
                                style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                            )
                        }
                        
                        // Remaining Exercise (Orange)
                        if (exerciseSweep > 0) {
                            drawArc(
                                color = Color(0xFFFF7A00),
                                startAngle = -90f + videoSweepAngle,
                                sweepAngle = exerciseSweep,
                                useCenter = false,
                                style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                            )
                        }
                    } else {
                        // Exercise phase
                        val remainingExerciseSweep = 360f * remainingSeconds / totalTimeFloat
                        val startAngle = -90f + 360f - remainingExerciseSweep
                        
                        if (remainingExerciseSweep > 0) {
                            drawArc(
                                color = Color(0xFFFF7A00),
                                startAngle = startAngle,
                                sweepAngle = remainingExerciseSweep,
                                useCenter = false,
                                style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TOTAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = String.format(Locale.US, "%02d:%02d", remainingSeconds / 60, remainingSeconds % 60),
                        style = MaterialTheme.typography.displayMedium,
                        color = Color(0xFF333333),
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "EXCISE PHASE: " + String.format(Locale.US, "%02d:%02d", exerciseSeconds / 60, exerciseSeconds % 60),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFF7A00),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /* Pause logic placeholder */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A5D8A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Pause, contentDescription = "Pause", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pause")
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = onBack,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF333333)),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = "Quit", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Quit")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Video Player Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (!isExercisePhase) {
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
                        if (exerciseType == ExerciseType.SHAKE) {
                            Text(
                                text = "Shake Count $shakeCount Point",
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

            Spacer(modifier = Modifier.height(16.dp))

            // Exercise Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F6F8)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCE2EF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = "Exercise",
                            tint = Color(0xFF5A72A0)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "EXERCISE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = exerciseType.name,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Text(
                            text = "120 kcal",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFF7A00),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = { /* menu action */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            }


            Spacer(modifier = Modifier.height(12.dp))

            // Channel Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F6F8)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Channel Icon",
                        tint = Color(0xFF5A72A0),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = video.channelTitle.ifBlank { "Elite Fitness" },
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Text(
                            text = "1.2M views • 2 weeks ago",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}