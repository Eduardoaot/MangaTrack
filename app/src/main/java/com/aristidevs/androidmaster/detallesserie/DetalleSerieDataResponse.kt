package com.aristidevs.androidmaster.manga
import com.google.gson.annotations.SerializedName

class DetalleSerieDataResponse (
    @SerializedName("response") val response: String,
    @SerializedName("result") val resultadoDetalles: ResultadoObjetoResponse
)

data class ResultadoObjetoResponse(
    @SerializedName("totalMangasEnColeccion") val totalMangasColeccion: Int,
    @SerializedName("totalMangasSinLeer") val totalMangasLeer: Int,
    @SerializedName("nombreSerie") val serieNom: String,
    @SerializedName("totalMangaSerie") val totalMangas: Int,
    @SerializedName("listaMangas") val detalleResultadoListaMnaga: List<ListaMangaSerieItemResponse>,
    @SerializedName("descripcionSerie") val serieDesc: String,
    @SerializedName("autorSerie") val serieAut: String,
    @SerializedName("porcentajeCompletado") val porcentajeMangaCompletado: Float,
    @SerializedName("porcentajePorLeer") val porcentajeMangaLeido: Float,
)

data class ListaMangaSerieItemResponse(
    @SerializedName("idManga") val idManga: Int,
    @SerializedName("mangaNum") val mangaNum: Float,
    @SerializedName("direccionImagen") val imgDir: String,
    @SerializedName("estadoManga") val estadoManga: Int

)