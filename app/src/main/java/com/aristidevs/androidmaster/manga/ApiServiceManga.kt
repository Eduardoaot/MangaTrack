package com.aristidevs.androidmaster.manga

import com.aristidevs.androidmaster.buscador.BuscadorDataResponse
import com.aristidevs.androidmaster.buscador.BuscadorDetallesDataResponse
import com.aristidevs.androidmaster.detallesmanga.AgregarMangaRequest
import com.aristidevs.androidmaster.principalcoleccion.ColeccionDetallesDataResponse
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
import com.aristidevs.androidmaster.principallectura.AgregarMetaRequest
import com.aristidevs.androidmaster.principallectura.AgregarMetaResponse
import com.aristidevs.androidmaster.principallectura.LecturaPendientesDataResponse
import com.aristidevs.androidmaster.principallectura.ObtenerMetaDataResponse
import com.aristidevs.androidmaster.principalmonetario.EstadisticasUsuarioResponse
import com.aristidevs.androidmaster.serie.SerieListDataResponse
import com.aristidevs.androidmaster.principalperfil.PerfilDataResponse
import com.aristidevs.androidmaster.principalpresupuestos.bolsa.AgregarPlanRequest
import com.aristidevs.androidmaster.principalpresupuestos.bolsa.AgregarPlanResponse
import com.aristidevs.androidmaster.principalpresupuestos.bolsa.GuardarBolsaRequest
import com.aristidevs.androidmaster.principalpresupuestos.bolsa.GuardarPresupuestoResponse
import com.aristidevs.androidmaster.principalpresupuestos.detallepresupuesto.ActualizarBolsaRequest
import com.aristidevs.androidmaster.principalpresupuestos.detallepresupuesto.DetallePresupuestoResponse
import com.aristidevs.androidmaster.principalpresupuestos.detallepresupuesto.GuardarEnMonetarioRequest
import com.aristidevs.androidmaster.principalpresupuestos.guardados.GuardadosDataResponse
import com.aristidevs.androidmaster.principalpresupuestos.presupuestos.PendienteListFaltantesDataResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceManga {

    @GET("/api/coleccion-manga/detalles/{id}")
    suspend fun getMangaByID(@Path("id") usuarioId: String): Response<MangaListDataResponse>

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
    suspend fun agregarManga(@Body actualizar: AgregarMangaRequest): Response<AgregarMangaResponse>

    @GET("/api/series/detalles/{id_usuario}/{id_serie}")
    suspend fun searchDataSerie(@Path("id_usuario") idUsuario: String, @Path("id_serie") idSerie: String): Response<DetalleSerieDataResponse>

    @GET("/api/manga/lectura/{id_usuario}")
    suspend fun searchDataLectura(@Path("id_usuario") idUsuario: String): Response<LecturaDataResponse>

    @GET("/api/manga/pendientes/{id_usuario}")
    suspend fun searchDataLecturaPendientes(@Path("id_usuario") idUsuario: String): Response<LecturaPendientesDataResponse>

    @POST("/api/usuarios/actualizar-meta")
    suspend fun actualizarMeta(@Body actualizar: AgregarMetaRequest): Response<AgregarMetaResponse>

    @GET("/api/usuarios/lectura-meta/{id_usuario}")
    suspend fun obtenerMeta(@Path("id_usuario") idUsuario: String): Response<ObtenerMetaDataResponse>

    @GET("/api/usuarios/perfil/{id_usuario}")
    suspend fun obtenerPerfil(@Path("id_usuario") idUsuario: String): Response<PerfilDataResponse>

    //Solicitudes para principal pendientes
    @GET("/api/mangas-pendientes/faltantes/{idUsuario}")
    suspend fun obtenerMangasFaltantes(@Path("idUsuario") idUsuario: String): Response<PendienteListFaltantesDataResponse>

    @POST("/api/mangas-pendientes/buscar")
    suspend fun buscarMangasPost(@Body body: AgregarPlanRequest): Response<AgregarPlanResponse>

    @POST("/api/presupuestos/crear")
    suspend fun botonGuardarPresupuesto(@Body actualizar: GuardarBolsaRequest): Unit

    @GET("/api/presupuestos/{idUsuario}")
    suspend fun obtenerPresupuestosGuardados(@Path("idUsuario") idUsuario: String): Response<GuardadosDataResponse>

    @DELETE("/api/presupuestos/eliminar/{idPresupuesto}")
    suspend fun borrarPresupuesto(@Path("idPresupuesto") idPresupuesto: Int): Response<Unit>

    @GET("/api/presupuestos/detalle/{idPresupuesto}")
    suspend fun obtenerDetallePresupuesto(@Path("idPresupuesto") idPresupuesto: Int): Response<DetallePresupuestoResponse>

    @PUT("/api/presupuestos/actualizar")
    suspend fun botonActualizarPresupuesto(@Body actualizar: ActualizarBolsaRequest): Unit

    @POST("/api/monetario/guardar")
    suspend fun guardarEnMonetario(@Body actualizar: GuardarEnMonetarioRequest): Response<Unit>

    @GET("/api/monetario/{idUsuario}")
    suspend fun obtenerEstadisticasUsuario(@Path("idUsuario") idUsuario: Int): Response<EstadisticasUsuarioResponse>
}