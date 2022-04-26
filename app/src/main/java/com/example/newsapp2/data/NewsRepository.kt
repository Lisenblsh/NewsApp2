package com.example.newsapp2.data

import com.example.newsapp2.data.network.FilterForBingNews
import com.example.newsapp2.data.network.FilterForNewsApi
import com.example.newsapp2.data.network.FilterForNewscather
import com.example.newsapp2.data.network.retrofit.RetrofitService

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