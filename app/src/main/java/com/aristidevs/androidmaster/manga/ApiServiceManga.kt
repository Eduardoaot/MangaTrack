package com.aristidevs.androidmaster.manga

import com.aristidevs.androidmaster.buscador.BuscadorDataResponse
import com.aristidevs.androidmaster.buscador.BuscadorDetallesDataResponse
import com.aristidevs.androidmaster.coleccion.ColeccionDetallesDataResponse
import com.aristidevs.androidmaster.detallesmanga.AgregarMangaResponse
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaDataResponse
import com.aristidevs.androidmaster.detallesmanga.EliminarMangaResponse
import com.aristidevs.androidmaster.detallesmanga.EliminarYAgregarMangaRequest
import com.aristidevs.androidmaster.detallesmanga.MarcarLeidoRequest
import com.aristidevs.androidmaster.detallesmanga.MarcarLeidoResponse
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
import retrofit2.http.Query

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

    @GET("/api/series/buscar")
    suspend fun searchSeries(@Query("query") query: String): Response<BuscadorDataResponse>

    @GET("/api/series/buscar")
    suspend fun searchSearchDataSeries(@Query("query") query: String): Response<BuscadorDetallesDataResponse>

    @GET("/api/manga/{id_manga}/{id_usurious}/detalles")
    suspend fun searchAllDataManga(@Path("id_manga") idManga: String, @Path("id_usurious") idUsuario: String): Response<DetalleMangaDataResponse>

    @POST("/api/manga/actualizarEstadoLectura")
    suspend fun modificarLectura(@Body actualizar: MarcarLeidoRequest): Response<MarcarLeidoResponse>

    @POST("/api/coleccion-manga/eliminar-manga")
    suspend fun eliminarManga(@Body actualizar: EliminarYAgregarMangaRequest): Response<EliminarMangaResponse>

    @POST("/api/coleccion-manga/agregar-manga")
    suspend fun agregarManga(@Body actualizar: EliminarYAgregarMangaRequest): Response<AgregarMangaResponse>

    @GET("/api/seriesdetalles/detalles/{id_usuario}/{id_serie}")
    suspend fun searchDataSerie(@Path("id_usuario") idManga: String, @Path("id_serie") idUsuario: String): Response<DetalleSerieDataResponse>



}