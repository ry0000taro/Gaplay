package com.example.ry0000tarodojo2026.di

import android.content.Context
import androidx.room.Room
import com.example.ry0000tarodojo2026.data.api.RetrofitInstance
import com.example.ry0000tarodojo2026.data.local.AppDatabase
import com.example.ry0000tarodojo2026.data.local.SearchPrefs
import com.example.ry0000tarodojo2026.data.repository.YouTubeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.example.ry0000tarodojo2026.data.api.NoodleApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.ry0000tarodojo2026.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoodleApiService(): NoodleApiService {
        val client = OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                addInterceptor(logging)
            }
        }.build()

        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NoodleApiService::class.java)
    }

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

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}
