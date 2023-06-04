package com.example.capstone.DI

import android.content.Context
import com.example.capstone.data.Repository
import com.example.capstone.data.remote.retrofit.ApiConfig

object Injection {

    fun provideRepository(context: Context):Repository{
        val apiService= ApiConfig.getApiService()
        return Repository.getInstance(apiService)
    }
}