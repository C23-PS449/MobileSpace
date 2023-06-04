package com.example.capstone.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.capstone.DI.Injection
import com.example.capstone.data.Repository

class ViewModelFactory(private val repo:Repository): ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun<T: ViewModel> create(modelClass:Class<T>):T{
        if(modelClass.isAssignableFrom(NewsViewModel::class.java)){
            return NewsViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModelClass :"+modelClass.name)
    }

    companion object{
        @Volatile
        private var instance: ViewModelFactory?=null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this){
                instance ?: synchronized(this){
                    instance ?: ViewModelFactory(Injection.provideRepository(context))
                }.also{ instance =it}
            }
    }
}