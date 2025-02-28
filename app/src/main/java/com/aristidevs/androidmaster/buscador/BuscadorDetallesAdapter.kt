package com.aristidevs.androidmaster.buscador

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R

class BuscadorDetallesAdapter(
    var busquedaListaDetalles: List<BusquedaListaDetallesItemResponse> = emptyList(),
    private val onItemSelected:(Int)-> Unit
) :
    RecyclerView.Adapter<BuscadorDetallesHolder>() {

    fun updateList(list: List<BusquedaListaDetallesItemResponse>) {
        busquedaListaDetalles = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuscadorDetallesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_buscador_detalles, parent, false)
        return BuscadorDetallesHolder(view)
    }


    override fun onBindViewHolder(viewholder: BuscadorDetallesHolder, position: Int) {
        viewholder.bind(busquedaListaDetalles[position], onItemSelected)
    }

    override fun getItemCount() = busquedaListaDetalles.size
}