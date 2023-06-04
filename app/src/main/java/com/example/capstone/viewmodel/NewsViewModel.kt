package com.example.capstone.viewmodel

import androidx.lifecycle.ViewModel
import com.example.capstone.data.Repository

class NewsViewModel(private val repo:Repository) : ViewModel() {
    fun getAllNews()=repo.getNews()
}