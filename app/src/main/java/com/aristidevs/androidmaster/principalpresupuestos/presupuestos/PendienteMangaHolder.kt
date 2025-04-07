package com.aristidevs.androidmaster.principalpresupuestos.presupuestos

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.databinding.ItemPresupuestosBinding
import com.squareup.picasso.Picasso

class PendienteMangaHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemPresupuestosBinding.bind(view)

    fun bind(
        pendienteMangaItem: PendienteMangaItemConEstado,
        onAddClick: (Int, Float, Boolean) -> Unit,
        onItemSelected: (Int) -> Unit
    ) {
        Picasso.get().load(pendienteMangaItem.mangaImg).into(binding.ivMangaListImage)
        val formattedMangaNum = if (pendienteMangaItem.mangaNum % 1 == 0f) {
            // Si es un número entero, lo mostramos como entero (sin decimales)
            pendienteMangaItem.mangaNum.toInt().toString()
        } else {
            // Si tiene decimales, mostramos con los decimales
            pendienteMangaItem.mangaNum.toString()
        }

        binding.tvMangaListNumber.text = formattedMangaNum


        val precioFormateado = if (pendienteMangaItem.precio % 1 == 0f) {
            String.format("%.0f", pendienteMangaItem.precio)
        } else {
            String.format("%.2f", pendienteMangaItem.precio)
        }
        binding.tvMangaListPrice.text = "$$precioFormateado"

        // Actualizar visibilidad de los botones según el estado actual
        binding.btnAgregar.isVisible = !pendienteMangaItem.isAdded
        binding.btnEliminar.isVisible = pendienteMangaItem.isAdded

        binding.btnAgregar.setOnClickListener {
            onAddClick(pendienteMangaItem.idManga, pendienteMangaItem.precio, true)
        }

        binding.btnEliminar.setOnClickListener {
            onAddClick(pendienteMangaItem.idManga, pendienteMangaItem.precio, false)
        }

        itemView.setOnClickListener {
            onItemSelected(pendienteMangaItem.idManga)
        }
    }
}

