package com.example.ry0000tarodojo2026.data.repository

import com.example.ry0000tarodojo2026.data.api.YouTubeApiService

class YouTubeRepository(private val apiService: YouTubeApiService) {


    suspend fun fetchVideoIds(apiKey: String, query: String, duration: String): List<String> {
        return try {
            // APIを呼び出してレスポンスを受け取る
            val response = apiService.getVideoList(
                apiKey = apiKey,
                query = query,
                duration = duration
            )

            // 複雑なレスポンスの中から videoId だけを抜き出してリストにする
            // itemsの中に動画の情報が入っています
            response.items.map { it.id.videoId }

        } catch (e: Exception) {
            // 通信エラーや鍵が無効な場合などは、空のリストを返す
            e.printStackTrace()
            emptyList()
        }
    }
}