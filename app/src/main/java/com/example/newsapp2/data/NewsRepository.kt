package com.example.newsapp2.data

import androidx.lifecycle.MutableLiveData
import com.example.newsapp2.data.network.Articles
import com.example.newsapp2.data.network.CurrentFilter
import retrofit2.HttpException

class NewsRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    suspend fun getNews() {
        val news = MutableLiveData<Resource<List<Articles>>>()
        val response = remoteDataSource.getNews(CurrentFilter.filterForNews)
        news.postValue(Resource.Loading())
        if(response.isSuccessful){
            news.postValue(response.body()?.let {
                Resource.Success(it)
            })
        }
        else {
            news.postValue(Resource.Error(HttpException(response).message()))

        }
    }
    fun getNewsFromFavSources() {}
}