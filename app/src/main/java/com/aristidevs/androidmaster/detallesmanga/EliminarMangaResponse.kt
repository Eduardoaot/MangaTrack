package com.aristidevs.androidmaster.detallesmanga

import com.google.gson.annotations.SerializedName

data class EliminarMangaResponse(
    @SerializedName("message") val message: String,  // Mensaje del servidor
)