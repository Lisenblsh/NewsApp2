package com.example.newsapp2.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.newsapp2.data.network.retrofit.RetrofitService
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.data.room.TypeArticles
import kotlinx.coroutines.flow.Flow

class NewsRepository(
    private val retrofitService: RetrofitService,
    private val dataBase: NewsDataBase
) {
    fun getNews(): Flow<PagingData<ArticlesDB>> {
        val pagingSourceFactory = {dataBase.newsListDao().getArticlesData(TypeArticles.RegularNews)}

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = NewsRemoteMediator(
                retrofitService,
                dataBase,
                TypeArticles.RegularNews
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }

}