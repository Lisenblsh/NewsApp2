package com.example.newsapp2

import android.app.Application
import com.example.newsapp2.di.AppContainer

class MyApplication: Application() {
    val appContainer = AppContainer()
}