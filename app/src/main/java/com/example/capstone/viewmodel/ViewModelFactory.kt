package com.example.capstone.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.capstone.DI.Injection
import com.example.capstone.data.remote.repository.DiseaseRepository
import com.example.capstone.data.remote.repository.LocationRepository
import com.example.capstone.data.remote.repository.NewsRepository
import com.example.capstone.data.remote.repository.WeatherRepository

class ViewModelFactory(private val repo: NewsRepository): ViewModelProvider.NewInstanceFactory(){
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
class WeatherViewModelFactory(private val repo: WeatherRepository):ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun<T: ViewModel> create(modelClass:Class<T>):T{
        if(modelClass.isAssignableFrom(WeatherViewModel::class.java)){
            return WeatherViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModelClass :"+modelClass.name)
    }

    companion object{
        @Volatile
        private var instance: WeatherViewModelFactory?=null
        fun getInstance(context: Context): WeatherViewModelFactory =
            instance ?: synchronized(this){
                instance ?: synchronized(this){
                    instance ?: WeatherViewModelFactory(Injection.provideWeatherRepository(context))
                }.also{ instance =it}
            }
    }
}
class LocationViewModelFactory(private val repo: LocationRepository):ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun<T: ViewModel> create(modelClass:Class<T>):T{
        if(modelClass.isAssignableFrom(LocationViewModel::class.java)){
            return LocationViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModelClass :"+modelClass.name)
    }

    companion object{
        @Volatile
        private var instance: LocationViewModelFactory?=null
        fun getInstance(context: Context): LocationViewModelFactory =
            instance ?: synchronized(this){
                instance ?: synchronized(this){
                    instance ?: LocationViewModelFactory(Injection.provideLocationRepository(context))
                }.also{ instance =it}
            }
    }
}
class DiseaseViewModelFactory(private val repo:DiseaseRepository):ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun<T: ViewModel> create(modelClass:Class<T>):T{
        if(modelClass.isAssignableFrom(DiseaseViewModel::class.java)){
            return DiseaseViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModelClass :"+modelClass.name)
    }
    companion object{
        @Volatile
        private var instance: DiseaseViewModelFactory?=null
        fun getInstance(context: Context): DiseaseViewModelFactory =
            instance ?: synchronized(this){
                instance ?: synchronized(this){
                    instance ?: DiseaseViewModelFactory(Injection.provideDiseaseRepository(context))
                }.also{ instance =it}
            }
    }
}