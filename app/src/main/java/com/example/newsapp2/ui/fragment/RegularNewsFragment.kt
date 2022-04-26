package com.example.newsapp2.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp2.R
import com.example.newsapp2.data.network.CurrentFilter
import com.example.newsapp2.data.network.FilterForNewsApi
import com.example.newsapp2.data.network.TypeNewsUrl
import com.example.newsapp2.databinding.FragmentRegularNewsBinding
import com.example.newsapp2.di.Injection
import com.example.newsapp2.tools.convertToAPIDate
import com.example.newsapp2.tools.convertToDeviceDate
import com.example.newsapp2.tools.showWebView
import com.example.newsapp2.ui.adapters.NewsLoadStateAdapter
import com.example.newsapp2.ui.adapters.NewsPagingAdapter
import com.example.newsapp2.ui.viewModel.NewsViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegularNewsFragment : Fragment() {

    private lateinit var binding: FragmentRegularNewsBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var pref: SharedPreferences
    private lateinit var typeNewsUrl: TypeNewsUrl

    private lateinit var newsAdapter: NewsPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (!this::binding.isInitialized) {
            binding = FragmentRegularNewsBinding.inflate(inflater, container, false)
            createSharedPreference()
            viewModel = ViewModelProvider(
                this, Injection.provideViewModelFactory(
                    requireActivity().applicationContext, this, typeNewsUrl
                ) // Крч при смене API надо очищять адаптер иначе старые новости остаются
            ).get(NewsViewModel::class.java)
            binding.bindingElement()
        }
        return binding.root
    }

    private fun FragmentRegularNewsBinding.bindingElement() {
        bindAdapter()
        initSwipeRefresh()
        initMenu()
        initGoToUpBtn()
    }

    private fun FragmentRegularNewsBinding.bindAdapter() {
        newsAdapter = NewsPagingAdapter()
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

    private var dateFrom = ""
    private var dateTo = ""

    private fun FragmentRegularNewsBinding.initMenu() {
        val sortByArray = resources.getStringArray(R.array.news_sort_by_for_api)
        val searchInArray = resources.getStringArray(R.array.news_search_in_for_api)
        val languageArray = resources.getStringArray(R.array.news_lang_code_array)

        languageSpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.news_lang_name_array,
            android.R.layout.simple_spinner_item
        )// Адаптер для спинера языка
        languageSpinner.setSelection(
            languageArray.indexOf(pref.getString("LANGUAGE", ""))
        )

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

        dateButton.setOnClickListener {
            val builder = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(
                    androidx.core.util.Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )
                .setTheme(R.style.MyDatePickerStyle)
            val picker = builder.build()
            picker.show(activity?.supportFragmentManager!!, picker.toString())
            picker.addOnPositiveButtonClickListener {
                dateFrom = convertToAPIDate(it.first)
                dateTo = convertToAPIDate(it.second)
                dateText.text = resources.getString(
                    R.string.date_text,
                    convertToDeviceDate(it.first, 0),
                    convertToDeviceDate(it.second, 0)
                )
            }
        }

        menuCard.setOnClickListener {
            menuLayout.visibility = View.VISIBLE
            /*Это надо заменить на анимации*/
        }//Для открытия меню

        forCloseMenu.setOnClickListener {
            menuLayout.visibility = View.GONE
            /*Это надо заменить на анимации*/
        }//Для закрытия меню

        resetButton.setOnClickListener {
            CurrentFilter.filterForNewsApi = FilterForNewsApi(
                language = CurrentFilter.filterForNewsApi.language,
                excludeDomains = CurrentFilter.excludeDomains
            )
            searchBar.apply {
                setQuery("", true)
                isIconified = true
            }
            whereSearchSpinner.setSelection(0)
            sortBySpinner.setSelection(0)

            dateText.text = ""
            newsList.scrollToPosition(0)
            newsAdapter.refresh()
        }//Для отмены фильтров

        confirmButton.setOnClickListener {


            val lang = languageArray[languageSpinner.selectedItemPosition]

            with(pref.edit()) {
                putString("LANGUAGE", lang)
                apply()
            }

            CurrentFilter.filterForNewsApi =
                CurrentFilter.filterForNewsApi.copy(
                    language = lang,
                    query = "${searchBar.query}".ifBlank { "a" },
                    sortBy = sortByArray[sortBySpinner.selectedItemPosition],
                    searchIn = searchInArray[whereSearchSpinner.selectedItemPosition],
                    from = dateFrom,
                    to = dateTo
                )
            newsList.scrollToPosition(0)
            newsAdapter.refresh()
        }//Для применения фильтров
    }

    private fun FragmentRegularNewsBinding.initGoToUpBtn() {
        goToUpButton.setOnClickListener {
            if (newsAdapter.itemCount >= 10) newsList.scrollToPosition(10)
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
        CurrentFilter.filterForNewsApi =
            FilterForNewsApi(language = pref.getString("LANGUAGE", "")!!)
        typeNewsUrl = TypeNewsUrl.values()[pref.getInt("TYPE_NEWS_URL", 1)]

    }//Вытаскиваю сохраненный язык из настроек

    override fun onResume() {
        super.onResume()
        pref = requireActivity().getSharedPreferences("appSettings", Context.MODE_PRIVATE)
        val prefType = pref.getInt("TYPE_NEWS_URL", 1)
        Log.e("type", prefType.toString())
    }
}
