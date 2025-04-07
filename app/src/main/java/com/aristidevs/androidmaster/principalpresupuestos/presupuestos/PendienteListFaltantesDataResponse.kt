package com.aristidevs.androidmaster.principalpresupuestos.presupuestos

import com.google.gson.annotations.SerializedName

// Respuesta completa que mapea los mangas pendientes
class PendienteListFaltantesDataResponse(
    @SerializedName("result") val response: String,
    @SerializedName("list") val mangasPendientes: List<PendienteMangaItemResponse>
)

// DTO de cada manga pendiente
data class PendienteMangaItemResponse(
    @SerializedName("idManga") val idManga: Int,            // ID del manga
    @SerializedName("mangaNum") val mangaNum: Float,        // Número del manga
    @SerializedName("mangaImg") val mangaImg: String,        // Imagen del manga
    @SerializedName("precio") val precio: Float,              // Precio del manga
    @SerializedName("nombreSerie") val serieNom: String
)