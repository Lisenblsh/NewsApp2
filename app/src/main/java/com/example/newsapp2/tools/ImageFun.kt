package com.example.newsapp2.tools

import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso

class ImageFun {
    fun setImage(image: String, imageView: ImageView) {
        if(image.isNotEmpty()){
            Picasso.get().load(image).into(imageView)
        }
    }

    fun setImage(image: Int, imageView: View) {
        Picasso.get().load(image).placeholder(image).into(imageView as? ImageView)
    }
}