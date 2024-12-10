package com.dicoding.cleemate.network.weather

import com.dicoding.cleemate.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("prakiraan-cuaca")
    suspend fun getCuaca(
        @Query("adm4") kodeWilayah: String
    ): WeatherResponse
}