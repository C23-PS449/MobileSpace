package com.example.capstone.DI

import android.content.Context
import com.example.capstone.data.Repository
import com.example.capstone.data.WeatherRepository
import com.example.capstone.data.remote.retrofit.ApiConfig
import com.example.capstone.data.remote.retrofit.WeatherApiService

object Injection {

    fun provideRepository(context: Context):Repository{
        val apiService= ApiConfig.getApiService()
        return Repository.getInstance(apiService)
    }
    fun provideWeatherRepository(context:Context):WeatherRepository{
        val weatherApiService=ApiConfig.getWeatherApiService()
        return WeatherRepository.getInstance(weatherApiService)
    }

}