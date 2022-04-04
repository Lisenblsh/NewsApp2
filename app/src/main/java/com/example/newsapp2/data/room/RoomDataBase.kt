package com.example.newsapp2.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ArticlesDB::class, SourcesDB::class], version = 1, exportSchema = false)
abstract class RoomDataBase : RoomDatabase() {
    abstract fun newsListDao(): NewsDao
}

fun getDataBase(context: Context) = Room.databaseBuilder(
    context.applicationContext,
    RoomDataBase::class.java,
    "newsDB"
).allowMainThreadQueries().build()
//.fallbackToDestructiveMigration() -- для смены версии
