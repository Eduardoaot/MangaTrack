package com.aristidevs.androidmaster.principalmonetario

import com.google.gson.annotations.SerializedName

data class EstadisticasUsuarioResponse(
    @SerializedName("response") val response: String,
    @SerializedName("totalComprados") val totalComprados: Int,
    @SerializedName("totalCompradosAnio") val totalCompradosAnio: Int,
    @SerializedName("totalMes") val totalMes: Int,
    @SerializedName("valorTotal") val valorTotal: Double,
    @SerializedName("ahorroTotal") val ahorroTotal: Double,
    @SerializedName("gastoTotal") val gastoTotal: Double,
    @SerializedName("mejoresAhorros") val mejoresAhorros: List<MejorAhorro>
)

data class MejorAhorro(
    @SerializedName("top") val top: Int,
    @SerializedName("totalMangas") val totalMangas: Int,
    @SerializedName("totalAhorrado") val totalAhorrado: Double,
    @SerializedName("fechaAhorro") val fechaAhorro: String
)