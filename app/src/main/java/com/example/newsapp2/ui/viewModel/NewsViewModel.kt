package com.example.newsapp2.ui.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.newsapp2.data.NewsRepository
import com.example.newsapp2.data.Resource
import com.example.newsapp2.data.network.NewsModel
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {
    var news: MutableLiveData<Resource<NewsModel>> = MutableLiveData()

    init {
        getCurrentNews()
    }

    private fun getCurrentNews() = viewModelScope.launch {
        newsRepository.getNews(news)
    }
}

class NewsViewModelFactory(private val newsRepository: NewsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            NewsViewModel(this.newsRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel is not found")
        }
    }

}