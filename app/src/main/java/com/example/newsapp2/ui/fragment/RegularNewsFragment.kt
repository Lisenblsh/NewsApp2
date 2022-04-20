package com.example.newsapp2.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
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
import com.example.newsapp2.databinding.FragmentRegularNewsBinding
import com.example.newsapp2.di.Injection
import com.example.newsapp2.tools.showWebView
import com.example.newsapp2.ui.adapters.NewsPagingAdapter
import com.example.newsapp2.ui.adapters.NewsLoadStateAdapter
import com.example.newsapp2.ui.viewModel.NewsViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegularNewsFragment : Fragment() {

    private lateinit var binding: FragmentRegularNewsBinding
    private lateinit var viewModel: NewsViewModel

    private val newsAdapter = NewsPagingAdapter()

    private val pref = activity?.getSharedPreferences("appSettings", Context.MODE_PRIVATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (!this::binding.isInitialized) {
            binding = FragmentRegularNewsBinding.inflate(inflater, container, false)
            createSharedPreference()
            viewModel = ViewModelProvider(
                this, Injection.provideViewModelFactory(
                    requireActivity().applicationContext, this
                )
            ).get(NewsViewModel::class.java)
            binding.bindingElement()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun FragmentRegularNewsBinding.bindingElement() {
        bindAdapter()
        initSwipeRefresh()
        initMenu()
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
                Log.e("state", "$loadState")
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
                    if ((it.error as HttpException).code() != 426) {
                        Toast.makeText(
                            requireContext(),
                            "Произошла ошибка: ${it.error}",
                            Toast.LENGTH_LONG
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

    private fun FragmentRegularNewsBinding.initMenu() {
        languageSpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.news_lang_name_array,
            android.R.layout.simple_spinner_item
        )// Адаптер для спинера языка

        whereSearchSpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.news_search_in,
            android.R.layout.simple_spinner_item
        )//Адаптер для спинера с выбором где искать

        sortBySpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.news_sort_by,
            android.R.layout.simple_spinner_item
        )//Адаптер для спинера сортировки

        menuCard.setOnClickListener {
            menuLayout.layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
            menuLayout.requestLayout()
            /**Это надо заменить на анимации*/
        }//Для открытия меню

        forCloseMenu.setOnClickListener {
            menuLayout.layoutParams.width = 0
            menuLayout.requestLayout()
            /**Это надо заменить на анимации*/
        }//Для закрытия меню

        resetButton.setOnClickListener {
            CurrentFilter.filterForNews = Filter(
                newsLanguage = CurrentFilter.filterForNews.newsLanguage,
                excludeDomains = CurrentFilter.excludeDomains
            )
            searchBar.apply {
                setQuery("", true)
                isIconified = true
            }
            whereSearchSpinner.setSelection(0)
            sortBySpinner.setSelection(2)

            clearDateFrom.visibility = View.GONE
            dateFrom.text = "Нажмите чтобы задать"
            clearDateTo.visibility = View.GONE
            dateTo.text = "Нажмите чтобы задать"
            newsList.scrollToPosition(0)
            newsAdapter.refresh()
        }//Для отмены фильтров

        confirmButton.setOnClickListener {
            val sortByArray = resources.getStringArray(R.array.news_sort_by_for_api)
            val searchInArray = resources.getStringArray(R.array.news_search_in_for_api)
            val languageArray = resources.getStringArray(R.array.news_lang_code_array)

            val lang = languageArray[languageSpinner.selectedItemPosition]

            Log.e("lang", lang)


            CurrentFilter.filterForNews =
                CurrentFilter.filterForNews.copy(
                    newsLanguage = lang,
                    newsQuery = if ("${searchBar.query}" == "") "a" else "${searchBar.query}",
                    newsSortBy = sortByArray[sortBySpinner.selectedItemPosition],
                    searchIn = searchInArray[whereSearchSpinner.selectedItemPosition],
                    newsFrom = "",
                    newsTo = ""
                )
            newsList.scrollToPosition(0)
            newsAdapter.refresh()
        }//ДЛя применения фильтров


    }

    private fun createSharedPreference() {
        if(pref != null){
            if (!pref.contains("LANGUAGE")) {
                with(pref.edit()) {
                    putString("LANGUAGE", "")
                    apply()
                }
            }//проверка на наличия дефолтного языка
            CurrentFilter.filterForNews =
                Filter(newsLanguage = pref.getString("LANGUAGE", "")!!)//передача во вью модель
            val landCodeArray = resources.getStringArray(R.array.news_lang_code_array)
            binding.languageSpinner.setSelection(
                landCodeArray.indexOf(pref.getString("LANGUAGE", ""))
            )
        }

    }//Вытаскиваю сохраненный язык из настроек

}
