package com.example.capstone.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.capstone.data.remote.response.ArticlesItem
import com.example.capstone.data.remote.response.NewsResponse
import com.example.capstone.data.remote.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository(
    private val apiService: ApiService
) {
    private val newsresult= MediatorLiveData<Result<List<ArticlesItem>>>()
    private val APIKEY="f085e7c7736d466bb4489024b72c219b"
    fun getNews(): LiveData<Result<List<ArticlesItem>>>{
        newsresult.value=Result.Loading
        val client=apiService.getNews(APIKEY)
        client.enqueue(object: Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if(response.isSuccessful){
                    newsresult.value=Result.Success(response.body()!!.articles)
                }else{
                    newsresult.value=Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
               newsresult.value=Result.Error(t.message.toString())
            }

        })
        return newsresult

    }
    companion object{
        @Volatile
        private var instance:Repository?=null
        fun getInstance(
            apiService: ApiService
        ):Repository=
            instance?: synchronized(this){
                instance?:Repository(apiService)
            }.also{instance=it}
    }
}
