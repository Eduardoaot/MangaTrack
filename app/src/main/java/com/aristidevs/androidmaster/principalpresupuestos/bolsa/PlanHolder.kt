package com.aristidevs.androidmaster.principalpresupuestos.bolsa

import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.databinding.ItemBolsaBinding
import com.aristidevs.androidmaster.databinding.ItemPresupuestosBinding
import com.aristidevs.androidmaster.principalpresupuestos.presupuestos.PendienteMangaItemConEstado
import com.squareup.picasso.Picasso

class PlanHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemBolsaBinding.bind(view)

    fun bind(
        mangaItem: PlanElementosItemResponse,
        onAddClick: (Int, Float, Boolean) -> Unit,
        onItemSelected: (Int) -> Unit
    ) {

        // Formatear el número del manga
        val mangaNumText = if (mangaItem.mangaNum == mangaItem.mangaNum.toInt().toFloat()) {
            mangaItem.mangaNum.toInt().toString() // Si es un número entero
        } else {
            mangaItem.mangaNum.toString() // Si tiene decimales
        }

        val precioFormateado = if (mangaItem.precio % 1 == 0f) {
            String.format("%.0f", mangaItem.precio)
        } else {
            String.format("%.2f", mangaItem.precio)
        }
        binding.tvMangaListPrice.text = "$$precioFormateado"

        // Establecer el número del manga
        binding.tvMangaListNumber.text = mangaNumText

        // Cargar la imagen del manga (deberías usar Picasso o Glide para esto)
        Picasso.get().load(mangaItem.mangaImg).into(binding.ivMangaListImage)

        // Mostrar el botón de agregar o eliminar dependiendo del estado
        binding.btnEliminar.isVisible = true // Mostrar el botón eliminar siempre

        // Acción del botón eliminar
        binding.btnEliminar.setOnClickListener {
            // Cambiar el estado al eliminar manga
            onAddClick(mangaItem.idManga, mangaItem.precio, false)
            // Ocultar el botón de eliminar
            binding.btnEliminar.isVisible = false
        }

        // Al hacer clic en el item, navega al detalle del manga
        itemView.setOnClickListener {
            onItemSelected(mangaItem.idManga)
        }
    }
}



