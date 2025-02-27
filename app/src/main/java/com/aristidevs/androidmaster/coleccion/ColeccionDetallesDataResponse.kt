package com.aristidevs.androidmaster.coleccion

import com.google.gson.annotations.SerializedName

class ColeccionDetallesDataResponse (
    @SerializedName("response") val response: String,
    @SerializedName("result") val coleccionDetalle: coleccionItemResponse
)

data class coleccionItemResponse(
    @SerializedName("totalMangas") val mangasTot: String,
    @SerializedName("totalSeries") val seriesTot: String,
    @SerializedName("seriesPorCompletar") val seriesPorComTot: String,
    @SerializedName("seriesCompletadas") val seriesComTot: String,
    @SerializedName("porcentajeSeries") val seriesPorentaje: Double
)
