package com.example.newsapp2.data.room

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

interface NewsDao {

    //Articles
    @Query("select* from tableArticles where typeArticles = :type")
    fun getArticlesData(type: TypeArticles): List<ArticlesDB>

    @Query("select* from tableArticles where typeArticles = :type and idArticles = :idArticles")
    fun getArticlesData(type: TypeArticles, idArticles: Int): ArticlesDB

    @Query("select* from tableArticles")
    fun getArticlesData(): List<ArticlesDB>

    @Insert
    fun insertArticles(articles: ArticlesDB)

    @Delete
    fun deleteArticles(articles: ArticlesDB)

    @Query("delete from tableArticles where typeArticles = :type")
    fun deleteArticles(type: TypeArticles)

    @Query(
        "delete from tableArticles where :title = title " +
                "and :url = url and :publishedAt = publishedAt " +
                "and typeArticles = :type"
    )
    fun deleteFavoriteArticles(
        title: String,
        url: String,
        publishedAt: String,
        type: TypeArticles = TypeArticles.FavoriteNews
    )

    @Query("delete from tableArticles where typeArticles = 3 and source = :source ")
    fun deleteArticlesFromFavSources(source: String)

    @Query("delete from tableArticles where typeArticles = :type")
    fun clearArticles(type: TypeArticles)


    //Sources
    @Query("select* from tableSources where typeSource = :type")
    fun getSourcesData(type: TypeSource): List<SourcesDB>

    @Query("select* from tableSources where typeSource = :type and idSource = :idSource")
    fun getSourcesData(type: TypeSource, idSource: Int): SourcesDB

    @Query("select* from tableSources")
    fun getSourcesData(): List<SourcesDB>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSources(sources: SourcesDB)

    @Delete
    fun deleteSources(sources: SourcesDB)

    @Query("delete from tableSources where name == :source and typeSource = :type")
    fun deleteSources(source: String, type: TypeSource)

    @Query("delete from tableSources where typeSource = :type")
    fun clearSources(type: TypeSource)
}