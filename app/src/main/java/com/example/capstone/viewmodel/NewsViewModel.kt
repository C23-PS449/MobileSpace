package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import com.example.capstone.data.remote.repository.DiseaseRepository
import com.example.capstone.data.remote.repository.LocationRepository
import com.example.capstone.data.remote.repository.NewsRepository
import com.example.capstone.data.remote.repository.WeatherRepository
import okhttp3.MultipartBody

class NewsViewModel(private val repo: NewsRepository) : ViewModel() {
    fun getAllNews()=repo.getNews()
}
class WeatherViewModel(private val repo: WeatherRepository):ViewModel(){
    fun getWeather(latlng:String)=repo.getWeather(latlng)
}
class LocationViewModel(private val repo: LocationRepository):ViewModel(){
    fun getLocation(latlng:String)=repo.getAllStoreLocation(latlng)
}
class DiseaseViewModel(private val repo:DiseaseRepository):ViewModel(){
    fun getDisease(photo:MultipartBody.Part)=repo.getDiseaseName(photo)
}