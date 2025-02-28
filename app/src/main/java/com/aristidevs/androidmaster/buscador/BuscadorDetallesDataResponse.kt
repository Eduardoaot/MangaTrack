package com.aristidevs.androidmaster.buscador

import com.google.gson.annotations.SerializedName

class BuscadorDetallesDataResponse (
    @SerializedName("response") val response: String,
    @SerializedName("result") val busquedaListaDetalles: List<BusquedaListaDetallesItemResponse>
)

data class BusquedaListaDetallesItemResponse(
    @SerializedName("idSerie") val serieId: Int,
    @SerializedName("nombre") val serieNom: String,
    @SerializedName("totalTomos") val serieTotalTomos: String,
    @SerializedName("imagenPrimerTomo") val serieImg: String
)