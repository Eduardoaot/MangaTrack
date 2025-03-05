package com.aristidevs.androidmaster.principallectura

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.manga.ItemMangaSinLeer
import com.aristidevs.androidmaster.manga.ListaMangaSerieItemResponse

class LecturaAdapter(
    private var listaMangasSinLeer: List<ItemMangaSinLeer> = emptyList(),
    private val onItemSelected: (Int) -> Unit, // Función para seleccionar el manga
    private val onSpecificButtonClick: (Int) -> Unit,// Función para el botón específico,
    private val seleccionarParaLeer: (Int) -> Unit,
) : RecyclerView.Adapter<LecturaHolder>() {

    // Función para actualizar la lista
    fun updateList(list: List<ItemMangaSinLeer>) {
        listaMangasSinLeer = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LecturaHolder {
        return LecturaHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_lectura_detalle, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LecturaHolder, position: Int) {
        holder.bind(listaMangasSinLeer[position], onItemSelected, onSpecificButtonClick, seleccionarParaLeer)
    }

    override fun getItemCount() = listaMangasSinLeer.size
}
