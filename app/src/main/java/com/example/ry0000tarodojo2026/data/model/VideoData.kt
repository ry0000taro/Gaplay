package com.example.ry0000tarodojo2026.data.model

/**
 * 画面（UI）に表示するために最適化された動画データ
 */
data class VideoData(
    val id: Int,             // リスト内での識別用
    val thumbnailUrl: String, // サムネイル画像のURL
    val videoTitle: String,   // 動画のタイトル
    val channelName: String,  // チャンネル名
    val formattedDuration: String = "" // "3:15" のように整形した再生時間（後で使います！）
)