package com.example.ry0000tarodojo2026.data.repository

import com.example.ry0000tarodojo2026.data.api.YouTubeApiService
import java.time.Duration

/**
 * YouTubeの再生時間形式 (例: PT3M15S) を秒数 (Long) に変換する関数
 */
fun parseDurationToSeconds(durationString: String?): Long {
    // データが空の場合は 0 秒として返す
    if (durationString.isNullOrEmpty()) return 0L

    return try {
        // Durationクラスを使って文字列を解析し、トータルの秒数を取得
        Duration.parse(durationString).seconds
    } catch (e: Exception) {
        // 解析に失敗した（形式が違うなど）場合は安全のために 0 秒を返す
        0L
    }
}

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