package com.aristidevs.androidmaster.principalmonetario

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.databinding.ItemMangalistBinding
import com.aristidevs.androidmaster.databinding.ItemMonetarioBinding
import com.aristidevs.androidmaster.manga.MangaListaItemResponse
import com.squareup.picasso.Picasso

class MonetarioHolder (view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemMonetarioBinding.bind(view)

    fun bind(mejorAhorro: MejorAhorro) {

        binding.txtNumero.text = mejorAhorro.top.toString()
        binding.txtTotalMangas.text = "Total de mangas: ${mejorAhorro.totalMangas}"
        binding.txtFecha.text = "Fecha: ${mejorAhorro.fechaAhorro}"
        binding.txtTotalAhorrado.text = "Total ahorrado: ${mejorAhorro.totalAhorrado}"
    }
}