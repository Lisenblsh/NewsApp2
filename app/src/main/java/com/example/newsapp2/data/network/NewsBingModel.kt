package com.example.newsapp2.data.network

data class NewsBingModel(
    val value: List<Value>
)

data class Value(
    val datePublished: String,
    val description: String,
    val image: Image?,
    val name: String,
    val url: String
)

data class Image(
    val contentUrl: String,
)