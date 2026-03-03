package com.example.ry0000tarodojo2026.data.model

// YouTube APIからのレスポンス全体を受け取る箱
data class YouTubeSearchResponse(
    val items: List<YouTubeSearchResult>
)

// 検索結果1件ごとのデータ
data class YouTubeSearchResult(
    val id: VideoId
)

// 動画IDが入っている部分
data class VideoId(
    val videoId: String
)