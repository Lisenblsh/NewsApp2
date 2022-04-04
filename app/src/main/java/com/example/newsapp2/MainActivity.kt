package com.example.newsapp2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.newsapp2.data.network.*
import com.example.newsapp2.data.room.TypeArticles
import com.example.newsapp2.data.room.TypeSource
import com.example.newsapp2.data.toArticlesDto

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val asd = Articles(
            Source("source"),
            "Author",
            "title",
            "description",
            "url",
            "urlToImage",
            "publeshedAt"
        )
        println(asd)
        println(asd.toArticlesDto(TypeArticles.FavoriteNews))
    }
}