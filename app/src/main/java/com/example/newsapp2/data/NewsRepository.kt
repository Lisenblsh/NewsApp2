package com.example.newsapp2.data

interface NewsRepository {
    fun getNews()
    fun getNewsFromFavSources()
}