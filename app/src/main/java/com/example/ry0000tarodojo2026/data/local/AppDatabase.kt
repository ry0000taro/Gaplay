package com.example.ry0000tarodojo2026.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ry0000tarodojo2026.data.model.VideoEntity

// 管理するEntityとバージョンを指定
@Database(entities = [VideoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    // DAOを取得するための窓口
    abstract fun videoDao(): VideoDao
}