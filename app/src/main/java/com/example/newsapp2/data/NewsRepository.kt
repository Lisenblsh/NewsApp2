package com.example.newsapp2.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.newsapp2.data.network.FilterForBingNews
import com.example.newsapp2.data.network.FilterForNewsApi
import com.example.newsapp2.data.network.FilterForNewscather
import com.example.newsapp2.data.network.TypeNewsUrl
import com.example.newsapp2.data.network.retrofit.RetrofitService
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.data.room.TypeArticles
import kotlinx.coroutines.flow.Flow

class NewsRepository(private val retrofitService: RetrofitService) {

    suspend fun getNewsApiResponse(filter: FilterForNewsApi) = retrofitService.getNewsApiResponse(
        filter.query,
        filter.sortBy,
        filter.searchIn,
        filter.from,
        filter.to,
        filter.domains,
        filter.language,
        filter.page,
        filter.pageSize,
        filter.excludeDomains
    )

    suspend fun getBingNewsResponse(filter: FilterForBingNews) = retrofitService.getBingNewsResponse(
        filter.query,
        filter.count,
        filter.offset,
        filter.language,
        filter.sortBy
    )

    suspend fun getNewscatcherResponse(filter: FilterForNewscather) = retrofitService.getNewscatcherResponse(
        filter.query,
        filter.language,
        filter.page,
        filter.pageSize
    )
}