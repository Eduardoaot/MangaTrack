package com.aristidevs.androidmaster.principalpresupuestos.guardados

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ItemMangalistBinding
import com.squareup.picasso.Picasso

class MangaListHolder(private val binding: ItemMangalistBinding) : RecyclerView.ViewHolder(binding.root) {

    // Cambiar el tipo de parámetro a 'MangaPresupuestoItemResponse'
    fun bind(mangaPresupuestoItemResponse: MangaPresupuestoItemResponse) {
        val mangaNum = mangaPresupuestoItemResponse.mangaNum.toFloat()

        // Verifica si el número tiene decimales y si no, lo muestra sin ellos
        val mangaNumText = if (mangaNum == mangaNum.toInt().toFloat()) {
            mangaNum.toInt().toString() // Si es entero, lo mostramos sin decimales
        } else {
            mangaNum.toString() // Si tiene decimales, lo mostramos completo
        }

        // Asignamos el número del manga
        binding.tvMangaListNumber.text = mangaNumText

        // Cargar la imagen con Picasso
        Picasso.get()
            .load(mangaPresupuestoItemResponse.mangaImg)
            .placeholder(R.drawable.placeholder)  // Imagen mientras carga
            .error(R.drawable.error_image)       // Imagen si ocurre un error
            .into(binding.ivMangaListImage)
    }
}





