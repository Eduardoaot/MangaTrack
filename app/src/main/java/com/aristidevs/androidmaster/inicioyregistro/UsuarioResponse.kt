package com.aristidevs.androidmaster.inicioyregistro

import com.google.gson.annotations.SerializedName

data class UsuarioResponse(
    @SerializedName("success") val success: Boolean, // Indica si el registro fue exitoso o no
    @SerializedName("message") val message: String,  // Mensaje del servidor
    @SerializedName("userId") val userId: String?    // ID del usuario (puede ser nulo si hay error)
)

