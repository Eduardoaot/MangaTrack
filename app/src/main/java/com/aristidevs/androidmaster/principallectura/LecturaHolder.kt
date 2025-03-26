package com.aristidevs.androidmaster.principallectura

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.databinding.ItemLecturaDetalleBinding
import com.aristidevs.androidmaster.manga.ItemMangaSinLeer
import com.aristidevs.androidmaster.manga.ListaMangaSerieItemResponse
import com.squareup.picasso.Picasso

class LecturaHolder(view: View) : RecyclerView.ViewHolder(view) {

    internal val binding = ItemLecturaDetalleBinding.bind(view)

    fun bind(
        listaMangaSerieItemResponse: ItemMangaSinLeer,
        onItemSelected: (Int) -> Unit,
        onSpecificButtonClick: (Int) -> Unit,
        seleccionarParaLeer: (Int) -> Unit
    ) {

        binding.txtTituloMangaLectura.text = listaMangaSerieItemResponse.serieNom

        val mangaNum = listaMangaSerieItemResponse.mangaNum.toFloat()

        val mangaNumText = if (mangaNum == mangaNum.toInt().toFloat()) {
            mangaNum.toInt().toString() // Si es entero, lo mostramos sin decimales
        } else {
            mangaNum.toString() // Si tiene decimales, lo mostramos completo
        }
        binding.txtNumeroMangaLectura.text = "#" + mangaNumText

        binding.root.setOnClickListener { seleccionarParaLeer(listaMangaSerieItemResponse.mangaId)}

        // Cargar la imagen con Picasso
        Picasso.get().load(listaMangaSerieItemResponse.mangaImg).into(binding.imagenMangaLectura)

        // Acción al seleccionar el manga (se puede usar para navegar a otra pantalla)
        binding.imagenMangaLectura.setOnClickListener {
            onItemSelected(listaMangaSerieItemResponse.mangaId)
        }

        // Botón para cambiar el estado de lectura
        binding.btnCambiarEstadoLectura.setOnClickListener {
            onSpecificButtonClick(listaMangaSerieItemResponse.mangaId)
        }
    }
}
