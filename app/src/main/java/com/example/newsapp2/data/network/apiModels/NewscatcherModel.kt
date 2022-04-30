package com.example.newsapp2.data.network.apiModels

data class NewscatcherModel(
    val articles: List<NewscatcherArticle>
)

data class NewscatcherArticle(
    val clean_url: String,
    val link: String,
    val media: String,
    val published_date: String,
    val summary: String,
    val title: String
)