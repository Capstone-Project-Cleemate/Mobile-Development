package com.dicoding.cleemate.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @field:SerializedName("resultMessage")
    val resultMessage: String,
    @field:SerializedName("diseases")
    val diseases: List<String>,
    @field:SerializedName("suggestions")
    val suggestions: List<String>,
    @field:SerializedName("kecamatan")
    val kecamatan: String,
    @field:SerializedName("cuaca")
    val cuaca: WeatherData
)

data class WeatherData(
    @field:SerializedName("lokasi")
    val lokasi: LocationData,
    @field:SerializedName("cuaca")
    val cuaca: List<List<WeatherDetail>>
)

data class LocationData(
    @field:SerializedName("provinsi")
    val provinsi: String,
    @field:SerializedName("kotkab")
    val kotkab: String,
    @field:SerializedName("kecamatan")
    val kecamatan: String,
    @field:SerializedName("desa")
    val desa: String,
    @field:SerializedName("lon")
    val lon: Double,
    @field:SerializedName("lat")
    val lat: Double
)

data class WeatherDetail(
    @field:SerializedName("datetime")
    val datetime: String,
    @field:SerializedName("t")
    val t: Double,
    @field:SerializedName("weather_desc")
    val weather_desc: String,
    @field:SerializedName("tcc")
    val tcc: Int,
    @field:SerializedName("hu")
    val hu: Int,
    @field:SerializedName("tp")
    val tp: Double,
    @field:SerializedName("image")
    val image: String
)