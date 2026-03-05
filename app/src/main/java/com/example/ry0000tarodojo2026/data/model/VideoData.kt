package com.example.ry0000tarodojo2026.data.model

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