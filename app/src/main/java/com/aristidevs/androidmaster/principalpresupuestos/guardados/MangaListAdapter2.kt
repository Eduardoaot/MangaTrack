package com.aristidevs.androidmaster.principalpresupuestos.guardados

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ItemMangalistBinding
import com.aristidevs.androidmaster.manga.MangaListAdapter
import com.aristidevs.androidmaster.manga.MangaListHolder
import com.aristidevs.androidmaster.principalpresupuestos.guardados.MangaListHolder2
import com.aristidevs.androidmaster.manga.MangaListaItemResponse
import com.squareup.picasso.Picasso

class MangaListAdapter2(
    private var mangaList: List<MangaPresupuestoItemResponse> = emptyList(),
    private val onItemSelected: (Int) -> Unit
) : RecyclerView.Adapter<MangaListHolder2>() {

    // Función para actualizar la lista de manera segura
    fun updateList(newList: List<MangaPresupuestoItemResponse>) {
        mangaList = newList.toList() // Creamos una copia para evitar modificaciones externas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaListHolder2 {
        val binding = ItemMangalistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MangaListHolder2(binding, onItemSelected)
    }

    override fun onBindViewHolder(holder: MangaListHolder2, position: Int) {
        holder.bind(mangaList[position])
    }

    override fun getItemCount(): Int = mangaList.size

    // Filtro opcional para manejar listas vacías
    fun isEmpty(): Boolean = mangaList.isEmpty()
}

