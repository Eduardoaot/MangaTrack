package com.aristidevs.androidmaster.principalpresupuestos.detallepresupuesto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.principalpresupuestos.bolsa.PlanElementosItemResponse

class PlanAdapterDetalle(
    private val onAddClick: (Int, Float, Boolean) -> Unit,
    private val onItemSelected: (Int) -> Unit
) : RecyclerView.Adapter<PlanHolderDetalle>() {

    var mangasConEstado: List<PlanElementosItemResponse> = emptyList()

    fun updateList(newList: List<PlanElementosItemResponse>) {
        mangasConEstado = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanHolderDetalle {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bolsa, parent, false)
        return PlanHolderDetalle(view)
    }

    override fun onBindViewHolder(holder: PlanHolderDetalle, position: Int) {
        val manga = mangasConEstado[position]
        holder.bind(manga, onAddClick, onItemSelected)
    }

    override fun getItemCount(): Int = mangasConEstado.size
}


