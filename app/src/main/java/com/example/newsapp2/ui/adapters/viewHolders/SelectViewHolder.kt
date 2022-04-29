package com.example.newsapp2.ui.adapters.viewHolders

import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.example.newsapp2.R
import com.example.newsapp2.tools.TypeSetting

abstract class SelectViewHolder(private val itemView: View, private val type: TypeSetting) {

    private val listView: ListView = itemView.findViewById(R.id.select_list)
    private val title: TextView = itemView.findViewById(R.id.title)

    fun bind() {
        val array: Int
        val key: String

        when (type) {
            TypeSetting.Theme -> {
                array = R.array.app_theme_array
                key = "THEME_ID"
                title.text = itemView.resources.getString(R.string.themes)
            }
            TypeSetting.ApiSource -> {
                array = R.array.api_source
                key = "TYPE_NEWS_URL"
                title.text = itemView.resources.getString(R.string.sources)
            }
            else -> return
        }

        listView.adapter = ArrayAdapter.createFromResource(
            itemView.context,
            array,
            android.R.layout.simple_list_item_single_choice
        )
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        listView.setOnItemClickListener { _, _, position, _ ->
            if (type == TypeSetting.Theme) forTheme(key, position)
            else if (type == TypeSetting.ApiSource) forAPI(key, position)
        }
        listView.setItemChecked(getPreferences().getInt(key, 0), true)
    }

    private fun forTheme(key: String, position: Int) {
        when (position) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
        savePref(key, position)
    }

    private fun forAPI(key: String, position: Int) {
        savePref(key, position)
        forRestartActivity()
    }

    private fun savePref(key: String, position: Int) {
        with(getPreferences().edit()) {
            putInt(key, position)
            apply()
        }
    }

    abstract fun getPreferences(): SharedPreferences

    abstract fun forRestartActivity()

}
