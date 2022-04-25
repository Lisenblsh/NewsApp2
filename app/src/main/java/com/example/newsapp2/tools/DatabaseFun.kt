package com.example.newsapp2.tools

import android.util.Log
import androidx.room.withTransaction
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.data.room.TypeArticles
import com.example.newsapp2.data.room.TypeSource

class DatabaseFun(private val dataBase: NewsDataBase) {
    suspend fun getLikedArticlesList(): List<ArticlesDB> {
        return dataBase.withTransaction {
            dataBase.newsListDao().getArticlesData2(TypeArticles.LikedNews).ifEmpty {
                Log.e("empty", "isEmpty")
                emptyList()
            }
        }
    }

    suspend fun deleteLikedArticle(article: ArticlesDB){
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
        dataBase.withTransaction {
            list.forEach {
                dataBase.newsListDao().deleteSources(it, type)
            }
        }
    }
}