package com.aristidevs.androidmaster.buscador

import com.google.gson.annotations.SerializedName

class BuscadorDataResponse (
    @SerializedName("response") val response: String,
    @SerializedName("result") val busquedaLista: List<BusquedaListaItemResponse>
)

data class BusquedaListaItemResponse(
    @SerializedName("nombre") val serieNom: String,
)
