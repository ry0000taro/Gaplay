package com.example.ry0000tarodojo2026.data.repository

import com.example.ry0000tarodojo2026.data.api.NoodleApiService
import com.example.ry0000tarodojo2026.data.model.NoodleResponse
import javax.inject.Inject

class NoodleRepository @Inject constructor(
    private val apiService: NoodleApiService
) {
    suspend fun getNoodleInfo(janCode: String): Result<NoodleResponse> {
        return try {
            val response = apiService.getNoodleInfo(janCode)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
