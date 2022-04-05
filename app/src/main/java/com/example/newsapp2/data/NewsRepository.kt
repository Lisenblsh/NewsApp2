package com.example.newsapp2.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.newsapp2.data.network.CurrentFilter
import com.example.newsapp2.data.network.NewsModel
import retrofit2.HttpException

class NewsRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    suspend fun getNews(news: MutableLiveData<Resource<NewsModel>>) {
        try {
            news.postValue(Resource.Loading())
            val response = remoteDataSource.getNews(CurrentFilter.filterForNews)
            if (response.isSuccessful) {
                news.postValue(response.body()?.let {
                    Log.e("news", "${it.articles[0].author}")
                    Resource.Success(it)
                })
            } else {
                val responseDB = localDataSource.getNews()
                if (responseDB.articles.isNotEmpty()) {
                    news.postValue(Resource.DataBase(responseDB, HttpException(response).message()))
                } else {
                    news.postValue(Resource.Error(HttpException(response).message()))
                }
            }
        } catch (e: HttpException) {
            news.postValue(Resource.Error("${e.message}"))
        } catch (e: Exception) {
            news.postValue(Resource.Error("${e.message}"))
        }
    }

    fun getNewsFromFavSources() {}
}