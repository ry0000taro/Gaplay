package com.example.ry0000tarodojo2026.data.model

/**
 * 動画の詳細情報（長さなど）を受け取るためのデータクラス
 */
data class VideoDetailsResponse(
    val items: List<VideoItem>
)

data class VideoItem(
    val id: String,
    val snippet: VideoSnippet,
    val contentDetails: ContentDetails,
    val statistics: Statistics
)

data class Statistics(
    val viewCount: String
)

data class VideoSnippet(
    val title: String,
    val thumbnails: Thumbnails,
    val channelTitle: String,
    val channelId: String
)

data class ContentDetails(
    // ここに PT3M15S といった形式で再生時間が入る
    val duration: String
)

// YouTubeSearchResponse と共通の場合は、共通ファイルに定義してもOK
data class Thumbnails(
    val default: ThumbnailDetails,
    val medium: ThumbnailDetails,
    val high: ThumbnailDetails
)

data class ThumbnailDetails(
    val url: String
)