package com.aristidevs.androidmaster.principallectura

import com.google.gson.annotations.SerializedName

data class ObtenerMetaDataResponse(
    @SerializedName("mangasLeidosMes") val mangasLeidosTot: Int,
    @SerializedName("meta") val mangasLeidosMes: Int,

)