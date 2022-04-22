package com.example.newsapp2.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.util.forEach
import androidx.core.util.isNotEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.newsapp2.R
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.data.room.TypeSource
import com.example.newsapp2.databinding.FragmentCurrentSettingBinding
import com.example.newsapp2.tools.DatabaseFun
import com.example.newsapp2.tools.TypeSetting
import kotlinx.coroutines.launch


class CurrentSettingFragment : Fragment() {

    private lateinit var binding: FragmentCurrentSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrentSettingBinding.inflate(inflater, container, false)
        val args = CurrentSettingFragmentArgs.fromBundle(requireArguments())

        lifecycleScope.launch {
            binding.showSetting(args.typeSetting)
        }

        return binding.root
    }

    private suspend fun FragmentCurrentSettingBinding.showSetting(typeSetting: TypeSetting) {
        when (typeSetting) {
            TypeSetting.Theme -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.theme_setting_layout, root, false)
                ThemeViewHolder(view).bind()
            }
            TypeSetting.Follow -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.source_setting_layout, root, false)
                SourceViewHolder(view, TypeSource.FollowSource).bind()
            }
            TypeSetting.BlockSource -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.source_setting_layout, root, false)
                SourceViewHolder(view,TypeSource.BlockSource).bind()
            }
        }
    }

    inner class ThemeViewHolder(private val itemView: View) {

        private val listView: ListView = itemView.findViewById(R.id.theme_list)

        private val pref = activity?.getSharedPreferences("appSettings", Context.MODE_PRIVATE)

        fun bind() {
            listView.adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.app_theme_array,
                android.R.layout.simple_list_item_single_choice
            )
            listView.choiceMode = ListView.CHOICE_MODE_SINGLE

            listView.setOnItemClickListener { _, _, position, _ ->
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
                if (pref != null) {
                    with(pref.edit()) {
                        putInt("THEME_ID", position)
                        apply()
                    }
                }
            }
            if (pref != null) {
                listView.setItemChecked(pref.getInt("THEME_ID", 0), true)
            }
            binding.root.addView(itemView)
        }
    }

    inner class SourceViewHolder(private val itemView: View, private val type: TypeSource) {
        private val databaseFun = DatabaseFun(NewsDataBase.getInstance(requireContext()))
        private val title: TextView = itemView.findViewById(R.id.title)
        private val button: Button = itemView.findViewById(R.id.delete_button)
        private val listView: ListView = itemView.findViewById(R.id.source_list)
        private val listEmpty: TextView = itemView.findViewById(R.id.list_empty)

        private val titleTextResource =
            if (type == TypeSource.BlockSource) R.string.blocks_source else R.string.follows_source

        suspend fun bind() {
            val list = databaseFun.getSource(type)
            title.text = resources.getString(titleTextResource)
            if (list.isEmpty()) {
                listEmpty.visibility = View.VISIBLE
            } else {
                list as ArrayList<String>
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_multiple_choice,
                    list
                )
                listView.apply {
                    this.adapter = adapter
                    choiceMode = ListView.CHOICE_MODE_MULTIPLE
                    setOnItemClickListener { _, _, _, _ ->
                        button.isVisible = listView.checkedItemPositions.isNotEmpty()
                    }
                }

                button.setOnClickListener {
                    lifecycleScope.launch {
                        val list2: ArrayList<String> = arrayListOf()
                        listView.checkedItemPositions.forEach { key, _ ->
                            list2.add(list[key])
                        }
                        databaseFun.deleteSource(type, list2)
                        listView.checkedItemPositions.clear()
                        list.removeAll(list2)
                        Log.e("list", "$list")

                        listEmpty.isVisible = list.isEmpty()
                        it.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            binding.root.addView(itemView)
        }
    }
}
