package com.example.ry0000tarodojo2026.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ry0000tarodojo2026.data.model.ChannelEntity

@Dao
interface ChannelDao {
    @Query("SELECT * FROM channels WHERE channelId = :id LIMIT 1")
    suspend fun getChannelById(id: String): ChannelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: ChannelEntity)
}