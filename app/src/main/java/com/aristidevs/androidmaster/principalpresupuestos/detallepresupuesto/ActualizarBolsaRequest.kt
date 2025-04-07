package com.aristidevs.androidmaster.principalpresupuestos.detallepresupuesto

data class ActualizarBolsaRequest(
    val idPresupuesto: Int,
    val nombrePresupuesto: String,
    val descuento: Float,
    val mangas_bolsa: MutableList<Int>,
)