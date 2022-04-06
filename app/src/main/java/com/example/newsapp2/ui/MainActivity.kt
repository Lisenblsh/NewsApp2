package com.example.newsapp2.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp2.databinding.ActivityMainBinding
import com.example.newsapp2.di.Injection
import com.example.newsapp2.ui.viewModel.NewsViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        val viewModel = ViewModelProvider(
            this, Injection.provideViewModelFactory(
                this, this
            )
        ).get(NewsViewModel::class.java)
        setContentView(view)
    }
}