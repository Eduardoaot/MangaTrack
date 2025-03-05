package com.aristidevs.androidmaster.principallectura

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ItemMangaPendienteBinding
import com.squareup.picasso.Picasso

class MangasPendientesHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemMangaPendienteBinding.bind(view)

    fun bind(
        itemMangaPendiente: ItemMangaPendiente,
    ) {
        // Cargar la imagen con Picasso
        Picasso.get().load(itemMangaPendiente.mangaImg).into(binding.imagenMangaPendiente)

        // Configurar el CheckBox con el fondo circular
        binding.checkboxPendiente.setButtonDrawable(R.drawable.circular_checkbox_unchecked) // Establecer el fondo circular por defecto

        // Al hacer clic, agregar o eliminar el id_manga según el estado del CheckBox
        binding.checkboxPendiente.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Agregar ID a la lista de seleccionados
                SELECTED_IDES.add(itemMangaPendiente.mangaId)
                // Cambiar el drawable al circular marcado cuando está seleccionado
                binding.checkboxPendiente.setButtonDrawable(R.drawable.circular_checkbox_checked)
                Log.i("aristidevs", "Response data: $SELECTED_IDES")
            } else {
                // Eliminar ID de la lista de seleccionados
                SELECTED_IDES.remove(itemMangaPendiente.mangaId)
                // Cambiar el drawable al circular desmarcado cuando no está seleccionado
                binding.checkboxPendiente.setButtonDrawable(R.drawable.circular_checkbox_unchecked) // Este es el fondo circular no marcado
            }
        }
    }

    companion object {
        // Lista global de IDs seleccionados
        val SELECTED_IDES = mutableListOf<Int>()
    }
}



