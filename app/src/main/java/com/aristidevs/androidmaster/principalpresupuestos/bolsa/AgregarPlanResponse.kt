package com.aristidevs.androidmaster.principalpresupuestos.bolsa

import com.google.gson.annotations.SerializedName

// Respuesta completa que mapea los mangas pendientes
class AgregarPlanResponse(
    @SerializedName("result") val response: String,
    @SerializedName("list") val mangasPendientes: List<PlanElementosItemResponse>
)

// DTO de cada manga pendiente
data class PlanElementosItemResponse(
    @SerializedName("idManga") val idManga: Int,            // ID del manga
    @SerializedName("mangaNum") val mangaNum: Float,        // Número del manga
    @SerializedName("mangaImg") val mangaImg: String,        // Imagen del manga
    @SerializedName("precio") val precio: Float,              // Precio del manga
    @SerializedName("nombreSerie") val serieNom: String
)