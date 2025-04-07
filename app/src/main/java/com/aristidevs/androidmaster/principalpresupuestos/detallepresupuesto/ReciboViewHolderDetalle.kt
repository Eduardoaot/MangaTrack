package com.aristidevs.androidmaster.principalpresupuestos.detallepresupuesto

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.databinding.ItemReciboBolsaBinding
import com.aristidevs.androidmaster.principalpresupuestos.bolsa.PlanElementosItemResponse

class ReciboViewHolderDetalle(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemReciboBolsaBinding.bind(view)

    fun bind(manga: PlanElementosItemResponse, descuento: Float, esDescuento100: Boolean) {
        val precioOriginal = manga.precio
        var precioConDescuento = precioOriginal * (1 - descuento)
        var descuentoManga = precioOriginal - precioConDescuento

        // Si el descuento es 100%, ajustamos el precio y el descuento
        if (descuento == 1f) {
            precioConDescuento = precioOriginal // El precio con descuento es igual al original
            descuentoManga = 0f // El descuento es 0, porque no hay descuento en realidad
        }

        // Si el descuento es del 100%, aplicar descuento completo
        if (esDescuento100) {
            precioConDescuento = 0f
            descuentoManga = precioOriginal // El descuento es el precio completo
        }

        // Establecer los valores de texto en los TextViews
        binding.txtNombreManga.text = manga.serieNom
        binding.txtNumeroManga.text = "Nº: ${manga.mangaNum.toInt()}"
        binding.txtPrecio.text = "${String.format("%.2f", precioOriginal)}$"
        binding.txtDescuento.text = "-${String.format("%.2f", descuentoManga)}$"
        binding.txtTotal.text = "${String.format("%.2f", precioConDescuento)}$"
    }
}
