package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import com.example.capstone.data.Repository
import com.example.capstone.data.WeatherRepository

class NewsViewModel(private val repo:Repository) : ViewModel() {
    fun getAllNews()=repo.getNews()
}
class WeatherViewModel(private val repo:WeatherRepository):ViewModel(){
    fun getWeather(latlng:String)=repo.getWeather(latlng)
}