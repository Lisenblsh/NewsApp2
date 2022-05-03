package com.example.newsapp2.tools

import android.util.Log
import androidx.room.withTransaction
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.data.room.TypeArticles
import com.example.newsapp2.data.room.TypeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseFun(private val dataBase: NewsDataBase) {
    suspend fun getLikedArticlesList(): List<ArticlesDB> {
        return dataBase.withTransaction {
            dataBase.newsListDao().getArticlesDataLiked(TypeArticles.LikedNews).ifEmpty {
                Log.e("empty", "isEmpty")
                emptyList()
            }
        }
    }

    suspend fun deleteLikedArticle(article: ArticlesDB) {
        dataBase.withTransaction {
            dataBase.newsListDao().deleteLikedArticle(article)
        }
    }

    suspend fun getSource(typeSource: TypeSource): List<String> {
        return dataBase.withTransaction {
            val list = dataBase.newsListDao().getSourcesData(typeSource)
                .ifEmpty { return@withTransaction emptyList() }
            return@withTransaction list.map { it.name }
        }
    }

    suspend fun deleteSource(type: TypeSource, list: List<String>) {
        withContext(Dispatchers.IO) {
            dataBase.withTransaction {
                list.forEach {
                    dataBase.newsListDao().deleteSources(it, type)
                }
            }
        }
    }

    suspend fun getNewsDomains(): String =
        dataBase.newsListDao().getSourcesData(TypeSource.FollowSource)
            .joinToString(",", transform = { it.name }).ifBlank { "0" }

    suspend fun getExcludeDomains(): String =
        dataBase.newsListDao().getSourcesData(TypeSource.BlockSource)
            .joinToString(",", transform = { it.name })
}