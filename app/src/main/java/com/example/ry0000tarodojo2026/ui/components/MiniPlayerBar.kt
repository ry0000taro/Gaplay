package com.example.ry0000tarodojo2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ry0000tarodojo2026.data.model.VideoEntity

@Composable
fun MiniPlayerBar(
    video: VideoEntity?,
    onExpand: () -> Unit,
    onClose: () -> Unit
    ){
    if (video == null) return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable{ onExpand()},
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 8.dp
    ){
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ){
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.width(110.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ){
                Text(
                    text = video.title,
                    style= MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = video.channelTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onClose){
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Player"
                )
        }
        }
    }
}