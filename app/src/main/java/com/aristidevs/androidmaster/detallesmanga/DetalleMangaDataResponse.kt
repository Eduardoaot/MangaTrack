package com.aristidevs.androidmaster.detallesmanga

import com.google.gson.annotations.SerializedName

data class DetalleMangaDataResponse(
    @SerializedName("response") val response: String,
    @SerializedName("result") val detallesManga: List<DetalleMangaItemResponse>
)

data class DetalleMangaItemResponse(
    @SerializedName("tituloSerie") val serieNom: String,
    @SerializedName("numeroManga") val mangaNum: Float,
    @SerializedName("estadoLectura") val estadoLectura: String,
    @SerializedName("descripcion") val mangaDesc: String,
    @SerializedName("nombreAutor") val serieAut: String,
    @SerializedName("imagenManga") val mangaImg: String,
    @SerializedName("estadoAgregado") val estadoAgregado: Long
)
