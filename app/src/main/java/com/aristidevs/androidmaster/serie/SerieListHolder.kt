package com.aristidevs.androidmaster.serie

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ItemSerielistBinding

import com.squareup.picasso.Picasso

class SerieListHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemSerielistBinding.bind(view)

    fun bind(
        serieListaItemResponse: SerieListaItemResponse,
        onItemSelected: (Int) -> Unit
    ) {
        // Setear el nombre de la serie
        binding.tvSerieTitle.text = serieListaItemResponse.SerieNom

        // Setear el estado de la serie
        binding.tvSerieStatus.text = serieListaItemResponse.serieEstado

        // Cargar la imagen de la serie usando Picasso
        Picasso.get().load(serieListaItemResponse.serieImag).into(binding.ivSerieCover)

        // Verificar el estado de la serie para manejar el ProgressBar
        when (serieListaItemResponse.serieEstado) {
            "No completar" -> {
                // Asegurarse de que el ProgressBar se vea y tiene el valor correcto
                binding.pbSerieProgress.progressDrawable =
                    ContextCompat.getDrawable(binding.root.context, R.drawable.progressbar_layer2)
                binding.pbSerieProgress.progress = (serieListaItemResponse.seriePorcentaje).toInt() // Convertir a porcentaje
                binding.pbSerieProgress.visibility = View.VISIBLE
            }
            "Tomo único" -> {
                // Si es un "Tomo único", ocultar el ProgressBar
                binding.pbSerieProgress.visibility = View.GONE
            }
            else -> {
                // Para otros estados, se muestra el ProgressBar con el porcentaje
                binding.pbSerieProgress.progress = (serieListaItemResponse.seriePorcentaje).toInt()
                binding.pbSerieProgress.visibility = View.VISIBLE
            }
        }

        binding.ibActionButton.setOnClickListener { onItemSelected(serieListaItemResponse.serieId.toInt()) }
    }

}
