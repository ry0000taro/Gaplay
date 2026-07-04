package com.example.ry0000tarodojo2026.data.api

import com.example.ry0000tarodojo2026.data.model.NoodleResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface NoodleApiService {
    @GET("api/noodles/{jan_code}")
    suspend fun getNoodleInfo(@Path("jan_code") janCode: String): NoodleResponse
}
