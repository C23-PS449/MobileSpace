package com.example.capstone.data.remote.retrofit
import com.example.capstone.data.remote.response.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("everything?q=pertanian&sortBy=published")
    fun getNews(@Query("apiKey") apiKey: String): Call<NewsResponse>
}