package com.aristidevs.androidmaster.detallesmanga

import com.google.gson.annotations.SerializedName

data class AgregarMangaResponse(
    @SerializedName("message") val message: String,  // Mensaje del servidor
    @SerializedName("idManga") val idMangaAgregado: Int  // ID del usuario, puede ser nulo si no se guarda correctamente
)