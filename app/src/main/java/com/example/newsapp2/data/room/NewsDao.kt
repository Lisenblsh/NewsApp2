package com.example.newsapp2.data.room

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface NewsDao {
    //Articles
    //Добавить новости в БД
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllArticles(articles: List<ArticlesDB>)

    //ПОлучить список новостей
    @Query("select* from articles where typeArticles = :type")
    fun getArticlesData(type: TypeArticles): PagingSource<Int, ArticlesDB>

    //ПОлучить список новостей
    @Query("select* from articles where typeArticles = :type")
    fun getArticlesData2(type: TypeArticles): List<ArticlesDB>

    //Получить новость по ID
    @Query("select* from articles where typeArticles = :type and idArticles = :idArticles")
    fun getArticlesData(type: TypeArticles, idArticles: Int): ArticlesDB

    //Удалить одну новость из списка избраных
    @Query(
        "delete from articles where :title = title " +
                "and :url = url and :publishedAt = publishedAt " +
                "and typeArticles = :type"
    )
    fun deleteFavoriteArticles(
        title: String,
        url: String,
        publishedAt: String,
        type: TypeArticles = TypeArticles.FavoriteNews
    )

    //Отчистка Новостей
    @Query("delete from articles where typeArticles = :type")
    suspend fun clearArticles(type: TypeArticles)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKeys(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE newsId = :newsId")
    suspend fun remoteKeysNewsId(newsId: Long): RemoteKeys?

    @Query("DELETE FROM remote_keys where typeArticles = :type")
    suspend fun clearRemoteKeys(type: TypeArticles)




    //Sources
    @Query("select* from tableSources where typeSource = :type")
    suspend fun getSourcesData(type: TypeSource): List<SourcesDB>

    @Query("select* from tableSources where typeSource = :type and idSource = :idSource")
    fun getSourcesData(type: TypeSource, idSource: Int): SourcesDB

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSources(sources: SourcesDB)

    @Delete
    fun deleteSources(sources: SourcesDB)

    @Query("delete from tableSources where name == :source and typeSource = :type")
    fun deleteSources(source: String, type: TypeSource)

    @Query("delete from tableSources where typeSource = :type")
    fun clearSources(type: TypeSource)
}