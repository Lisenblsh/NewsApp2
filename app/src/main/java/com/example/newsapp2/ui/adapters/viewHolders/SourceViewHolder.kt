package com.example.newsapp2.ui.adapters.viewHolders

import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.util.forEach
import androidx.core.util.isNotEmpty
import androidx.core.view.isVisible
import com.example.newsapp2.R
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.data.room.TypeSource
import com.example.newsapp2.tools.DatabaseFun
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SourceViewHolder(private val itemView: View, private val type: TypeSource) {
    private val databaseFun = DatabaseFun(NewsDataBase.getInstance(itemView.context))
    private val title: TextView = itemView.findViewById(R.id.title)
    private val button: Button = itemView.findViewById(R.id.delete_button)
    private val listView: ListView = itemView.findViewById(R.id.source_list)
    private val listEmpty: TextView = itemView.findViewById(R.id.list_empty)

    private val titleTextResource =
        if (type == TypeSource.BlockSource) R.string.blocks_source else R.string.follows_source

    suspend fun bind() {
        val list = databaseFun.getSource(type)
        title.text = itemView.resources.getString(titleTextResource)
        if (list.isEmpty()) {
            listEmpty.visibility = View.VISIBLE
        } else {
            list as ArrayList<String>
            val adapter = ArrayAdapter(
                itemView.context,
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
                CoroutineScope(Dispatchers.Main).launch {
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
    }
}
