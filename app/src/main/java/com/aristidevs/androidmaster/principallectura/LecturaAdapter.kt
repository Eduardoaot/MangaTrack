package com.aristidevs.androidmaster.principallectura

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.manga.ItemMangaSinLeer
import com.aristidevs.androidmaster.manga.ListaMangaSerieItemResponse

class LecturaAdapter(
    private var listaMangasSinLeer: List<ItemMangaSinLeer> = emptyList(),
    private val onItemSelected: (Int) -> Unit, // Función para seleccionar el manga
    private val onSpecificButtonClick: (Int) -> Unit, // Función para el botón específico
    private val seleccionarParaLeer: (Int) -> Unit
) : RecyclerView.Adapter<LecturaHolder>() {

    private var mangasPorSerie: Map<String, List<ItemMangaSinLeer>> = emptyMap()

    // Función para actualizar la lista
    fun updateList(list: List<ItemMangaSinLeer>) {
        // Agrupar mangas por serie (nombre de la serie)
        mangasPorSerie = list
            .groupBy { it.serieNom }  // Agrupar por nombre de la serie
            .mapValues { entry -> entry.value.sortedBy { it.mangaNum.toFloat() } } // Ordenar cada grupo por mangaNum

        // Aplanar la lista de mangas, tomando el manga más bajo de cada serie
        listaMangasSinLeer = mangasPorSerie.values
            .map { it.first() } // Obtener el primero de cada grupo (el más bajo)

        // Notificar al adaptador que la lista ha cambiado
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LecturaHolder {
        return LecturaHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_lectura_detalle, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LecturaHolder, position: Int) {
        val manga = listaMangasSinLeer[position]

        // Obtener mangas de la misma serie dentro de la lista original
        val mangasDeLaSerie = mangasPorSerie[manga.serieNom] ?: emptyList()

        // Calcular cuántos mangas faltan dentro de la serie en la lista
        val mangasFaltantes = mangasDeLaSerie.size - 1  // Restamos 1 ya que el manga que estamos mostrando no cuenta como faltante

        // Asignar el texto de faltantes al TextView en el Holder
        if (mangasFaltantes >= 1) {
            holder.binding.txtNumeroFaltantes.text = "$mangasFaltantes+" // Mostrar el número de mangas faltantes
            holder.binding.txtNumeroFaltantes.isVisible = true // Mostrar el TextView
        } else {
            holder.binding.txtNumeroFaltantes.isVisible = false // Ocultar el TextView si solo hay uno
        }

        // Bindear el manga más reciente de cada serie
        holder.bind(manga, onItemSelected, onSpecificButtonClick, seleccionarParaLeer)
    }

    override fun getItemCount() = listaMangasSinLeer.size
}




