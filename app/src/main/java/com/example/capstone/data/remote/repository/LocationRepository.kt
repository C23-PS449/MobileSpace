package com.example.capstone.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.capstone.BuildConfig
import com.example.capstone.data.Result
import com.example.capstone.data.remote.response.LocationResponse
import com.example.capstone.data.remote.response.ResultsItem
import com.example.capstone.data.remote.retrofit.LocationApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationRepository(
    private val locationApiService: LocationApiService
) {
    private val locationresult= MediatorLiveData<Result<List<ResultsItem>>>()
    fun getAllStoreLocation(location:String): LiveData<Result<List<ResultsItem>>> {
        locationresult.value= Result.Loading
        val client=locationApiService.getShop(location,"Tanaman|Pupuk|Tani","distance",
            BuildConfig.Google_API_KEY)
        client.enqueue(object: Callback<LocationResponse>{
            override fun onResponse(
                call: Call<LocationResponse>,
                response: Response<LocationResponse>
            ) {
                if(response.isSuccessful){
                    locationresult.value= Result.Success(response.body()!!.results)
                }else{
                    locationresult.value= Result.Error(response.body()!!.status)
                }
            }

            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                locationresult.value= Result.Error(t.message.toString())
            }


        })
        return locationresult
    }

    companion object{
        @Volatile
        private var instance: LocationRepository?=null
        fun getInstance(
            locationApiService: LocationApiService
        ): LocationRepository =
            instance ?: synchronized(this){
                instance ?: LocationRepository(locationApiService)
            }.also{ instance =it}
    }
}