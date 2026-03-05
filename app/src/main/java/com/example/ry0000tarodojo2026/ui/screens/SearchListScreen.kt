package com.example.ry0000tarodojo2026.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ry0000tarodojo2026.VideoItemRow
import com.example.ry0000tarodojo2026.data.model.VideoEntity


@Composable
fun SearchListScreen(
    videoList: List<VideoEntity>,
    onVideoSelect: (VideoEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(videoList) { video ->
            // タップで再生画面へ遷移するためのSurface
            Surface(onClick = { onVideoSelect(video) }, color = Color.Transparent) {
                VideoItemRow(video = video) // ※VideoItemRowも別ファイル化を推奨
            }
        }
    }
}