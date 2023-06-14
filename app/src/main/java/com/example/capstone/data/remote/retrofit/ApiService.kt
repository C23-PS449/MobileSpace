package com.example.capstone.data.remote.retrofit
import com.example.capstone.data.remote.response.DiseaseResponse
import com.example.capstone.data.remote.response.LocationResponse
import com.example.capstone.data.remote.response.NewsResponse
import com.example.capstone.data.remote.response.WeatherResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

interface LocationApiService{
    @GET("json")
    fun getShop(@Query("location") location:String,
                @Query("keyword") keyword:String,
                @Query("rankby") rankby:String,
    @Query("key") apiKey:String):Call<LocationResponse>
}

interface DiseaseApiService{
    @Multipart
    @POST("predict")
    fun getDisease(@Part file:MultipartBody.Part):Call<DiseaseResponse>
}