package com.aristidevs.androidmaster.principalpresupuestos.guardados

import com.google.gson.annotations.SerializedName

data class GuardadosDataResponse(
    @SerializedName("response") val response: String,
    @SerializedName("result") val presupuestos: List<PresupuestoItemResponse>
)

data class PresupuestoItemResponse(
    @SerializedName("idPresupuesto") val idPresupuesto: Int,
    @SerializedName("nombrePresupuesto") val nombrePresupuesto: String,
    @SerializedName("idUsuario") val idUsuario: Int,
    @SerializedName("descuento") val descuento: Float,
    @SerializedName("fechaPresupuestoCreado") val fechaPresupuestoCreado: String,
    @SerializedName("listaMangasPresupuesto") val listaMangasPresupuesto: List<MangaPresupuestoItemResponse>
)

data class MangaPresupuestoItemResponse(
    @SerializedName("idManga") val idManga: Int,
    @SerializedName("mangaNum") val mangaNum: Float,
    @SerializedName("direccionMangaImg") val mangaImg: String,
    @SerializedName("precio") val precio: Float,
    @SerializedName("serieNom") val serieNom: String
)
