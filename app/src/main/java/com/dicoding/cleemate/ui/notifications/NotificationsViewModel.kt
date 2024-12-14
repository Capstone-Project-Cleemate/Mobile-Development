package com.dicoding.cleemate.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.cleemate.network.akun.ApiService
import kotlinx.coroutines.launch
import retrofit2.HttpException

class NotificationsViewModel(private val apiService: ApiService) : ViewModel() {

    private val _resultMessage = MutableLiveData<String>()
    val resultMessage: LiveData<String> = _resultMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val weatherResponse = apiService.getWeatherData(latitude, longitude)
                _resultMessage.value = weatherResponse.resultMessage
                _isLoading.value = false
            } catch (e: HttpException) {
                _error.value = "Network error: ${e.code()} ${e.message()}"
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "An error occurred: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}