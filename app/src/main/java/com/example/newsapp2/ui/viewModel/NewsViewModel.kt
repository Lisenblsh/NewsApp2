package com.example.newsapp2.ui.viewModel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.savedstate.SavedStateRegistryOwner
import com.example.newsapp2.data.NewsRepository
import com.example.newsapp2.data.room.ArticlesDB
import kotlinx.coroutines.flow.Flow

class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val pagingDataRegularNewsFlow: Flow<PagingData<ArticlesDB>>
    var pagingDataFavoriteNewsFlow: Flow<PagingData<ArticlesDB>>

    init {
        pagingDataFavoriteNewsFlow = getFavoriteNews()
        pagingDataRegularNewsFlow = getCurrentNews()
    }


    private fun getCurrentNews(): Flow<PagingData<ArticlesDB>> =
        newsRepository.getNews()

    private fun getFavoriteNews() : Flow<PagingData<ArticlesDB>> =
        newsRepository.getFavoriteNews()
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
            return NewsViewModel(newsRepository, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}