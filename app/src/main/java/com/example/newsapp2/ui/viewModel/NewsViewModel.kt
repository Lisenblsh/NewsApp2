package com.example.newsapp2.ui.viewModel

import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.savedstate.SavedStateRegistryOwner
import com.example.newsapp2.data.NewsRepository
import com.example.newsapp2.data.network.TypeNewsUrl
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.data.room.TypeArticles
import kotlinx.coroutines.flow.Flow

class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val typeNewsUrl: TypeNewsUrl
) : ViewModel() {
    lateinit var pagingDataRegularNewsFlow: Flow<PagingData<ArticlesDB>>
    val pagingDataFavoriteNewsFlow: Flow<PagingData<ArticlesDB>>

    init {
        getCurrentNews()
        pagingDataFavoriteNewsFlow = getFavoriteNews()
    }


    fun getCurrentNews() {
        Log.e("type", "${typeNewsUrl}")
        pagingDataRegularNewsFlow = newsRepository.getNews(TypeArticles.RegularNews, typeNewsUrl)
    }

    private fun getFavoriteNews(): Flow<PagingData<ArticlesDB>> =
        newsRepository.getNews(TypeArticles.FollowNews, TypeNewsUrl.NewsApi)
}

class NewsViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val newsRepository: NewsRepository,
    private val typeNewsUrl: TypeNewsUrl
) : AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(newsRepository, typeNewsUrl) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}