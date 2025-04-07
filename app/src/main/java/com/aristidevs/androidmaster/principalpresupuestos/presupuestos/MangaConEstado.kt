package com.aristidevs.androidmaster.principalpresupuestos.presupuestos

data class PendienteMangaItemConEstado(
    val idManga: Int,
    val mangaImg: String,
    val mangaNum: Float,
    val precio: Float,
    val serieNom: String,  // Agregar el nombre de la serie
    val isAdded: Boolean
)



