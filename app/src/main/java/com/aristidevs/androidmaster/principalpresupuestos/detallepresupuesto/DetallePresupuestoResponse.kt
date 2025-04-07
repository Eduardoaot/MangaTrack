package com.aristidevs.androidmaster.principalpresupuestos.detallepresupuesto

import com.google.gson.annotations.SerializedName

data class DetallePresupuestoResponse(
    @SerializedName("response") val response: String,
    @SerializedName("result") val resultado: DetallePresupuestoResult
)

data class DetallePresupuestoResult(
    @SerializedName("idPresupuesto") val idPresupuesto: Int,
    @SerializedName("nombrePresupuesto") val nombrePresupuesto: String,
    @SerializedName("idUsuario") val idUsuario: Int,
    @SerializedName("descuento") val descuento: Float,
    @SerializedName("fechaPresupuestoCreado") val fechaPresupuestoCreado: String,
    @SerializedName("listaMangasPresupuesto") val listaMangasPresupuesto: List<MangaPresupuestoItem>
)

data class MangaPresupuestoItem(
    @SerializedName("idManga") val idManga: Int,
    @SerializedName("mangaNum") val mangaNum: Float,
    @SerializedName("direccionMangaImg") val mangaImg: String,
    @SerializedName("precio") val precio: Float,
    @SerializedName("serieNom") val serieNom: String
)