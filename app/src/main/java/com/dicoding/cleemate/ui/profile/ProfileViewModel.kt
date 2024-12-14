package com.dicoding.cleemate.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.cleemate.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                userRepository.getSession().collect { userModel ->
                    _profileState.value = ProfileState.Success(
                        name = userModel.email.split("@")[0], // Extracting name from email
                        email = userModel.email
                    )
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error("Gagal memuat profil: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _profileState.value = ProfileState.Logout
        }
    }
}