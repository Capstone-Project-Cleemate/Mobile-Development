package com.dicoding.cleemate.ui.kesehatan

import com.dicoding.cleemate.model.HealthRequest
import com.dicoding.cleemate.model.KesehatanResponse
import com.dicoding.cleemate.network.akun.ApiConfig

class KesehatanViewModel {
    private val apiService = ApiConfig.getApiService()

    suspend fun sendHealthConditions(conditions: List<String>): Result<KesehatanResponse> {
        return try {
            val response = apiService.sendHealthConditions(HealthRequest(conditions))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}