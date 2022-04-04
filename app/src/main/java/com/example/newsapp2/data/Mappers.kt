package com.example.newsapp2.data

import com.example.newsapp2.data.network.Articles
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.data.room.TypeArticles

internal fun Articles.toArticlesDto(typeArticles: TypeArticles): ArticlesDB {
    return ArticlesDB(
        0,
        this.source.name,
        author,
        title,
        description,
        url,
        urlToImage,
        publishedAt,
        typeArticles
    )
}