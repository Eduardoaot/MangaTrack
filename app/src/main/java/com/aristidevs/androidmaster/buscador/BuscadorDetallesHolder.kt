package com.aristidevs.androidmaster.buscador

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.databinding.ItemBuscadorBinding
import com.aristidevs.androidmaster.databinding.ItemBuscadorDetallesBinding
import com.squareup.picasso.Picasso

class BuscadorDetallesHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemBuscadorDetallesBinding.bind(view) // Usamos ViewBinding para acceder a las vistas

    @SuppressLint("SetTextI18n")
    fun bind(busquedaListaDetallesItemResponse: BusquedaListaDetallesItemResponse, onItemSelected: (Int) -> Unit) {
        // Asignamos los valores a las vistas del layout
        binding.txtTitle.text = busquedaListaDetallesItemResponse.serieNom
        binding.txtTotalSerieDetalleB.text = "Colección de: " + busquedaListaDetallesItemResponse.serieTotalTomos + " números."
        if (busquedaListaDetallesItemResponse.serieTotalTomos == "1"){
            binding.txtTotalSerieDetalleB.text = "Tomo único."
        }
        Picasso.get().load(busquedaListaDetallesItemResponse.serieImg).into(binding.imgCover)

        // Establecemos el listener para el botón
        binding.btnDetallesSerie.setOnClickListener {
            // Llamamos a la función cuando el botón es clickeado
            onItemSelected(busquedaListaDetallesItemResponse.serieId)
        }

    }
}
