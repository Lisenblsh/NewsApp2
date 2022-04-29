package com.example.newsapp2.ui.adapters.viewHolders

import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.example.newsapp2.R
import com.example.newsapp2.data.network.*
import com.example.newsapp2.tools.convertToAPIDate
import com.example.newsapp2.tools.convertToDeviceDateFilter
import com.google.android.material.datepicker.MaterialDatePicker

abstract class FilterViewHolder(private val itemVew: View, private val type: TypeNewsUrl) {
    private val menu = getView(R.id.menu_layout)
    private val openMenuButton = getView(R.id.menu_card)
    private val forClose = getView(R.id.for_close_menu)
    private val languageTitle = getView(R.id.language_title) as TextView
    private val languageSpinner = getView(R.id.language_spinner) as Spinner
    private val searchBar = getView(R.id.search_bar) as androidx.appcompat.widget.SearchView
    private val whereSearchTitle = getView(R.id.where_search_title) as TextView
    private val whereSearchSpinner = getView(R.id.where_search_spinner) as Spinner
    private val sortByTitle = getView(R.id.sort_by_title) as TextView
    private val sortBySpinner = getView(R.id.sort_by_spinner) as Spinner
    private val dateText = getView(R.id.date_text) as TextView
    private val dateButton = getView(R.id.date_button) as Button
    private val freshnessTitle = getView(R.id.freshness_title) as TextView
    private val freshnessSpinner = getView(R.id.freshness_spinner) as Spinner
    private val safeSearchTitle = getView(R.id.safe_search_title) as TextView
    private val safeSearchSpinner = getView(R.id.safe_search_spinner) as Spinner
    private val rssTypeTitle = getView(R.id.type_rss_title) as TextView
    private val rssTypeSpinner = getView(R.id.type_rss_spinner) as Spinner
    private val confirmButton = getView(R.id.confirm_button) as Button
    private val resetButton = getView(R.id.reset_button) as Button
    private val instruction = getView(R.id.instruction) as TextView

    private val sortByArray = itemVew.resources.getStringArray(R.array.news_sort_by_for_api)
    private val searchInArray = itemVew.resources.getStringArray(R.array.news_search_in_for_api)
    private val languageArray = itemVew.resources.getStringArray(R.array.news_lang_code_array)
    private val rssTypeArray = itemVew.resources.getStringArray(R.array.stopgame_type_rss_api)

    init {
        bind()
    }

    private fun bind() {
        openMenuButton.setOnClickListener {
            menu.visibility = View.VISIBLE
        }

        forClose.setOnClickListener {
            menu.visibility = View.GONE
        }

        languageSpinner.adapter = getSpinnerAdapter(R.array.news_lang_name_array)
        Log.e("pref", "${getPreferences()}")
        languageSpinner.setSelection(
            languageArray.indexOf(getPreferences().getString("LANGUAGE", ""))
        )
        when (type) {
            TypeNewsUrl.NewsApi -> {
                initNewsApi()
                showNewsApiFilter()
            }
            TypeNewsUrl.BingNews -> {
                initBingNews()
                showBingNewsFilter()
            }
            TypeNewsUrl.Newscatcher -> {
                initNewscather()
                showNewscatherFilter()
            }
            TypeNewsUrl.StopGame -> {
                initStopGame()
                showStopGameFilter()
            }
        }
    }

    private var dateFrom = ""
    private var dateTo = ""


    private fun initNewsApi() {
        whereSearchSpinner.adapter = getSpinnerAdapter(R.array.news_search_in)
        sortBySpinner.adapter = getSpinnerAdapter(R.array.news_sort_by)

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
            picker.show(fragmentManager, picker.toString())
            picker.addOnPositiveButtonClickListener {
                dateFrom = convertToAPIDate(it.first)
                dateTo = convertToAPIDate(it.second)
                dateText.text = itemVew.resources.getString(
                    R.string.date_text,
                    convertToDeviceDateFilter(it.first),
                    convertToDeviceDateFilter(it.second)
                )
            }
        }

        resetButton.setOnClickListener {
            CurrentFilter.filterForNewsApi = FilterForNewsApi(
                language = getPreferences().getString("LANGUAGE", "")!!,
                excludeDomains = CurrentFilter.excludeDomains
            )
            searchBar.setQuery("", true)

            whereSearchSpinner.setSelection(0)
            sortBySpinner.setSelection(0)

            dateText.text = ""
            updateList()
        }//Для отмены фильтров

        confirmButton.setOnClickListener {
            val lang = languageArray[languageSpinner.selectedItemPosition]

            with(getPreferences().edit()) {
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
            updateList()
        }//Для применения фильтров
    }

    private fun showNewsApiFilter() {
        sortByTitle.isVisible = true
        sortBySpinner.isVisible = true
        whereSearchTitle.isVisible = true
        whereSearchSpinner.isVisible = true
        dateText.isVisible = true
        dateButton.isVisible = true
        instruction.isVisible = true
    }

    private fun initBingNews() {
        val list = itemVew.resources.getStringArray(R.array.news_sort_by).toList().subList(0, 2)
        sortBySpinner.adapter = ArrayAdapter(
            itemVew.context,
            android.R.layout.simple_spinner_dropdown_item,
            list
        )

        safeSearchSpinner.adapter = getSpinnerAdapter(R.array.safe_search)
        freshnessSpinner.adapter = getSpinnerAdapter(R.array.freshness)

        resetButton.setOnClickListener {
            CurrentFilter.filterForBingNews = FilterForBingNews(
                language = getPreferences().getString("LANGUAGE", "")!!
            )
            searchBar.setQuery("", true)
            sortBySpinner.setSelection(0)
            safeSearchSpinner.setSelection(0)
            freshnessSpinner.setSelection(0)
            Log.e("filterR", "${CurrentFilter.filterForBingNews}")
            dateText.text = ""
            updateList()
        }//Для отмены фильтров

        confirmButton.setOnClickListener {
            val lang = languageArray[languageSpinner.selectedItemPosition]
            val sortBy = if (sortBySpinner.selectedItemPosition == 0) "Date" else ""

            with(getPreferences().edit()) {
                putString("LANGUAGE", lang)
                apply()
            }

            CurrentFilter.filterForBingNews =
                CurrentFilter.filterForBingNews.copy(
                    query = "${searchBar.query}",
                    language = lang,
                    sortBy = sortBy,
                    safeSearch = SafeSearch.values()[safeSearchSpinner.selectedItemPosition],
                    freshness = Freshness.values()[freshnessSpinner.selectedItemPosition]
                )
            Log.e("filterR", "${CurrentFilter.filterForBingNews}")

            updateList()
        }//Для применения фильтров
    }

    private fun showBingNewsFilter() {
        sortByTitle.isVisible = true
        sortBySpinner.isVisible = true
        safeSearchTitle.isVisible = true
        safeSearchSpinner.isVisible = true
        freshnessTitle.isVisible = true
        freshnessSpinner.isVisible = true
    }

    private fun initNewscather() {
        resetButton.setOnClickListener {
            CurrentFilter.filterForNewscatcher = FilterForNewscather(
                language = getPreferences().getString("LANGUAGE", "")!!
            )
            searchBar.setQuery("", true)
            updateList()

        }

        confirmButton.setOnClickListener {
            val lang = languageArray[languageSpinner.selectedItemPosition]

            with(getPreferences().edit()) {
                putString("LANGUAGE", lang)
                apply()
            }

            CurrentFilter.filterForNewscatcher =
                CurrentFilter.filterForNewscatcher.copy(
                    query = "${searchBar.query}".ifBlank { "a" },
                    language = lang
                )
            updateList()
        }
    }

    private fun showNewscatherFilter() {
    }

    private fun initStopGame() {
        rssTypeSpinner.adapter = getSpinnerAdapter(R.array.stopgame_type_rss)

        confirmButton.setOnClickListener {
            val type = rssTypeArray[rssTypeSpinner.selectedItemPosition]

            CurrentFilter.filterForStopGame =
                FilterForStopGame(path = "https://rss.stopgame.ru/$type")
            updateList()
        }
    }

    private fun showStopGameFilter() {
        languageTitle.visibility = View.GONE
        languageSpinner.visibility = View.GONE
        searchBar.visibility = View.GONE
        resetButton.visibility = View.GONE

        rssTypeTitle.isVisible = true
        rssTypeSpinner.isVisible = true
    }

    private fun getSpinnerAdapter(res: Int): ArrayAdapter<CharSequence> {
        return ArrayAdapter.createFromResource(
            itemVew.context,
            res,
            android.R.layout.simple_spinner_dropdown_item
        )
    }

    private fun getView(res: Int): View {
        return itemVew.findViewById(res)
    }

    abstract fun getPreferences(): SharedPreferences

    abstract fun updateList()

    abstract val fragmentManager: FragmentManager
}
