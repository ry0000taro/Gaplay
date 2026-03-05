package com.example.ry0000tarodojo2026.data.model

import com.example.ry0000tarodojo2026.data.model.VideoEntity
import com.example.ry0000tarodojo2026.data.model.VideoItem

/**
 * 画面（UI）に表示するために最適化された動画データ
 */
data class VideoData(
    val id: String,             // リスト内での識別用
    val thumbnailUrl: String, // サムネイル画像のURL
    val videoTitle: String,   // 動画のタイトル
    val channelName: String,  // チャンネル名
    val viewCount: String,
    val duration: String,
    val channelId: String
)

fun VideoItem.toEntity(): VideoEntity {
    return VideoEntity(
        id = this.id,
        title = this.snippet.title,
        thumbnailUrl = this.snippet.thumbnails.high.url,
        channelTitle = this.snippet.channelTitle,
        duration = null, // APIの別エンドポイントが必要なため一旦null
        savedAt = System.currentTimeMillis()
    )
}