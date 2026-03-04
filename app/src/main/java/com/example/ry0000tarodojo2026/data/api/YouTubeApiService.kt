package com.example.ry0000tarodojo2026.data.api

import com.example.ry0000tarodojo2026.data.model.YouTubeSearchResponse
import com.example.ry0000tarodojo2026.data.model.VideoDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * YouTube Data APIとの通信ルールを定義するインターフェース
 */
interface YouTubeApiService {

    // YouTubeの検索エンドポイントを指定
    @GET("youtube/v3/search")
    suspend fun getVideoList(
        // APIキー（Google Cloud Consoleで取得したもの）
        @Query("key") apiKey: String,

        // 検索クエリ
        @Query("q") query: String,

        // 動画の長さ（'any', 'long', 'medium', 'short' のいずれか）
        @Query("videoDuration") duration: String,

        // 取得する情報の種類（基本は "snippet" でOK）
        @Query("part") part: String = "snippet",

        // 検索対象を「動画」に限定
        @Query("type") type: String = "video",

        // 最大取得件数（デフォルトは10件）
        @Query("maxResults") maxResults: Int = 10
    ): YouTubeSearchResponse


    @GET("youtube/v3/videos")
    suspend fun getVideoDetails(
        @Query("part") part: String = "contentDetails,snippet",
        @Query("id") id: String, // カンマ区切りで複数ID指定可能
        @Query("key") apiKey: String
    ): VideoDetailsResponse


}