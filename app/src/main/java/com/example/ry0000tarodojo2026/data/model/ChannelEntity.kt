package com.example.ry0000tarodojo2026.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey val channelId: String,
    val iconUrl: String,
    val savedAt: Long = System.currentTimeMillis()
)
