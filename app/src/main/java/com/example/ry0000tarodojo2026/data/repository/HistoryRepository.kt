package com.example.ry0000tarodojo2026.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    suspend fun saveWatchHistory(
        videoId: String, 
        videoTitle: String, 
        videoDurationSeconds: Long,
        exerciseDurationSeconds: Long,
        totalDurationSeconds: Long,
        exerciseType: String
    ) {
        val uid = auth.currentUser?.uid ?: return
        
        val historyData = hashMapOf(
            "videoId" to videoId,
            "title" to videoTitle,
            "videoDurationSeconds" to videoDurationSeconds,
            "exerciseDurationSeconds" to exerciseDurationSeconds,
            "totalDurationSeconds" to totalDurationSeconds,
            "exerciseType" to exerciseType,
            "savedAt" to FieldValue.serverTimestamp()
        )

        try {
            db.collection("users").document(uid).collection("watch_history")
                .add(historyData)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
