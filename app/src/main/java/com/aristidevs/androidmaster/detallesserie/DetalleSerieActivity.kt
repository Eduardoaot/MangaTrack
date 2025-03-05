package com.aristidevs.androidmaster.detallesserie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

import com.aristidevs.androidmaster.databinding.ActivityDetalleSerieBinding

import com.aristidevs.androidmaster.detallesmanga.DetalleMangaActivity
import com.aristidevs.androidmaster.detallesmanga.EliminarYAgregarMangaRequest
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.manga.DetalleSerieDataResponse

import com.aristidevs.androidmaster.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class DetalleSerieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleSerieBinding
    private lateinit var adapter: DetalleSerieAdapter
    private lateinit var retrofit: Retrofit


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleSerieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = RetrofitClient.getRetrofit()

        val id_serie: Int = intent.getIntExtra(DetalleMangaActivity.SERIE_ID, -1)
        val id_usuario: Int = intent.getIntExtra(DetalleMangaActivity.USER_ID, -1)

        initUI(id_usuario, id_serie)

        binding.btnflecha.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        val id_serie: Int = intent.getIntExtra(DetalleMangaActivity.SERIE_ID, -1)
        val id_usuario: Int = intent.getIntExtra(DetalleMangaActivity.USER_ID, -1)

        initUI(id_usuario, id_serie)
    }

    private fun initUI(idUsuario: Int, idSerie: Int) {
        adapter = DetalleSerieAdapter(
            onItemSelected = { idManga -> navigateToDetail(idManga, idUsuario) },
            onAddOrRemove = { idManga, isAdded ->
                if (isAdded) {
                    // Llamar a la función para agregar el manga
                    AgregarManga(idManga, idUsuario, idSerie)
                } else {
                    // Llamar a la función para eliminar el manga
                    EliminarManga(idManga, idUsuario, idSerie)
                }
            }
        )
        binding.rvMangasListaAAgregar.setHasFixedSize(true)
        binding.rvMangasListaAAgregar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMangasListaAAgregar.adapter = adapter


    searchSerieDetails(idUsuario, idSerie)
    }

    private fun searchSerieDetails(idUsuario: Int, idSerie: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse = retrofit.create(ApiServiceManga::class.java).searchDataSerie(
                idUsuario.toString(), idSerie.toString()
            )

            if (myResponse.isSuccessful) {
                Log.i("DetalleSerie", "Consulta exitosa")
                val response: DetalleSerieDataResponse? = myResponse.body()

                if (response != null) {
                    if (response != null) {
                        runOnUiThread {
                            val detalles = response.resultadoDetalles
                            binding.txtTituloSerie.text = detalles.serieNom
                            binding.txtTotalMangasSerie.text = "Total: ${detalles.totalMangas} mangas"
                            binding.txtDescripcion.text = detalles.serieDesc
                            binding.txtAutor.text = detalles.serieAut

                            binding.txtTotalMangaTengo.text = detalles.totalMangasColeccion.toString()
                            binding.txtTotalFaltanLeer.text = detalles.totalMangasLeer.toString()
                            binding.bdpColeccion.progress = detalles.porcentajeMangaCompletado.toInt()
                            binding.bdpLectura.progress = detalles.porcentajeMangaLeido.toInt()

                            // Verificamos si la colección está completa o incompleta
                            if (detalles.totalMangas == detalles.totalMangasColeccion) {
                                // Colección completa
                                binding.txtEstadoIncompleto.isVisible = false
                                binding.txtNumeroEstadoIncompleto.isVisible = false
                                binding.txtEstadoCompletado.isVisible = true
                                binding.imgCorazon.isVisible = true
                            } else {
                                // Colección incompleta
                                val mangasFaltantes = detalles.totalMangas - detalles.totalMangasColeccion
                                binding.txtEstadoIncompleto.isVisible = true
                                binding.txtNumeroEstadoIncompleto.isVisible = true
                                binding.txtEstadoCompletado.isVisible = false
                                binding.imgCorazon.isVisible = false
                                binding.txtNumeroEstadoIncompleto.text = mangasFaltantes.toString()
                            }

                            val todosLosMangasAgregados = detalles.porcentajeMangaCompletado == 100f

                            val listaFiltrada = if (todosLosMangasAgregados) {
                                detalles.detalleResultadoListaMnaga // Mostrar todos los mangas
                            } else {
                                detalles.detalleResultadoListaMnaga.filter { it.estadoManga == 0 } // Mostrar solo los no agregados
                            }

                            //  Aquí corregimos el acceso a la lista de mangas
                            adapter.updateList(listaFiltrada, todosLosMangasAgregados)
                        }
                    }

                }

            } else {
                Log.e("DetalleSerie", "Error en la consulta: ${myResponse.code()}")
            }
        }
    }

    private fun EliminarManga(idManga: Int, idUsuario: Int, idSerie: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = EliminarYAgregarMangaRequest(idManga, idUsuario)

            try {
                // Realiza la llamada a la API para eliminar el manga
                val myResponse = retrofit.create(ApiServiceManga::class.java).eliminarManga(request)

                if (myResponse.isSuccessful) {
                    // Si la respuesta es exitosa
                    val message = myResponse.body()?.message ?: "Manga eliminado correctamente"

                    // Actualizamos la interfaz en el hilo principal
                    runOnUiThread {
                        Toast.makeText(this@DetalleSerieActivity, message, Toast.LENGTH_SHORT).show()
                        CoroutineScope(Dispatchers.IO).launch {
                            val myResponse = retrofit.create(ApiServiceManga::class.java).searchDataSerie(
                                idUsuario.toString(), idSerie.toString()
                            )
                            if (myResponse.isSuccessful) {
                                Log.i("DetalleSerie", "Consulta exitosa")
                                val response: DetalleSerieDataResponse? = myResponse.body()
                                if (response != null) {
                                    if (response != null) {
                                        runOnUiThread {
                                            val detalles = response.resultadoDetalles
                                            val todosLosMangasAgregados = true

                                            val listaFiltrada = if (todosLosMangasAgregados) {
                                                detalles.detalleResultadoListaMnaga // Mostrar todos los mangas
                                            } else {
                                                detalles.detalleResultadoListaMnaga.filter { it.estadoManga == 0 } // Mostrar solo los no agregados
                                            }
                                            adapter.updateList(listaFiltrada, todosLosMangasAgregados)
                                            binding.txtTotalMangaTengo.text = detalles.totalMangasColeccion.toString()
                                            binding.txtTotalFaltanLeer.text = detalles.totalMangasLeer.toString()
                                            binding.bdpColeccion.progress = detalles.porcentajeMangaCompletado.toInt()
                                            binding.bdpLectura.progress = detalles.porcentajeMangaLeido.toInt()

                                            if (detalles.totalMangas == detalles.totalMangasColeccion) {
                                                // Colección completa
                                                binding.txtEstadoIncompleto.isVisible = false
                                                binding.txtNumeroEstadoIncompleto.isVisible = false
                                                binding.txtEstadoCompletado.isVisible = true
                                                binding.imgCorazon.isVisible = true
                                            } else {
                                                // Colección incompleta
                                                val mangasFaltantes = detalles.totalMangas - detalles.totalMangasColeccion
                                                binding.txtEstadoIncompleto.isVisible = true
                                                binding.txtNumeroEstadoIncompleto.isVisible = true
                                                binding.txtEstadoCompletado.isVisible = false
                                                binding.imgCorazon.isVisible = false
                                                binding.txtNumeroEstadoIncompleto.text = mangasFaltantes.toString()
                                            }
                                        }
                                    }
                                }
                            } else {
                                Log.e("DetalleSerie", "Error en la consulta: ${myResponse.code()}")
                            }
                        }
                    }
                } else {
                    // Si la respuesta no es exitosa
                    runOnUiThread {
                        Toast.makeText(this@DetalleSerieActivity, "Error al eliminar manga", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Manejo de errores de red
                runOnUiThread {
                    Toast.makeText(this@DetalleSerieActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun AgregarManga(idManga: Int, idUsuario: Int, idSerie: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = EliminarYAgregarMangaRequest(idManga, idUsuario)

            try {
                // Realiza la llamada a la API para agregar el manga
                val myResponse = retrofit.create(ApiServiceManga::class.java).agregarManga(request)

                if (myResponse.isSuccessful) {
                    // Si la respuesta es exitosa
                    val message = myResponse.body()?.message ?: "Manga agregado correctamente"

                    // Actualizamos la interfaz en el hilo principal
                    runOnUiThread {
                        Toast.makeText(this@DetalleSerieActivity, message, Toast.LENGTH_SHORT).show()
                        CoroutineScope(Dispatchers.IO).launch {
                            val myResponse = retrofit.create(ApiServiceManga::class.java).searchDataSerie(
                                idUsuario.toString(), idSerie.toString()
                            )
                            if (myResponse.isSuccessful) {
                                Log.i("DetalleSerie", "Consulta exitosa")
                                val response: DetalleSerieDataResponse? = myResponse.body()
                                if (response != null) {
                                    if (response != null) {
                                        runOnUiThread {
                                            val detalles = response.resultadoDetalles
                                            val todosLosMangasAgregados = detalles.porcentajeMangaCompletado == 100f

                                            val listaFiltrada = if (todosLosMangasAgregados) {
                                                detalles.detalleResultadoListaMnaga // Mostrar todos los mangas
                                            } else {
                                                detalles.detalleResultadoListaMnaga.filter { it.estadoManga == 0 } // Mostrar solo los no agregados
                                            }
                                            adapter.updateList(listaFiltrada, todosLosMangasAgregados)
                                            binding.txtTotalMangaTengo.text = detalles.totalMangasColeccion.toString()
                                            binding.txtTotalFaltanLeer.text = detalles.totalMangasLeer.toString()
                                            binding.bdpColeccion.progress = detalles.porcentajeMangaCompletado.toInt()
                                            binding.bdpLectura.progress = detalles.porcentajeMangaLeido.toInt()

                                            if (detalles.totalMangas == detalles.totalMangasColeccion) {
                                                // Colección completa
                                                binding.txtEstadoIncompleto.isVisible = false
                                                binding.txtNumeroEstadoIncompleto.isVisible = false
                                                binding.txtEstadoCompletado.isVisible = true
                                                binding.imgCorazon.isVisible = true
                                            } else {
                                                // Colección incompleta
                                                val mangasFaltantes = detalles.totalMangas - detalles.totalMangasColeccion
                                                binding.txtEstadoIncompleto.isVisible = true
                                                binding.txtNumeroEstadoIncompleto.isVisible = true
                                                binding.txtEstadoCompletado.isVisible = false
                                                binding.imgCorazon.isVisible = false
                                                binding.txtNumeroEstadoIncompleto.text = mangasFaltantes.toString()
                                            }
                                        }
                                    }
                                }
                            } else {
                                Log.e("DetalleSerie", "Error en la consulta: ${myResponse.code()}")
                            }
                        }
                    }
                } else {
                    // Si la respuesta no es exitosa
                    runOnUiThread {
                        Toast.makeText(this@DetalleSerieActivity, "Error al agregar manga", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Manejo de errores de red
                runOnUiThread {
                    Toast.makeText(this@DetalleSerieActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToDetail(id_manga: Int, id_usuario: Int) {
        Log.d("NavigateToDetail", "ID serie: $id_manga")
        Log.d("NavigateToDetail", "ID usuario: $id_usuario")

        val intent = Intent(this, DetalleMangaActivity::class.java)
        intent.putExtra(DetalleMangaActivity.MANGA_ID, id_manga) // Pasar el ID como un extra
        intent.putExtra(DetalleMangaActivity.USER_ID, id_usuario)
        startActivity(intent)
    }



}