package com.example.newsapp2.data.network

data class NewsApiModel(
    val status: String,
    val totalResults: Int,
    val articles: MutableList<Articles>
)

data class Articles(
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String
)
