package com.aristidevs.androidmaster.manga

import com.aristidevs.androidmaster.coleccion.ColeccionDetallesDataResponse
//import com.aristidevs.androidmaster.serie.SerieListDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiServiceManga {

//    @GET("/api/coleccion-manga/detalles/{id}")
//    suspend fun getMangaByID(@Path("id") usuarioId: String): Response<MangaListDataResponse>
//
//    @GET("/api/coleccion-manga/detalles/{nombre_serie}")
//    suspend fun searchMangaByName(@Path("id") usuarioId: String): Response<MangaListDataResponse>

//    @GET("/api/coleccion-manga/series/{id}")
//    suspend fun searchSerieById(@Path("id") usuarioIdSerie: String): Response<SerieListDataResponse>

    @GET("/api/coleccion-manga/usuario-detalles/{id}")
    suspend fun searchColeccionById(@Path("id") usuarioIdSerie: String): Response<ColeccionDetallesDataResponse>
}