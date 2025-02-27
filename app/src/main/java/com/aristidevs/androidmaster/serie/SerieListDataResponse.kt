package com.aristidevs.androidmaster.serie

import com.google.gson.annotations.SerializedName

data class SerieListaItemResponse(
    @SerializedName("serieNom") val SerieNom: String,
    @SerializedName("seriePorcentaje") val seriePorcentaje: Double,
    @SerializedName("serieEstado") val serieEstado: String,
    @SerializedName("serieImagen") val serieImag: String,
    @SerializedName("serieId") val serieId: String
)