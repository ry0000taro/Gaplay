package com.example.ry0000tarodojo2026.data.repository

import com.example.ry0000tarodojo2026.data.api.YouTubeApiService
import com.example.ry0000tarodojo2026.data.model.VideoData
import java.time.Duration
import android.util.Log
class YouTubeRepository(private val apiService: YouTubeApiService) {

    /**
     * 指定したキーワードで検索し、指定秒数以内の動画だけを返す
     */
    suspend fun getVideosWithinDuration(
        apiKey: String,
        query: String,
        maxDurationSeconds: Long
    ): List<VideoData> {
        return try {
            // 1. 検索結果（4分以内）の取得
            val searchResponse = apiService.getVideoList(apiKey, query, "short")
            val videoIds = searchResponse.items.map { it.id.videoId }.joinToString(",")

            Log.d("YouTubeTest", "検索ヒット件数: ${searchResponse.items.size}件")

            if (videoIds.isEmpty()) return emptyList()

            // 2. 詳細情報の取得
            val detailsResponse = apiService.getVideoDetails(id = videoIds, apiKey = apiKey)

            // 3. フィルタリングの実行
            val filteredList = detailsResponse.items.filter { item ->
                val seconds = parseDurationToSeconds(item.contentDetails.duration)

                Log.d("YouTubeTest", "動画ID: ${item.id} | 長さ: ${item.contentDetails.duration} (${seconds}秒)")

                seconds <= maxDurationSeconds
            }

            Log.d("YouTubeTest", "フィルタリング後の件数 (${maxDurationSeconds}秒以内): ${filteredList.size}件")

            filteredList.map { item ->
                VideoData(
                    id = item.id,
                    thumbnailUrl = item.snippet.thumbnails.high.url,
                    videoTitle = item.snippet.title,
                    channelName = item.snippet.channelTitle,
                    viewCount = formatViewCount(item.statistics.viewCount), // 視聴回数を整形して追加
                    duration = formatDurationString(item.contentDetails.duration) ,// "3:15" 形式に変換
                    channelId = item.snippet.channelId
                )
            }

        } catch (e: Exception) {
            Log.e("YouTubeTest", "エラー発生", e)
            emptyList()
        }
    }

    /**
     * YouTubeの再生時間形式 (例: PT3M15S) を秒数 (Long) に変換する関数
     */
    private fun parseDurationToSeconds(durationString: String?): Long {
        if (durationString.isNullOrEmpty()) return 0L
        return try {
            Duration.parse(durationString).seconds
        } catch (e: Exception) {
            0L
        }
    }

    // 以前使っていた関数も残しておく場合はここに入れる
    suspend fun fetchVideoIds(apiKey: String, query: String, duration: String): List<String> {
        return try {
            val response = apiService.getVideoList(apiKey, query, duration)
            response.items.map { it.id.videoId }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}


/*
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

 */