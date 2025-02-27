package com.aristidevs.androidmaster.inicioyregistro

import com.google.gson.annotations.SerializedName

data class RegistroUsuarioResponse(
    @SerializedName("message") val message: String,  // Mensaje del servidor
    @SerializedName("userId") val userId: Int?  // ID del usuario, puede ser nulo si no se guarda correctamente
)
