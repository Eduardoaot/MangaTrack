package com.aristidevs.androidmaster.serie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.manga.MangaListaItemResponse
import com.aristidevs.androidmaster.serie.SerieListHolder
import com.aristidevs.androidmaster.serie.SerieListaItemResponse
import com.google.firebase.firestore.FirebaseFirestore

class SerieListAdapter(var serieList: List<SerieListaItemResponse> = emptyList()) :
    RecyclerView.Adapter<SerieListHolder>() {

    fun updateList(list: List<SerieListaItemResponse>) {
        serieList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerieListHolder {
        return SerieListHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_serielist, parent, false)
        )
    }

    override fun onBindViewHolder(viewholder: SerieListHolder, position: Int) {
        val item = serieList[position]
        viewholder.bind(item)
    }

    override fun getItemCount() = serieList.size
}