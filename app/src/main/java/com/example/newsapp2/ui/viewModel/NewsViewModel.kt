package com.example.newsapp2.ui.viewModel

import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.savedstate.SavedStateRegistryOwner
import com.example.newsapp2.data.NewsRemoteMediator
import com.example.newsapp2.data.NewsRepository
import com.example.newsapp2.data.network.TypeNewsUrl
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.data.room.TypeArticles
import kotlinx.coroutines.flow.Flow

class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val typeNewsUrl: TypeNewsUrl,
    private val dataBase: NewsDataBase
) : ViewModel() {
    lateinit var pagingDataRegularNewsFlow: Flow<PagingData<ArticlesDB>>
    val pagingDataFavoriteNewsFlow: Flow<PagingData<ArticlesDB>>

    init {
        getCurrentNews()
        pagingDataFavoriteNewsFlow = getFavoriteNews()
    }


    fun getCurrentNews() {
        Log.e("type", "${typeNewsUrl}")
        pagingDataRegularNewsFlow = getNews(TypeArticles.RegularNews, typeNewsUrl)
    }

    private fun getFavoriteNews(): Flow<PagingData<ArticlesDB>> =
        getNews(TypeArticles.FollowNews, TypeNewsUrl.NewsApi)

    fun getNews(
        typeArticles: TypeArticles,
        typeNewsUrl: TypeNewsUrl
    ): Flow<PagingData<ArticlesDB>> {
        val pagingSourceFactory =
            { dataBase.newsListDao().getArticlesData(typeArticles, typeNewsUrl) }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = if (typeNewsUrl == TypeNewsUrl.StopGame) 30 else NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = INITIAL_LOAD_SIZE
            ),
            remoteMediator = NewsRemoteMediator(
                newsRepository,
                dataBase,
                typeArticles,
                typeNewsUrl
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
        const val INITIAL_LOAD_SIZE = 3
    }
}

class NewsViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val newsRepository: NewsRepository,
    private val typeNewsUrl: TypeNewsUrl,
    private val dataBase: NewsDataBase
) : AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(newsRepository, typeNewsUrl, dataBase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}