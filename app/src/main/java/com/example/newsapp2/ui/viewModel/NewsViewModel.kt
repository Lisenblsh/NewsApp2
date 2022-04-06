package com.example.newsapp2.ui.viewModel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.savedstate.SavedStateRegistryOwner
import com.example.newsapp2.data.NewsRepository
import com.example.newsapp2.data.Resource
import com.example.newsapp2.data.network.NewsModel
import com.example.newsapp2.data.room.ArticlesDB
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val pagingDataFlow: Flow<PagingData<ArticlesDB>>

    init {
        pagingDataFlow = getCurrentNews().cachedIn(viewModelScope)

    }

    private fun getCurrentNews(): Flow<PagingData<ArticlesDB>> =
        newsRepository.getNews()
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

sealed class UiModel {
    data class NewsItem(val news: ArticlesDB) : UiModel()
}