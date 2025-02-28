package com.aristidevs.androidmaster.manga

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.superheroapp.SuperheroItemResponse
import com.google.firebase.firestore.FirebaseFirestore

class MangaListAdapter(
    var mangaList: List<MangaListaItemResponse> = emptyList(),
    private val onItemSelected:(Int)-> Unit
) :
    RecyclerView.Adapter<MangaListHolder>() {

    fun updateList(list: List<MangaListaItemResponse>) {
        mangaList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaListHolder {
        return MangaListHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_mangalist, parent, false)
        )
    }

    override fun onBindViewHolder(viewholder: MangaListHolder, position: Int) {
        viewholder.bind(mangaList[position], onItemSelected)
    }

    override fun getItemCount() = mangaList.size
}
