package com.example.newsapp2.data

import com.example.newsapp2.MyApplication
import com.example.newsapp2.data.network.Articles
import com.example.newsapp2.data.network.NewsModel
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.data.room.getDataBase

class LocalDataSource {
    suspend fun getNews(): NewsModel {
        val newsDB = getDataBase(MyApplication().applicationContext).newsListDao()
        val articlesList: List<ArticlesDB> = newsDB.getArticlesData()
        val list = arrayListOf<Articles>().also { list ->
            articlesList.forEach {
                list.add(it.toArticles())
            } to listOf<Articles>()
        }
        return NewsModel("", 0, list)
    }
}