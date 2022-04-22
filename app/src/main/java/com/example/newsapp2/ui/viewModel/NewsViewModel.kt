package com.example.newsapp2.ui.viewModel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.savedstate.SavedStateRegistryOwner
import com.example.newsapp2.data.NewsRepository
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.data.room.TypeArticles
import kotlinx.coroutines.flow.Flow

class NewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {
    val pagingDataRegularNewsFlow: Flow<PagingData<ArticlesDB>>
    val pagingDataFavoriteNewsFlow: Flow<PagingData<ArticlesDB>>

    init {
        pagingDataRegularNewsFlow = getCurrentNews()
        pagingDataFavoriteNewsFlow = getFavoriteNews()
    }


    private fun getCurrentNews(): Flow<PagingData<ArticlesDB>> =
        newsRepository.getNews(TypeArticles.RegularNews)

    private fun getFavoriteNews(): Flow<PagingData<ArticlesDB>> =
        newsRepository.getNews(TypeArticles.FollowNews)
}

class NewsViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val newsRepository: NewsRepository
) : AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(newsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}