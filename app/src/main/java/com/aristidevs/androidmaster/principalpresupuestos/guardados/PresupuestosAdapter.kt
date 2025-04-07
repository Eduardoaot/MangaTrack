package com.aristidevs.androidmaster.principalpresupuestos.guardados


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ItemPresupuestosDetalleBinding

class PresupuestosAdapter(
    private var presupuestosList: List<PresupuestoItemResponse> = emptyList(),
    private val onItemSelected: (Int) -> Unit,
    private val onMangaSelected: (Int) -> Unit,  // Nuevo parámetro para el callback
    private val onDeleteSelected: (Int) -> Unit
) : RecyclerView.Adapter<PresupuestoViewHolder>() {

    fun updateList(newList: List<PresupuestoItemResponse>) {
        presupuestosList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresupuestoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_presupuestos_detalle, parent, false)
        return PresupuestoViewHolder(
            view,
            onMangaSelected,  // Pasamos el callback
            onItemSelected,
            onDeleteSelected
        )
    }

    override fun onBindViewHolder(holder: PresupuestoViewHolder, position: Int) {
        holder.bind(presupuestosList[position])
    }

    override fun getItemCount(): Int = presupuestosList.size
}






