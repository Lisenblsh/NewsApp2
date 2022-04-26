package com.example.newsapp2.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.newsapp2.data.NewsRepository
import com.example.newsapp2.data.network.TypeNewsUrl
import com.example.newsapp2.data.network.retrofit.RetrofitService
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.ui.viewModel.NewsViewModelFactory

object Injection {
    private fun provideNewsRepository(context: Context, typeNewsUrl: TypeNewsUrl): NewsRepository {
        return NewsRepository(RetrofitService.create(typeNewsUrl))
    }

    fun provideViewModelFactory(
        context: Context,
        owner: SavedStateRegistryOwner,
        typeNewsUrl: TypeNewsUrl
    ): ViewModelProvider.Factory {
        return NewsViewModelFactory(
            owner,
            provideNewsRepository(context, typeNewsUrl),
            typeNewsUrl,
            NewsDataBase.getInstance(context)
        )
    }
}