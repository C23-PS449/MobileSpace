package com.example.capstone.data.remote.retrofit
import com.example.capstone.data.remote.response.NewsResponse
import com.example.capstone.data.remote.response.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("everything?q=pertanian&sortBy=published")
    fun getNews(@Query("apiKey") apiKey: String): Call<NewsResponse>
}
interface WeatherApiService{
    @GET("current.json")
    fun getWeather(@Query("key") apiKey:String,
    @Query("q") latlng:String):Call<WeatherResponse>
}