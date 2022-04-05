package com.example.newsapp2.data.network

data class NewsModel(
    val status: String,
    val totalResults: Int,
    val articles: MutableList<Articles>
)

data class Articles(
    val source: Source,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String
)

data class Source(
    val name: String
)
