package com.example.newsapp2.di

import com.example.newsapp2.data.NewsRepository
import com.example.newsapp2.data.RemoteDataSource
import com.example.newsapp2.data.network.retrofit.RetrofitClient
import com.example.newsapp2.data.LocalDataSource

class AppContainer {
    private val retrofit = RetrofitClient.instance

    private val localDataSource = LocalDataSource()
    private val remoteDataSource = RemoteDataSource(retrofit)

    val newsRepository = NewsRepository(localDataSource, remoteDataSource)
}