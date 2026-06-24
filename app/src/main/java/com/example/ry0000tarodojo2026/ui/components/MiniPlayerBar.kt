package com.example.ry0000tarodojo2026.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
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
    isExercisePhase: Boolean = false,
    onExpand: () -> Unit,
    onClose: () -> Unit,
    sharedBoundsModifier: Modifier = Modifier,
    videoPlayerContent: @Composable () -> Unit
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
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
                    .then(sharedBoundsModifier)
            ) {
                if (isExercisePhase) {
                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.tertiaryContainer), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                } else {
                    videoPlayerContent()
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ){
                if (isExercisePhase) {
                    Text(
                        text = "EXERCISE TIME!",
                        style= MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Keep it up!",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
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