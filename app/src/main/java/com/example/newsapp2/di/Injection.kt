package com.example.newsapp2.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.newsapp2.data.NewsRepository
import com.example.newsapp2.data.network.retrofit.RetrofitService
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.ui.viewModel.NewsViewModelFactory

object Injection {

    private fun provideNewsRepository(context: Context): NewsRepository  {
        return NewsRepository(RetrofitService.create(), NewsDataBase.getInstance(context))
    }

    fun provideViewModelFactory(context: Context, owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return NewsViewModelFactory(owner, provideNewsRepository(context))
    }
}