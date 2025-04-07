package com.aristidevs.androidmaster.principalpresupuestos.presupuestos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R

class PendienteMangaAdapter(
    private val onAddClick: (Int, Float, Boolean) -> Unit,
    private val onItemSelected: (Int) -> Unit
) : RecyclerView.Adapter<PendienteMangaHolder>() {

    // Cambiar el tipo de lista a PendienteMangaItemConEstado
    var mangasConEstado: List<PendienteMangaItemConEstado> = emptyList()

    fun updateList(newList: List<PendienteMangaItemConEstado>) {
        mangasConEstado = newList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendienteMangaHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_presupuestos, parent, false)
        return PendienteMangaHolder(view)
    }

    override fun onBindViewHolder(holder: PendienteMangaHolder, position: Int) {
        val manga = mangasConEstado[position]
        holder.bind(
            manga,
            onAddClick,
            onItemSelected
        )
    }


    override fun getItemCount(): Int = mangasConEstado.size
}





