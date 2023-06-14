package com.example.capstone.DI

import android.content.Context
import com.example.capstone.data.remote.repository.DiseaseRepository
import com.example.capstone.data.remote.repository.LocationRepository
import com.example.capstone.data.remote.repository.NewsRepository
import com.example.capstone.data.remote.repository.WeatherRepository
import com.example.capstone.data.remote.retrofit.ApiConfig

object Injection {

    fun provideRepository(context: Context): NewsRepository {
        val apiService= ApiConfig.getApiService()
        return NewsRepository.getInstance(apiService)
    }
    fun provideWeatherRepository(context:Context): WeatherRepository {
        val weatherApiService=ApiConfig.getWeatherApiService()
        return WeatherRepository.getInstance(weatherApiService)
    }
    fun provideLocationRepository(context:Context): LocationRepository {
        val locationApiService=ApiConfig.getLocationApiService()
        return LocationRepository.getInstance(locationApiService)
    }
    fun provideDiseaseRepository(context:Context): DiseaseRepository{
        val diseaseApiService=ApiConfig.getDiseaseApiService()
        return DiseaseRepository.getInstance(diseaseApiService)
    }

}