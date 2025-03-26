package com.aristidevs.androidmaster.principallectura.agregarpendientes

import MangasPendientesAdapter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ActivityAgregarPendientesBinding
import com.aristidevs.androidmaster.databinding.ActivityLecturaBinding
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaActivity
import com.aristidevs.androidmaster.detallesmanga.MarcarLeidoRequest
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.network.RetrofitClient
import com.aristidevs.androidmaster.principallectura.LecturaAdapter
import com.aristidevs.androidmaster.principallectura.LecturaPendientesDataResponse
import com.aristidevs.androidmaster.principallectura.MangasPendientesHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class AgregarPendientesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarPendientesBinding
    private lateinit var adapter: MangasPendientesAdapter
    private lateinit var retrofit: Retrofit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarPendientesBinding.inflate(layoutInflater) // Infla el binding
        setContentView(binding.root) // Usa la vista raíz del binding
        retrofit = RetrofitClient.getRetrofit()
        val id_usuario: Int = intent.getIntExtra(DetalleMangaActivity.USER_ID, -1)


        binding.btnflecha.setOnClickListener {
            // Vaciar la lista global de IDs seleccionados
            MangasPendientesHolder.SELECTED_IDES.clear()

            // Realizar la acción de volver atrás
            onBackPressedDispatcher.onBackPressed()
        }
        initUI(id_usuario)
    }

    private fun initUI(idUsuario: Int) {
        adapter = MangasPendientesAdapter()
        binding.recyclerViewMangasPendientes.layoutManager =
            GridLayoutManager(this, 3) // 3 columnas
        binding.recyclerViewMangasPendientes.setHasFixedSize(true) // Si las vistas no cambian de tamaño, esto mejora rendimiento
        binding.recyclerViewMangasPendientes.adapter = adapter

        searchDataPendientes(idUsuario)

        binding.btnConfirmarSeleccion.setOnClickListener {
            AgregarLectura(idUsuario)
        }

        binding.btnCancelar.setOnClickListener {
            MangasPendientesHolder.SELECTED_IDES.clear()
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun AgregarLectura(idUsuario: Int) {
        // Aquí recorremos todos los ID de manga seleccionados
        for (idManga in MangasPendientesHolder.SELECTED_IDES) {
            // Creamos el objeto de solicitud para cada id_manga
            val marcarLeidoRequest = MarcarLeidoRequest(idManga, idUsuario, 2)

            // Realizamos el POST con Retrofit dentro de un Coroutine
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Llamada al servicio API
                    val responsePost = retrofit.create(ApiServiceManga::class.java)
                        .modificarLectura(marcarLeidoRequest)

                    if (responsePost.isSuccessful) {
                        val responseBody = responsePost.body()
                        if (responseBody != null) {
                            // Si la respuesta es exitosa, actualizamos la UI
                            Log.i("MarcarLeido", "Estado de lectura actualizado a: ${responseBody.estadoLectura}")
                            runOnUiThread {
                                initUI(idUsuario) // Actualizamos la UI después de cada cambio
                                MangasPendientesHolder.SELECTED_IDES.clear()
                            }
                        }
                    } else {
                        Log.e("MarcarLeido", "Error en la actualización del estado de lectura para el manga ID: $idManga")
                    }
                } catch (e: Exception) {
                    Log.e("MarcarLeido", "Error al hacer la solicitud POST para el manga ID: $idManga: ${e.message}")
                }
            }
        }

    }

    private fun searchDataPendientes(idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            // Llamamos al servicio de la API para obtener los datos
            val myResponse = retrofit.create(ApiServiceManga::class.java).searchDataLecturaPendientes(idUsuario.toString())

            if (myResponse.isSuccessful) {
                Log.i("aristidevs", "Petición exitosa")
                val response: LecturaPendientesDataResponse? = myResponse.body()
                if (response != null) {
                    // Si la respuesta no es nula, logueamos los datos recibidos
                    Log.i("aristidevs", "Response data: $response")  // Log de toda la respuesta
                    // Actualiza los valores de los TextViews con la data recibida
                    runOnUiThread {
                        val lecturaData = response.detalles
                        adapter.updateList(response.detalles)  // Actualizamos la lista del adaptador
                    }
                }
            } else {
                Log.i("aristidevs", "Error en la petición: ${myResponse.code()} - ${myResponse.message()}")
                runOnUiThread {
                    // Mostrar un mensaje de error, si lo deseas
                }
            }
        }
    }
}