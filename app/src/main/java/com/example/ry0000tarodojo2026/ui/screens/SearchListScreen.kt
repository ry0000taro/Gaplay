package com.example.ry0000tarodojo2026.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ry0000tarodojo2026.data.model.VideoEntity
// ★ ここのインポートが component 側を向いているか確認
import com.example.ry0000tarodojo2026.ui.components.VideoItemRow

@Composable
fun SearchListScreen(
    videoList: List<VideoEntity>,
    targetMinutes: String,
    exerciseType: String,
    onVideoSelect: (VideoEntity) -> Unit
) {
    val targetSeconds = (targetMinutes.toLongOrNull() ?: 3L) * 60

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(videoList) { video ->
            val videoSeconds = parseDurationToSeconds(video.duration)
            val exerciseSec = (targetSeconds - videoSeconds).coerceAtLeast(0L)
            val exerciseText = String.format("%01d:%02d", exerciseSec / 60, exerciseSec % 60)

            Surface(onClick = { onVideoSelect(video) }, color = Color.Transparent) {
                VideoItemRow(
                    video = video,
                    exerciseTimeText = exerciseText,
                    exerciseType = exerciseType
                )
            }
        }
    }
}

private fun parseDurationToSeconds(duration: String?): Long {
    if (duration == null) return 0L
    return try {
        val parts = duration.split(":")
        if (parts.size == 2) {
            (parts[0].toLong() * 60) + parts[1].toLong()
        } else {
            0L
        }
    } catch (e: Exception) { 0L }
}