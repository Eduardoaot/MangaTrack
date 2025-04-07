package com.aristidevs.androidmaster.principalpresupuestos.guardados

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.databinding.ItemPresupuestosDetalleBinding

class PresupuestoViewHolder(
    view: View,
    private val onMangaSelected: (Int) -> Unit,  // Nuevo parámetro para el callback
    private val onItemSelected: (Int) -> Unit,
    private val onDeleteSelected: (Int) -> Unit
) : RecyclerView.ViewHolder(view) {
    private val binding = ItemPresupuestosDetalleBinding.bind(view)
    private lateinit var mangasAdapter: MangaListAdapter2

    init {
        mangasAdapter = MangaListAdapter2 { mangaId ->
            onMangaSelected(mangaId)  // Usamos el callback recibido
        }

        binding.rvMangas.layoutManager = LinearLayoutManager(
            binding.root.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvMangas.adapter = mangasAdapter
    }

    fun bind(presupuestoItemResponse: PresupuestoItemResponse) {
        binding.txtNombreBolsa.text = presupuestoItemResponse.nombrePresupuesto
        binding.txtTotalMangas.text = "Total mangas: ${presupuestoItemResponse.listaMangasPresupuesto.size}"
        binding.txtFecha.text = "Fecha: ${presupuestoItemResponse.fechaPresupuestoCreado}"

        // Calcular totales con descuento
        val (total, subtotal, descuentoCalculado) = calcularTotalesConDescuento(
            presupuestoItemResponse.listaMangasPresupuesto,
            presupuestoItemResponse.descuento
        )

        binding.txtTotalPrecio.text = "Total: $${String.format("%.2f", total)}"

        mangasAdapter.updateList(presupuestoItemResponse.listaMangasPresupuesto)

        binding.btnVerBolsa.setOnClickListener {
            onItemSelected(presupuestoItemResponse.idPresupuesto)
        }

        binding.btnEliminarBolsa.setOnClickListener {
            onDeleteSelected(presupuestoItemResponse.idPresupuesto)
        }
    }

    private fun calcularTotalesConDescuento(
        mangas: List<MangaPresupuestoItemResponse>,
        descuento: Float
    ): Triple<Float, Float, Float> {
        var subtotal = 0f
        var totalSum = 0f
        val mangasConDescuento = mutableListOf<Pair<MangaPresupuestoItemResponse, Boolean>>()

        mangas.forEach { manga ->
            subtotal += manga.precio

            if (descuento == 1.0f) { // Descuento 3x2 (100%)
                mangasConDescuento.add(manga to false)
                totalSum += manga.precio
            } else { // Descuento porcentual normal
                mangasConDescuento.add(manga to false)
                totalSum += manga.precio * (1 - descuento)
            }
        }

        // Aplicar lógica 3x2 si el descuento es 100%
        if (descuento == 1.0f) {
            val mangasOrdenados = mangas.sortedBy { it.precio }
            val totalGruposDe3 = mangasOrdenados.size / 3

            // Restamos los mangas más baratos (uno por cada grupo de 3)
            for (i in 0 until totalGruposDe3) {
                totalSum -= mangasOrdenados[i].precio
            }
        }

        val descuentoCalculado = subtotal - totalSum
        return Triple(totalSum, subtotal, descuentoCalculado)
    }
}






