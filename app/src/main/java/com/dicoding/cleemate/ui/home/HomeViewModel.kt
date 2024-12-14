package com.dicoding.cleemate.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.cleemate.model.KesehatanResponse
import com.dicoding.cleemate.model.WeatherResponse
import com.dicoding.cleemate.network.akun.ApiConfig
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> = _weatherData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val apiService = ApiConfig.getApiService()

    fun fetchWeatherData(latitude: Double, longitude: Double) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.getWeatherData(latitude, longitude)
                _weatherData.value = response
                Log.d("WeatherData", "Berhasil: ${response.cuaca.lokasi.desa}")
            } catch (e: Exception) {
                Log.e("WeatherData", "Gagal mengambil data: ${e.message}")
                // Tambahkan toast atau error handling
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateHealthConditions(kesehatanResponse: KesehatanResponse) {
        // Jika API mengembalikan data kesehatan, update UI
        if (kesehatanResponse.message.isNotEmpty()) {
            // Update di SharedPreferences atau langsung di TextView
        }
    }
}