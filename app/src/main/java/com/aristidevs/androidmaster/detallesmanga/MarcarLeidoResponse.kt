package com.aristidevs.androidmaster.detallesmanga

import com.google.gson.annotations.SerializedName

data class MarcarLeidoResponse(
    @SerializedName("message") val message: String,  // Mensaje del servidor
    @SerializedName("estadoLectura") val estadoLectura: Int  // ID del usuario, puede ser nulo si no se guarda correctamente
)