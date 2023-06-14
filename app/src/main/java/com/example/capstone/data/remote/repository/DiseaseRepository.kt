package com.example.capstone.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.capstone.data.Result
import com.example.capstone.data.remote.response.DiseaseResponse
import com.example.capstone.data.remote.retrofit.DiseaseApiService
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiseaseRepository (
    private val diseaseApiService:DiseaseApiService
){
    private val diseaseresult= MediatorLiveData<Result<DiseaseResponse>>()

    fun getDiseaseName(imageMultipart:MultipartBody.Part): LiveData<Result<DiseaseResponse>> {
        diseaseresult.value=Result.Loading
        val client=diseaseApiService.getDisease(imageMultipart)
        client.enqueue(object:Callback<DiseaseResponse>{
            override fun onResponse(
                call: Call<DiseaseResponse>,
                response: Response<DiseaseResponse>
            ) {
               if(response.isSuccessful){
                   diseaseresult.value=Result.Success(response.body()!!)
               }
                else{
                    diseaseresult.value=Result.Error(response.body()!!.toString())
               }
            }

            override fun onFailure(call: Call<DiseaseResponse>, t: Throwable) {
                diseaseresult.value=Result.Error(t.message.toString())
            }

        })
        return diseaseresult
    }

    companion object{
        @Volatile
        private var instance:DiseaseRepository?=null
        fun getInstance(
            diseaseApiService: DiseaseApiService
        ):DiseaseRepository=
            instance ?: synchronized(this){
                instance ?: synchronized(this){
                    instance ?: DiseaseRepository(diseaseApiService)
                }.also{instance =it}
            }
    }
}