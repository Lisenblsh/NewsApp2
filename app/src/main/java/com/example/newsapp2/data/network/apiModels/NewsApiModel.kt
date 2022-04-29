package com.example.newsapp2.data.network.apiModels

data class NewsApiModel(
    val status: String,
    val totalResults: Int,
    val articles: MutableList<NewsApiArticle>
)

data class NewsApiArticle(
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String
)
