package com.dicoding.cleemate.network.akun

data class RegistResponse(
    val message: String,
    val userId: String? = null,
    val success: Boolean
)
