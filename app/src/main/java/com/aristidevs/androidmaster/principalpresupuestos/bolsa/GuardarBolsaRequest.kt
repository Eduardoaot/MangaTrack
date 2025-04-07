package com.aristidevs.androidmaster.principalpresupuestos.bolsa

data class GuardarBolsaRequest(
    val mangas_bolsa: MutableList<Int>,
    val descuento: Float,
    val nombreBolsa: String,
    val idUsuario: Int,
)