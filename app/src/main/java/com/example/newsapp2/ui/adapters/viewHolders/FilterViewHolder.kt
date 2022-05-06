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
    private val searchInTitle = getView(R.id.search_in_title) as TextView
    private val searchInSpinner = getView(R.id.search_in_spinner) as Spinner
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
    private val categoryTitle = getView(R.id.category_title) as TextView
    private val categorySpinner = getView(R.id.category_spinner) as Spinner
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
                showNewsApiFilter()
            }
            TypeNewsUrl.BingNews -> {
                showBingNewsFilter()
            }
            TypeNewsUrl.StopGame -> {
                showStopGameFilter()
            }
            TypeNewsUrl.NewsData -> {
                showNewsDataFilter()
            }
            else -> {

            }
        }
        confirmButton.setOnClickListener {
            saveFilter()
            closeMenuAndUpdate()
        }
        resetButton.setOnClickListener {
            val lang = languageArray[languageSpinner.selectedItemPosition]

            CurrentFilter.filter = Filter(
                apiName = CurrentFilter.filter.apiName,
                lang = lang
            )
            closeMenuAndUpdate()
        }
    }

    private fun saveFilter() {
        val safeSearchPosition = safeSearchSpinner.selectedItemPosition
        val safeSearch =
            if (safeSearchPosition == -1) SafeSearch.Off else SafeSearch.values()[safeSearchPosition]
        val freshnessPosition = freshnessSpinner.selectedItemPosition
        val freshness =
            if (freshnessPosition == -1) Freshness.Day else Freshness.values()[freshnessPosition]
        val categoryPosition = categorySpinner.selectedItemPosition
        val category =
            if (categoryPosition == -1) Category.Top else Category.values()[categoryPosition]
        val rssTypePosition = rssTypeSpinner.selectedItemPosition
        val rssType =
            if(rssTypePosition == -1) "" else rssTypeArray[rssTypePosition]

        val lang = languageArray[languageSpinner.selectedItemPosition]
        saveLanguage(lang)

        CurrentFilter.filter = Filter(
            apiName = CurrentFilter.filter.apiName,
            q = "${searchBar.query}",
            lang = lang,
            sortBy = ifNoInitSpinner(sortBySpinner.selectedItemPosition, sortByArray),
            searchIn = ifNoInitSpinner(searchInSpinner.selectedItemPosition, searchInArray),
            from = dateFrom,
            to = dateTo,
            excludeDomains = CurrentFilter.filter.excludeDomains,
            safeSearch = safeSearch,
            freshness = freshness,
            category = category,
            rssFeed = rssType
        )
    }

    private fun ifNoInitSpinner(position: Int, array: Array<String>): String {
        return if (position == -1) ""
        else array[position]
    }

    private var dateFrom = ""
    private var dateTo = ""
    private fun showNewsApiFilter() {
        searchInSpinner.adapter = getSpinnerAdapter(R.array.news_search_in)
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

        sortByTitle.isVisible = true
        sortBySpinner.isVisible = true
        searchInTitle.isVisible = true
        searchInSpinner.isVisible = true
        dateText.isVisible = true
        dateButton.isVisible = true
        instruction.isVisible = true
    }

    private fun showBingNewsFilter() {
        val list = itemVew.resources.getStringArray(R.array.news_sort_by).toList().subList(0, 2)
        sortBySpinner.adapter = ArrayAdapter(
            itemVew.context,
            android.R.layout.simple_spinner_dropdown_item,
            list
        )

        safeSearchSpinner.adapter = getSpinnerAdapter(R.array.safe_search)
        freshnessSpinner.adapter = getSpinnerAdapter(R.array.freshness)

        sortByTitle.isVisible = true
        sortBySpinner.isVisible = true
        safeSearchTitle.isVisible = true
        safeSearchSpinner.isVisible = true
        freshnessTitle.isVisible = true
        freshnessSpinner.isVisible = true

    }

    private fun showStopGameFilter() {
        rssTypeSpinner.adapter = getSpinnerAdapter(R.array.stopgame_type_rss)

        languageTitle.visibility = View.GONE
        languageSpinner.visibility = View.GONE
        searchBar.visibility = View.GONE
        resetButton.visibility = View.GONE

        rssTypeTitle.isVisible = true
        rssTypeSpinner.isVisible = true
    }

    private fun showNewsDataFilter() {
        categorySpinner.adapter = getSpinnerAdapter(R.array.newsdata_category)

        searchBar.visibility = View.GONE

        categoryTitle.isVisible = true
        categorySpinner.isVisible = true
    }

    private fun saveLanguage(lang: String) {
        with(getPreferences().edit()) {
            putString("LANGUAGE", lang)
            apply()
        }
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

    private fun closeMenuAndUpdate() {
        menu.visibility = View.GONE
        updateList()
    }

    abstract fun getPreferences(): SharedPreferences

    abstract fun updateList()

    abstract val fragmentManager: FragmentManager
}
