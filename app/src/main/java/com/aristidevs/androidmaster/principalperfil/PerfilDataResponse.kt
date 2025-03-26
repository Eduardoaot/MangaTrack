package com.aristidevs.androidmaster.principalperfil

import com.google.gson.annotations.SerializedName

class PerfilDataResponse(
    @SerializedName("email") val email: String,
    @SerializedName("name") val nombre: String,
    @SerializedName("user") val usuario: String,
)