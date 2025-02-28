package com.aristidevs.androidmaster.buscador

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.databinding.ItemBuscadorBinding
import com.aristidevs.androidmaster.manga.MangaListaItemResponse
import com.squareup.picasso.Picasso


class BuscadorHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemBuscadorBinding.bind(view)

    fun bind(busquedaListaItemResponse: BusquedaListaItemResponse, onItemSelected:(String)-> Unit) {
        binding.ResultadoBusqueda.text = busquedaListaItemResponse.serieNom
        binding.root.setOnClickListener { onItemSelected(busquedaListaItemResponse.serieNom)}
    }
}