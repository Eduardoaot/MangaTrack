package com.aristidevs.androidmaster.principallectura

import com.google.gson.annotations.SerializedName

class LecturaPendientesDataResponse(
    @SerializedName("response") val response: String,
    @SerializedName("detalles") val detalles: List<ItemMangaPendiente>
)

data class ItemMangaPendiente(
    @SerializedName("idManga") val mangaId: Int,
    @SerializedName("mangaNum") val mangaNum: Float,
    @SerializedName("direccionMangaImg") val mangaImg: String

)
