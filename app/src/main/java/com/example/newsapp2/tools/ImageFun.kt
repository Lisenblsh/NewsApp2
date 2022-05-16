package com.example.newsapp2.tools

import android.view.View
import android.widget.ImageView
import coil.load

class ImageFun {
    fun setImage(image: String, view: ImageView) {
        view.load(image)
    }

    fun setImage(image: Int, view: View) {
        (view as? ImageView)?.load(image)
    }
}