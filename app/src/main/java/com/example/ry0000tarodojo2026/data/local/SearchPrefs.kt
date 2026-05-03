package com.example.ry0000tarodojo2026.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Contextの拡張プロパティとしてDataStoreを定義
private val Context.dataStore by preferencesDataStore(name = "search_prefs")

class SearchPrefs(private val context: Context) {
    // 保存するためのキー（合言葉）を決める
    private val KEY_QUERY = stringPreferencesKey("last_query")
    private val KEY_MINUTES = stringPreferencesKey("last_minutes")
    private val KEY_EXERCISE_TYPE = stringPreferencesKey("last_exercise_type")

    // 読み込み（データが流れるパイプ）
    val lastQuery: Flow<String> = context.dataStore.data.map { it[KEY_QUERY] ?: "カップ麺" }
    val lastMinutes: Flow<String> = context.dataStore.data.map { it[KEY_MINUTES] ?: "3" }
    val lastExerciseType: Flow<String> = context.dataStore.data.map { it[KEY_EXERCISE_TYPE] ?: "選択なし" }

    // 保存（書き込み処理）
    suspend fun saveSearchConditions(query: String, minutes: String, exerciseType: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_QUERY] = query
            prefs[KEY_MINUTES] = minutes
            prefs[KEY_EXERCISE_TYPE] = exerciseType
        }
    }
}