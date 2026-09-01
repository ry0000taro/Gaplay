package com.example.ry0000tarodojo2026.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ry0000tarodojo2026.data.model.VideoEntity
import com.example.ry0000tarodojo2026.data.model.ChannelEntity

// 管理するEntityとバージョンを指定
@Database(entities = [VideoEntity::class, ChannelEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    // DAOを取得するための窓口
    abstract fun videoDao(): VideoDao
    abstract fun channelDao(): ChannelDao
}