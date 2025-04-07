package com.aristidevs.androidmaster.principalpresupuestos.guardados

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ItemMangalistBinding
import com.aristidevs.androidmaster.manga.MangaListaItemResponse
import com.squareup.picasso.Picasso

class MangaListHolder2(
    private val binding: ItemMangalistBinding,
    private val onItemSelected: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(mangaItem: MangaPresupuestoItemResponse) {
        with(binding) {
            // Formateo del número de manga
            val mangaNumText = if (mangaItem.mangaNum % 1 == 0f) {
                mangaItem.mangaNum.toInt().toString()
            } else {
                mangaItem.mangaNum.toString()
            }

            tvMangaListNumber.text = mangaNumText

            // Carga de imagen con manejo de errores
            Picasso.get()
                .load(mangaItem.mangaImg)
                .into(ivMangaListImage)

            // Click listener
            root.setOnClickListener {
                onItemSelected(mangaItem.idManga)
                animateClick() // Efecto visual opcional
            }
        }
    }

    private fun animateClick() {
        binding.root.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction {
                binding.root.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
            }
    }
}