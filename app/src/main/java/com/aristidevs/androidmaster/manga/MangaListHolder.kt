package com.aristidevs.androidmaster.manga

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.databinding.ItemMangalistBinding
import com.squareup.picasso.Picasso

class MangaListHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemMangalistBinding.bind(view)

    fun bind(mangaListaItemResponse: MangaListaItemResponse, onItemSelected: (Int) -> Unit) {
        val mangaNum = mangaListaItemResponse.mangaNum.toFloat()

        // Verifica si el número tiene decimales y si no, lo muestra sin ellos
        val mangaNumText = if (mangaNum == mangaNum.toInt().toFloat()) {
            mangaNum.toInt().toString() // Si es entero, lo mostramos sin decimales
        } else {
            mangaNum.toString() // Si tiene decimales, lo mostramos completo
        }
        binding.root.setOnClickListener {onItemSelected(mangaListaItemResponse.mangaId)}
        binding.tvMangaListNumber.text = mangaNumText
        Picasso.get().load(mangaListaItemResponse.mangaImg).into(binding.ivMangaListImage)

    }
}
