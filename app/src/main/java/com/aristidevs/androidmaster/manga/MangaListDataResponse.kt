package com.aristidevs.androidmaster.manga
import com.google.gson.annotations.SerializedName

class MangaListDataResponse (
    @SerializedName("response") val response: String,
    @SerializedName("result") val mangaLista: List<MangaListaItemResponse>
)

data class MangaListaItemResponse(
    @SerializedName("mangaNum") val mangaNum: String,
    @SerializedName("mangaImg") val mangaImg: String,
    @SerializedName("serieNom") val serieNom: String
)

