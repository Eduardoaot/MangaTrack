package com.aristidevs.androidmaster.serie

import com.aristidevs.androidmaster.manga.MangaListaItemResponse
import com.google.gson.annotations.SerializedName

class SerieListDataResponse (
    @SerializedName("response") val response: String,
    @SerializedName("result") val serieLista: List<SerieListaItemResponse>
)
data class SerieListaItemResponse(
    @SerializedName("serieNom") val SerieNom: String,
    @SerializedName("seriePorcentaje") val seriePorcentaje: Double,
    @SerializedName("serieEstado") val serieEstado: String,
    @SerializedName("serieImagen") val serieImag: String,
    @SerializedName("serieId") val serieId: String
)