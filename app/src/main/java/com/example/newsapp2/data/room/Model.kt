package com.example.newsapp2.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticlesDB(
    @PrimaryKey(autoGenerate = true) val idArticles: Long = 0,
    val source: String,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val typeArticles: TypeArticles
)

enum class TypeArticles(val type: Int) {
    RegularNews(1),
    FavoriteNews(2),
    NewsFromFavoriteSource(3);
}

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val newsId: Long,
    val prevKey: Int?,
    val nextKey: Int?,
    val typeArticles: TypeArticles
)

/*Типы:
* 1 - Обычные новости
* 2 - Избраные нововсти
* 3 - Новости с избраных иточников */

@Entity(tableName = "tableSources")
data class SourcesDB(
    @PrimaryKey(autoGenerate = true) val idSource: Int = 0,
    val name: String,
    val typeSource: TypeSource
)

enum class TypeSource(val typeId: Int, val typeName: String) {
    FavoriteSource(1, "Избраные источники"),
    RemovedSource(2, "Скрытые источники");
}

/*Типы:
* 1 - Избраные источники
* 2 - Скрытые источники*/