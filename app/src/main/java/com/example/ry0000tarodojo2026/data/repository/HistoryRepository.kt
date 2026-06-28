package com.example.ry0000tarodojo2026.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class HistoryRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun saveWatchHistory(
        videoId: String, 
        videoTitle: String, 
        videoDurationSeconds: Long,
        exerciseDurationSeconds: Long,
        totalDurationSeconds: Long
    ) {
        val uid = auth.currentUser?.uid ?: return
        
        val historyData = hashMapOf(
            "videoId" to videoId,
            "title" to videoTitle,
            "videoDurationSeconds" to videoDurationSeconds,
            "exerciseDurationSeconds" to exerciseDurationSeconds,
            "totalDurationSeconds" to totalDurationSeconds,
            "savedAt" to System.currentTimeMillis()
        )

        db.collection("users").document(uid).collection("watch_history")
            .add(historyData)
    }
}
