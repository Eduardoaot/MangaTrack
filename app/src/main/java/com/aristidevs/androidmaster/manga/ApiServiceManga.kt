package com.aristidevs.androidmaster.manga

import com.aristidevs.androidmaster.coleccion.ColeccionDetallesDataResponse
import com.aristidevs.androidmaster.inicioyregistro.LoginRequest
import com.aristidevs.androidmaster.inicioyregistro.RegistroUsuarioRequest
import com.aristidevs.androidmaster.inicioyregistro.RegistroUsuarioResponse
import com.aristidevs.androidmaster.inicioyregistro.UsuarioResponse
import com.aristidevs.androidmaster.serie.SerieListDataResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiServiceManga {

    @GET("/api/coleccion-manga/detalles/{id}")
    suspend fun getMangaByID(@Path("id") usuarioId: String): Response<MangaListDataResponse>

    @GET("/api/coleccion-manga/detalles/{nombre_serie}")
    suspend fun searchMangaByName(@Path("id") usuarioId: String): Response<MangaListDataResponse>

    @GET("/api/coleccion-manga/series/{id}")
    suspend fun searchSerieById(@Path("id") usuarioIdSerie: String): Response<SerieListDataResponse>

    @GET("/api/coleccion-manga/usuario-detalles/{id}")
    suspend fun searchColeccionById(@Path("id") usuarioIdSerie: String): Response<ColeccionDetallesDataResponse>

    // Método POST para registrar un usuario
    @POST("/api/usuarios/guardar")
    suspend fun registrarUsuario(@Body usuario: RegistroUsuarioRequest): Response<RegistroUsuarioResponse>

    // Método GET para autenticar un usuario
    @POST("/api/usuarios/autenticar")
    suspend fun autenticarUsuario(@Body usuario: LoginRequest): Response<UsuarioResponse>

}