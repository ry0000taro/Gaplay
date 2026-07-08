package com.example.ry0000tarodojo2026.data.model

import com.google.gson.annotations.SerializedName

data class NoodleResponse(
    @SerializedName("jan_code")
    val janCode: String,
    val name: String,
    @SerializedName("time_minutes")
    val timeMinutes: Int
)
