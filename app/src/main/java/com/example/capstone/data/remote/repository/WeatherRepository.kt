package com.example.capstone.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.capstone.BuildConfig
import com.example.capstone.data.Result
import com.example.capstone.data.remote.response.WeatherResponse
import com.example.capstone.data.remote.retrofit.WeatherApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherRepository(
    private val weatherApiService: WeatherApiService
) {
    private val weatherResult= MediatorLiveData<Result<WeatherResponse>>()
    fun getWeather(latlng:String): LiveData<Result<WeatherResponse>>{
        weatherResult.value= Result.Loading
        val client=weatherApiService.getWeather(BuildConfig.Weather_API_KEY,latlng)
        client.enqueue(object: Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if(response.isSuccessful){
                    weatherResult.value= Result.Success(response.body()!!)
                }else{
                    weatherResult.value= Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                weatherResult.value= Result.Error(t.toString())
            }

        })
        return weatherResult
    }

    companion object{
        @Volatile
        private var instance: WeatherRepository?=null
        fun getInstance(
            weatherApiService: WeatherApiService
        ): WeatherRepository =
            instance ?: synchronized(this){
                instance ?: WeatherRepository(weatherApiService)
            }.also{ instance =it}
    }
}