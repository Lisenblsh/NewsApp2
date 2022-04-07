package com.example.newsapp2.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ArticlesDB::class, SourcesDB::class, RemoteKeys::class], version = 2, exportSchema = false)
abstract class NewsDataBase : RoomDatabase() {
    abstract fun newsListDao(): NewsDao

    companion object {
        @Volatile
        private var INSTANCE: NewsDataBase? = null

        fun getInstance(context: Context): NewsDataBase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                NewsDataBase::class.java, "news.db"
            ).build()
//.fallbackToDestructiveMigration() -- для смены версии
    }
}

