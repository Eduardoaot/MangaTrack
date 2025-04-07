package com.aristidevs.androidmaster.detallesserie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.manga.ListaMangaSerieItemResponse


class DetalleSerieAdapter(
    var detalleResultadoListaMnaga: List<ListaMangaSerieItemResponse> = emptyList(),
    private val onItemSelected: (Int) -> Unit,
    private val onAddOrRemove: (Int, Boolean) -> Unit,
    private var todosLosMangasAgregados: Boolean = false // Nuevo parámetro
) : RecyclerView.Adapter<DetalleSerieHolder>() {

    // Función para actualizar la lista y el estado de "todosLosMangasAgregados"
    fun updateList(list: List<ListaMangaSerieItemResponse>, todosAgregados: Boolean) {
        detalleResultadoListaMnaga = list
        todosLosMangasAgregados = todosAgregados
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleSerieHolder {
        return DetalleSerieHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_detalle_serie, parent, false)
        )
    }

    override fun onBindViewHolder(viewholder: DetalleSerieHolder, position: Int) {
        viewholder.bind(detalleResultadoListaMnaga[position], onItemSelected, onAddOrRemove, todosLosMangasAgregados)
    }

    override fun getItemCount() = detalleResultadoListaMnaga.size
}


