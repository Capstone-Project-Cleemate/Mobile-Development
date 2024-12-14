package com.dicoding.cleemate.model

data class KesehatanResponse(
    val message: String,
    val rekomendasi: List<RecommendationItem>
)

data class RecommendationItem(
    val penyakit: String,
    val rekomendasi: String
)