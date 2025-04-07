package com.aristidevs.androidmaster.detallesserie

import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.databinding.ItemDetalleSerieBinding

import com.aristidevs.androidmaster.manga.ListaMangaSerieItemResponse
import com.squareup.picasso.Picasso

class DetalleSerieHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding = ItemDetalleSerieBinding.bind(view)

    fun bind(
        listaMangaSerieItemResponse: ListaMangaSerieItemResponse,
        onItemSelected: (Int) -> Unit,
        onAddOrRemove: (Int, Boolean) -> Unit,
        todosLosMangasAgregados: Boolean
    ) {
        val mangaNum = listaMangaSerieItemResponse.mangaNum
        val mangaNumText = if (mangaNum == mangaNum.toInt().toFloat()) {
            mangaNum.toInt().toString()
        } else {
            mangaNum.toString()
        }

        Log.i("Aristidevs", "SE están agregando el holder")

        binding.tvMangaListNumber.text = mangaNumText

        // Navegar a la actividad de detalles
        binding.imgBotonManga.setOnClickListener { onItemSelected(listaMangaSerieItemResponse.idManga) }

        // Verificar si el manga está agregado o no
        val mangaAgregado = listaMangaSerieItemResponse.estadoManga == 1

        // Si todos los mangas están agregados, mostrar todos los mangas
        // Si no, mostrar solo los mangas no agregados
        if (todosLosMangasAgregados || listaMangaSerieItemResponse.estadoManga == 0){
            binding.root.isVisible = true // Mostrar el elemento

            // Condicional para mostrar los botones según el estadoManga
            if (mangaAgregado) {
                // El manga está agregado, mostrar el botón para eliminar
                binding.btnEliminarMangaDetalle.isVisible = true
                binding.btnAgregarMangaDetalle.isVisible = false
            } else {
                // El manga no está agregado, mostrar el botón para agregar
                binding.btnEliminarMangaDetalle.isVisible = false
                binding.btnAgregarMangaDetalle.isVisible = true
            }

            // Botón para agregar manga
            binding.btnAgregarMangaDetalle.setOnClickListener {
                // Cambiar estadoManga a 1 (agregado)
                onAddOrRemove(listaMangaSerieItemResponse.idManga, true)

                // Actualizar visibilidad de los botones después de agregar
                binding.btnEliminarMangaDetalle.isVisible = true
                binding.btnAgregarMangaDetalle.isVisible = false
            }

            // Botón para eliminar manga
            binding.btnEliminarMangaDetalle.setOnClickListener {
                // Cambiar estadoManga a 0 (eliminado)
                onAddOrRemove(listaMangaSerieItemResponse.idManga, false)

                // Actualizar visibilidad de los botones después de eliminar
                binding.btnEliminarMangaDetalle.isVisible = false
                binding.btnAgregarMangaDetalle.isVisible = true
            }

            // Cargar imagen con Picasso
            Picasso.get().load(listaMangaSerieItemResponse.imgDir).into(binding.imgBotonManga)
        } else {
            // Ocultar el elemento si no cumple las condiciones
            binding.root.isVisible = false
        }
    }
}



