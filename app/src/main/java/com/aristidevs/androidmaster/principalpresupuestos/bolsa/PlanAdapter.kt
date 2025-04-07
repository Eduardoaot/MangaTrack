package com.aristidevs.androidmaster.principalpresupuestos.bolsa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R

class PlanAdapter(
    private val onAddClick: (Int, Float, Boolean) -> Unit,
    private val onItemSelected: (Int) -> Unit
) : RecyclerView.Adapter<PlanHolder>() {

    var mangasConEstado: List<PlanElementosItemResponse> = emptyList()

    fun updateList(newList: List<PlanElementosItemResponse>) {
        mangasConEstado = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bolsa, parent, false)
        return PlanHolder(view)
    }

    override fun onBindViewHolder(holder: PlanHolder, position: Int) {
        val manga = mangasConEstado[position]
        holder.bind(manga, onAddClick, onItemSelected)
    }

    override fun getItemCount(): Int = mangasConEstado.size
}


