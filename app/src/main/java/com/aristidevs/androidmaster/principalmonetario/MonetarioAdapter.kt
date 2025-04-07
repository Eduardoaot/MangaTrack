package com.aristidevs.androidmaster.principalmonetario

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.manga.MangaListHolder

class MonetarioAdapter (
    var mangaList: List<MejorAhorro> = emptyList(),
) :
    RecyclerView.Adapter<MonetarioHolder>() {

    fun updateList(list: List<MejorAhorro>) {
        mangaList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonetarioHolder {
        return MonetarioHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_monetario, parent, false)
        )
    }

    override fun onBindViewHolder(viewholder: MonetarioHolder, position: Int) {
        viewholder.bind(mangaList[position])
    }

    override fun getItemCount() = mangaList.size
}