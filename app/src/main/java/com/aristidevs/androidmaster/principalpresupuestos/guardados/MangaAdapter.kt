package com.aristidevs.androidmaster.principalpresupuestos.guardados

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.databinding.ItemMangalistBinding


class MangasAdapter(private var mangas: List<MangaPresupuestoItemResponse>) : RecyclerView.Adapter<MangaListHolder>() {

    // Método para actualizar la lista
    fun updateList(newMangas: List<MangaPresupuestoItemResponse>) {
        mangas = newMangas
        notifyDataSetChanged()  // Notifica que los datos han cambiado
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaListHolder {
        val binding = ItemMangalistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MangaListHolder(binding)
    }

    override fun onBindViewHolder(holder: MangaListHolder, position: Int) {
        val manga = mangas[position]
        holder.bind(manga)
    }


    override fun getItemCount(): Int = mangas.size
}

