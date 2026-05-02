package com.example.ry0000tarodojo2026.di

import android.content.Context
import androidx.room.Room
import com.example.ry0000tarodojo2026.data.api.RetrofitInstance
import com.example.ry0000tarodojo2026.data.local.AppDatabase
import com.example.ry0000tarodojo2026.data.local.SearchPrefs
import com.example.ry0000tarodojo2026.data.repository.YouTubeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //データベースの作り方を教える
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "youtube-db"
        ).build()
    }

    //設定ファイルの作り方を教える
    @Provides
    @Singleton
    fun provideSearchPrefs(@ApplicationContext context: Context): SearchPrefs {
        return SearchPrefs(context)
    }

    //リポジトリの作り方を教える
    @Provides
    @Singleton
    fun provideYouTubeRepository(db: AppDatabase): YouTubeRepository {
        return YouTubeRepository(RetrofitInstance.api, db.videoDao())
    }
}
