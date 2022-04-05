package com.example.newsapp2.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import com.example.newsapp2.R
import com.example.newsapp2.data.LocalDataSource
import com.example.newsapp2.data.NewsRepository
import com.example.newsapp2.data.RemoteDataSource
import com.example.newsapp2.data.Resource
import com.example.newsapp2.data.network.Filter
import com.example.newsapp2.data.network.retrofit.RetrofitClient
import com.example.newsapp2.databinding.FragmentMainBinding
import com.example.newsapp2.di.AppContainer
import com.example.newsapp2.ui.viewModel.NewsViewModel
import com.example.newsapp2.ui.viewModel.NewsViewModelFactory

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsFilter: Filter
    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val retrofitService = RetrofitClient.instance
        val localDataSource = LocalDataSource()
        val remoteDataSource = RemoteDataSource(retrofitService)
        val newsRepository = NewsRepository(localDataSource, remoteDataSource)
        viewModel = ViewModelProvider(this, NewsViewModelFactory(newsRepository)).get(NewsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.news.observe(viewLifecycleOwner){ response ->
            Log.e("state", "nepoxyi")
            when(response){
                is Resource.Loading -> {
                    Log.e("state", "Loading")
                    binding.testText.text = "Loading"
                }
                is Resource.Success -> {
                    Log.e("state", "Success")
                    response.data?.let { news ->
                        binding.testText.text = news.articles[0].author
                    }
                }
                is Resource.DataBase -> {
                    Log.e("state", "DataBase")
                    response.data?.let { news ->
                        binding.testText.text = news.articles[0].author
                    }
                    response.message?.let {
                        binding.testText.text = it
                    }
                }
                is Resource.Error -> {
                    Log.e("state", "Error")

                    response.message?.let {
                        binding.testText.text = it
                    }
                }
            }

        }
        Log.e("state", "poxyi")

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}