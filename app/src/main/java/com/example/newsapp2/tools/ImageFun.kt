package com.example.newsapp2.tools

import android.view.View
import android.widget.ImageView
import com.squareup.picasso.LruCache
import com.squareup.picasso.Picasso

class ImageFun {
    private val cache = LruCache(MAX_CACHE_SIZE)

    fun setImage(image: String, view: ImageView) {
        Picasso.Builder(view.context)
            .memoryCache(cache)
            .build()
            .load(image)
            .into(view)
    }

    fun setImage(image: Int, view: View) {
        if (view is ImageView) {
            Picasso.Builder(view.context)
                .memoryCache(cache)
                .build()
                .load(image)
                .placeholder(image)
                .into(view)
        }
    }

    companion object {
        private const val MAX_CACHE_SIZE = 30 * 1024 * 1024
    }
}