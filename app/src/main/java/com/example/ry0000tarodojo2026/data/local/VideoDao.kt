package com.example.ry0000tarodojo2026.data.local


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ry0000tarodojo2026.data.model.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    // 保存された動画を、保存日時が新しい順に全て取得する
    // Flowを使うことで、DBが更新されるとUIも自動的に書き換わります
    @Query("SELECT * FROM videos ORDER BY savedAt DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    // リストをまるごと保存する（IDが重複していたら上書き）
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    // 古いキャッシュを一度削除する
    @Query("DELETE FROM videos")
    suspend fun clearAll()

}