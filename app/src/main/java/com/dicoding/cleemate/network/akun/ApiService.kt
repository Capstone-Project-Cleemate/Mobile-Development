package com.dicoding.cleemate.network.akun

import com.dicoding.cleemate.model.HealthRequest
import com.dicoding.cleemate.model.KesehatanResponse
import com.dicoding.cleemate.model.LoginResponse
import com.dicoding.cleemate.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("api/health")
    suspend fun sendHealthConditions(
        @Body conditions: HealthRequest
    ): KesehatanResponse
}