package com.example.newsapp2.data.network

data class NewscatcherModel(
    val articles: List<Article>
)

data class Article(
    val author: String,
    val clean_url: String,
    val link: String,
    val media: String,
    val published_date: String,
    val summary: String,
    val title: String
)