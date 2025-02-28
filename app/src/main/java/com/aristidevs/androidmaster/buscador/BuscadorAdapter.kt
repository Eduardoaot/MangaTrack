package com.aristidevs.androidmaster.buscador

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.manga.MangaListHolder
import com.aristidevs.androidmaster.manga.MangaListaItemResponse

class BuscadorAdapter(
    var busquedaLista: List<BusquedaListaItemResponse> = emptyList(),
    private val onItemSelected:(String)-> Unit
) :
    RecyclerView.Adapter<BuscadorHolder>() {

    fun updateList(list: List<BusquedaListaItemResponse>) {
        busquedaLista = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuscadorHolder {

        return BuscadorHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_buscador, parent, false)
        )
    }

    override fun onBindViewHolder(viewholder: BuscadorHolder, position: Int) {
        viewholder.bind(busquedaLista[position], onItemSelected)
    }

    override fun getItemCount() = busquedaLista.size
}