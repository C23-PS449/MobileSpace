package com.example.capstone.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.capstone.BuildConfig
import com.example.capstone.data.Result
import com.example.capstone.data.remote.response.ArticlesItem
import com.example.capstone.data.remote.response.NewsResponse
import com.example.capstone.data.remote.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsRepository(
    private val apiService: ApiService
) {
    private val newsresult= MediatorLiveData<Result<List<ArticlesItem>>>()
    fun getNews(): LiveData<Result<List<ArticlesItem>>>{
        newsresult.value= Result.Loading
        val client=apiService.getNews(BuildConfig.API_KEY)
        client.enqueue(object: Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if(response.isSuccessful){
                    newsresult.value= Result.Success(response.body()!!.articles)
                }else{
                    newsresult.value= Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
               newsresult.value= Result.Error(t.message.toString())
            }

        })
        return newsresult

    }
    companion object{
        @Volatile
        private var instance: NewsRepository?=null
        fun getInstance(
            apiService: ApiService
        ): NewsRepository =
            instance ?: synchronized(this){
                instance ?: NewsRepository(apiService)
            }.also{ instance =it}
    }
}
