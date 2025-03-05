package com.aristidevs.androidmaster.manga

import com.google.gson.annotations.SerializedName

class LecturaDataResponse(
    @SerializedName("response") val response: String,
    @SerializedName("result") val lecturaDataTotal: ResultadoDetalles
)

data class ResultadoDetalles(
    @SerializedName("totalMangasLeidos") val mangasLeidosTot: Int,
    @SerializedName("totalMangaLeidosMes") val mangasLeidosMes: Int,
    @SerializedName("totalMangaLeidosAnio") val mangasLeidosAnio: Int,
    @SerializedName("listaMangasSinLeer") val listaMangasSinLeer: List<ItemMangaSinLeer>,
    @SerializedName("listaMangasCompradosData") val mangasAniadidos: List<Int>,
    @SerializedName("listaMangasLeidosData") val mangasLeidos: List<Int>,
    @SerializedName("listaMangasMeses") val months: List<String>
)

data class ItemMangaSinLeer(
    @SerializedName("idManga") val mangaId: Int,
    @SerializedName("serieNom") val serieNom: String,
    @SerializedName("mangaSum") val mangaNum: Float,
    @SerializedName("direccionManga") val mangaImg: String
)
