package com.example.newsapp2.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp2.R
import com.example.newsapp2.data.network.CurrentFilter
import com.example.newsapp2.data.network.Filter
import com.example.newsapp2.data.network.TypeNewsUrl
import com.example.newsapp2.databinding.FragmentRegularNewsBinding
import com.example.newsapp2.di.Injection
import com.example.newsapp2.tools.showWebView
import com.example.newsapp2.ui.adapters.NewsLoadStateAdapter
import com.example.newsapp2.ui.adapters.NewsPagingAdapter
import com.example.newsapp2.ui.adapters.viewHolders.FilterViewHolder
import com.example.newsapp2.ui.viewModel.NewsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegularNewsFragment : Fragment() {

    private lateinit var binding: FragmentRegularNewsBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var pref: SharedPreferences
    private lateinit var typeNewsUrl: TypeNewsUrl

    private val newsAdapter = NewsPagingAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (!this::binding.isInitialized) {
            binding = FragmentRegularNewsBinding.inflate(inflater, container, false)
            createSharedPreference()
            viewModel = getViewModel()
            binding.bindingElement()
        }
        return binding.root
    }

    private fun FragmentRegularNewsBinding.bindingElement() {
        bindAdapter()
        initSwipeRefresh()
        initFilterMenu()
        initGoToUpBtn()
    }

    private fun FragmentRegularNewsBinding.bindAdapter() {
        val header = NewsLoadStateAdapter { newsAdapter.retry() }
        newsAdapter.setOnItemClickListener(object : NewsPagingAdapter.OnItemClickListener {
            override fun onItemClick(id: Long?) {
                if (id != null) {
                    showWebView(this@RegularNewsFragment, id)
                }
            }
        })
        newsList.adapter = newsAdapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = NewsLoadStateAdapter { newsAdapter.retry() }
        )
        newsList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        lifecycleScope.launch {
            viewModel.pagingDataRegularNewsFlow.collectLatest(newsAdapter::submitData)
        }

        lifecycleScope.launch {
            newsAdapter.loadStateFlow.collect { loadState ->
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && newsAdapter.itemCount >= 0 }
                    ?: loadState.prepend

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                    ?: loadState.refresh as? LoadState.Error

                errorState?.let {
                    if ((it.error as? HttpException)?.code() != 426) {
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.error_occurred, it.error.localizedMessage),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun FragmentRegularNewsBinding.initSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            newsAdapter.refresh()
            swipeRefresh.isRefreshing = false
        }
    }

    private fun FragmentRegularNewsBinding.initFilterMenu() {
        if (typeNewsUrl != TypeNewsUrl.WebSearch) {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.filter_menu_layout, root, false)
            object : FilterViewHolder(view, typeNewsUrl) {

                override fun getPreferences(): SharedPreferences {
                    return pref
                }

                override fun updateList() {
                    newsList.scrollToPosition(0)
                    newsAdapter.refresh()
                }

                override val fragmentManager = requireActivity().supportFragmentManager
            }
            root.addView(view)
        }
    }

    private fun FragmentRegularNewsBinding.initGoToUpBtn() {
        goToUpButton.setOnClickListener {
            val position = (newsList.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            if (position >= 10) newsList.scrollToPosition(10)
            newsList.smoothScrollToPosition(0)
        }
    }

    private fun createSharedPreference() {
        pref = requireActivity().getSharedPreferences("appSettings", Context.MODE_PRIVATE)
        if (!pref.contains("LANGUAGE")) {
            with(pref.edit()) {
                putString("LANGUAGE", "")
                apply()
            }
        }//проверка на наличия дефолтного языка
        val lang = pref.getString("LANGUAGE", "")!!
        typeNewsUrl = TypeNewsUrl.values()[pref.getInt("TYPE_NEWS_URL", 1)]
        CurrentFilter.filter = Filter(apiName = typeNewsUrl, lang = lang)

    }//Вытаскиваю сохраненный язык из настроек

    private fun getViewModel(): NewsViewModel {
        return ViewModelProvider(
            this, Injection.provideViewModelFactory(
                requireActivity().applicationContext, this, typeNewsUrl
            ) // Крч при смене API надо очищять адаптер иначе старые новости остаются
        ).get(NewsViewModel::class.java)
    }
}
