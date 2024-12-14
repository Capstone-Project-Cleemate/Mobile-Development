package com.dicoding.cleemate.network.akun

import com.dicoding.cleemate.model.HealthRequest
import com.dicoding.cleemate.model.KesehatanResponse
import com.dicoding.cleemate.model.LoginResponse
import com.dicoding.cleemate.model.RegisterResponse
import com.dicoding.cleemate.model.WeatherResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("api/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("api/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("weather/{latitude}/{longitude}")
    suspend fun getWeatherData(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): WeatherResponse

    @POST("health")
    suspend fun sendHealthConditions(
        @Body healthRequest: HealthRequest
    ): KesehatanResponse
}