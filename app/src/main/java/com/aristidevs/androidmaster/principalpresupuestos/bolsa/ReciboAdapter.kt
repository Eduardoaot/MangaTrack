package com.aristidevs.androidmaster.principalpresupuestos.bolsa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R

class ReciboAdapter(
    private var mangasRecibo: List<Pair<PlanElementosItemResponse, Boolean>>,  // Lista de mangas con su flag de descuento
    private val descuento: Float
) : RecyclerView.Adapter<ReciboViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReciboViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recibo_bolsa, parent, false)
        return ReciboViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReciboViewHolder, position: Int) {
        val (manga, esDescuento100) = mangasRecibo[position]
        holder.bind(manga, descuento, esDescuento100)
    }

    override fun getItemCount(): Int = mangasRecibo.size

    // Método para actualizar la lista de mangas
    fun updateList(newList: List<Pair<PlanElementosItemResponse, Boolean>>) {
        mangasRecibo = newList
        notifyDataSetChanged()
    }
}










