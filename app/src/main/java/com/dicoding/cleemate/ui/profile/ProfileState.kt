package com.dicoding.cleemate.ui.profile

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val name: String, val email: String) : ProfileState()
    data class Error(val message: String) : ProfileState()
    object Logout : ProfileState()
}