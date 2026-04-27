package com.example.ry0000tarodojo2026.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String, // 動画ID
    val title: String,
    val thumbnailUrl: String,
    val channelTitle: String,
    val duration: String?, // 以前話した「カップ麺の待ち時間」に合う動画選びで重要ですね
    val savedAt: Long = System.currentTimeMillis() // キャッシュの鮮度管理用
)