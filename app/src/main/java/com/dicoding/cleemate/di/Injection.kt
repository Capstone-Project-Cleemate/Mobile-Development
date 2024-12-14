package com.dicoding.cleemate.di

import android.content.Context
import com.dicoding.cleemate.network.akun.ApiConfig
import com.dicoding.cleemate.pref.UserPreference
import com.dicoding.cleemate.pref.dataStore
import com.dicoding.cleemate.repository.UserRepository

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }
}