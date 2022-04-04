package com.example.newsapp2.data

import com.example.newsapp2.data.network.Filter
import com.example.newsapp2.data.network.retrofit.RetrofitService

class RemoteDataSource(private val retrofitService: RetrofitService) {
    suspend fun getNews(filter: Filter) = retrofitService.getNews(
        filter.newsQuery,
        filter.newsSortBy,
        filter.searchIn,
        filter.newsFrom,
        filter.newsTo,
        filter.newsDomains,
        filter.newsLanguage,
        filter.page,
        filter.pageSize,
        filter.excludeDomains
    )
}