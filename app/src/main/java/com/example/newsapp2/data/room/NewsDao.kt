package com.example.newsapp2.data.room

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface NewsDao {
    //Articles
    //Добавить новости в БД
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllArticles(articles: List<ArticlesDB>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticlesDB)

    //ПОлучить список новостей
    @Query("select* from articles where typeArticles = :type")
    fun getArticlesData(type: TypeArticles): PagingSource<Int, ArticlesDB>

    //ПОлучить список новостей
    @Query("select* from articles where typeArticles = :type")
    fun getArticlesData2(type: TypeArticles): List<ArticlesDB>

    //Получить новость по ID
    @Query("select* from articles where typeArticles = :type and idArticles = :idArticles")
    fun getArticlesData(type: TypeArticles, idArticles: Long): ArticlesDB
    //Получить новость по ID
    @Query("select* from articles where idArticles = :idArticles")
    suspend fun getArticlesData(idArticles: Long): ArticlesDB

    @Query("select* from articles where source = :source and :url = url " +
            "and :publishedAt = publishedAt and :typeArticles = typeArticles")
    suspend fun getArticlesData(
        source: String,
        url: String,
        publishedAt: String,
        typeArticles: TypeArticles
    ): ArticlesDB?

    //Удалить одну новость из списка избраных
    @Query("delete from articles where :source = source and :url = url " +
            "and :publishedAt = publishedAt and :typeArticles = typeArticles")
    suspend fun deleteLikedArticle(
        source: String,
        url: String,
        publishedAt: String,
        typeArticles: TypeArticles
    )

    @Delete
    suspend fun deleteLikedArticle(article: ArticlesDB)

    //Отчистка Новостей
    @Query("delete from articles where typeArticles = :type")
    suspend fun clearArticles(type: TypeArticles)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKeys(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE articleId = :newsId")
    suspend fun remoteKeysNewsId(newsId: Long): RemoteKeys?

    @Query("DELETE FROM remote_keys where typeArticles = :type")
    suspend fun clearRemoteKeys(type: TypeArticles)




    //Sources
    @Query("select* from tableSources where typeSource = :type")
    suspend fun getSourcesData(type: TypeSource): List<SourcesDB>

    @Query("select* from tableSources where typeSource = :type and name = :source")
    fun getSourcesData(source: String, type: TypeSource): SourcesDB?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSources(sources: SourcesDB)

    @Delete
    fun deleteSources(sources: SourcesDB)

    @Query("delete from tableSources where name == :source and typeSource = :type")
    suspend fun deleteSources(source: String, type: TypeSource)

    @Query("delete from tableSources where typeSource = :type")
    fun clearSources(type: TypeSource)
}