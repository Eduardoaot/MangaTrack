package com.aristidevs.androidmaster.detallesserie

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.MenuActivity
import com.aristidevs.androidmaster.R

import com.aristidevs.androidmaster.databinding.ActivityDetalleSerieBinding
import com.aristidevs.androidmaster.detallesmanga.AgregarMangaRequest

import com.aristidevs.androidmaster.detallesmanga.DetalleMangaActivity
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaDataResponse
import com.aristidevs.androidmaster.detallesmanga.EliminarYAgregarMangaRequest
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.manga.DetalleSerieDataResponse

import com.aristidevs.androidmaster.network.RetrofitClient
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class DetalleSerieActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetalleSerieBinding
    private lateinit var adapter: DetalleSerieAdapter
    private lateinit var retrofit: Retrofit
    private var precio: Float? = null

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
        // Dentro de tu Adapter o Activity donde estás manejando el evento de agregar manga
        adapter = DetalleSerieAdapter(
            onItemSelected = { idManga -> navigateToDetail(idManga, idUsuario) },
            onAddOrRemove = { idManga, isAdded ->
                if (isAdded) {
                    searchManga(idManga, idUsuario)

                    // Aquí pasas los parámetros que necesita la función
                    mostrarDialogoAgregarManga(
                        context = this,
                        idManga = idManga,
                        updateButtonVisibility = { mangaId, isVisible ->
                            updateButtonVisibility(mangaId, isVisible) // Aquí llamas a la función que cambia la visibilidad
                        }
                    ) { precioIngresado ->
                        // Aquí manejas el precio ingresado
                        when (precioIngresado) {
                            -1f -> {
                                // Regalado: Llamar a la función con precio en 0
                                Toast.makeText(this, "El manga fue regalado", Toast.LENGTH_SHORT).show()
                                AgregarManga(idManga, idUsuario, idSerie, precio ?: 0f)
                            }
                            0f -> {
                                // No sé: El precio es 0
                                precio = 0f
                                Toast.makeText(this, "Precio desconocido, asignado 0", Toast.LENGTH_SHORT).show()
                                AgregarManga(idManga, idUsuario, idSerie, 0f)
                            }
                            else -> {
                                // Verificar que `precio` no sea null antes de hacer la resta
                                val diferenciaPrecio = (precio ?: 0f) - precioIngresado
                                Toast.makeText(this, "Precio ingresado: $precioIngresado\nDiferencia: $diferenciaPrecio", Toast.LENGTH_SHORT).show()
                                AgregarManga(idManga, idUsuario, idSerie, diferenciaPrecio)
                            }
                        }
                    }
                } else {
                    EliminarManga(idManga, idUsuario, idSerie)
                }
            }
        )

        binding.rvMangasListaAAgregar.setHasFixedSize(true)
        binding.rvMangasListaAAgregar.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMangasListaAAgregar.adapter = adapter

    searchSerieDetails(idUsuario, idSerie)
    }

    private fun navigateToMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()  // Finaliza la actividad actual
    }

    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
        binding.mainContent.visibility = View.GONE
        binding.errorView.visibility = View.GONE
    }

    private fun showContent() {
        binding.progressBar.visibility = View.GONE
        binding.mainContent.visibility = View.VISIBLE
        binding.errorView.visibility = View.GONE
    }

    private fun searchManga(id_manga: Int, id_usuario: Int) {
        showLoadingState()
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse = retrofit.create(ApiServiceManga::class.java)
                .searchAllDataManga(id_manga.toString(), id_usuario.toString())
            if (myResponse.isSuccessful) {
                Log.i("aristidevs", "Funciona")
                val response: DetalleMangaDataResponse? = myResponse.body()
                if (response != null) {
                    runOnUiThread {
                        val mangaDetalle = response.detallesManga.firstOrNull()
                        precio = mangaDetalle?.precio
                        println(precio)
                        showContent()
                    }
                }
            } else {
                Log.i("aristidevs", "No funciona, algo salió mal")
            }
        }
    }

    fun updateButtonVisibility(idManga: Int, isAdded: Boolean) {
        val recyclerView = findViewById<RecyclerView>(R.id.rvMangasListaAAgregar)
        val position = adapter.detalleResultadoListaMnaga.indexOfFirst { it.idManga == idManga }
        if (position != -1) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) as? DetalleSerieHolder
            viewHolder?.let {
                if (isAdded) {
                    it.binding.btnEliminarMangaDetalle.isVisible = true
                    it.binding.btnAgregarMangaDetalle.isVisible = false
                } else {
                    it.binding.btnEliminarMangaDetalle.isVisible = false
                    it.binding.btnAgregarMangaDetalle.isVisible = true
                }
            }
        }
    }

    fun mostrarDialogoAgregarManga(
        context: Context,
        idManga: Int, // Necesitamos el ID del manga para llamar a la función después
        updateButtonVisibility: (Int, Boolean) -> Unit, // Referencia a la función para actualizar la visibilidad
        onPrecioIngresado: (Float) -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Ingrese el precio por el que compró el manga:")

        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(input)

        // Variable de control para saber si el diálogo fue cerrado con una acción
        var dialogoConfirmado = false

        builder.setPositiveButton("Aceptar") { _, _ ->
            val textoIngresado = input.text.toString()
            val precioIngresado = textoIngresado.toFloatOrNull() ?: 0f
            onPrecioIngresado(precioIngresado)

            dialogoConfirmado = true
        }

        builder.setNeutralButton("No sé") { _, _ ->
            onPrecioIngresado(0f)
            dialogoConfirmado = true
        }

        builder.setNegativeButton("Regalado") { _, _ ->
            onPrecioIngresado(-1f)
            dialogoConfirmado = true
        }

        // Crear el diálogo
        val dialog = builder.create()

        // Aquí, en el listener de dismiss, es donde se llamará a updateButtonVisibility
        dialog.setOnDismissListener {
            // Si el diálogo fue confirmado (se eligió una opción)
            if (dialogoConfirmado) {
                updateButtonVisibility(idManga, true)  // Cambiar visibilidad
            } else {
                updateButtonVisibility(idManga, false)  // Cambiar visibilidad en caso de que no se haya tomado una acción
            }
        }

        dialog.show()
    }


    private fun searchSerieDetails(idUsuario: Int, idSerie: Int) {
        showLoadingState()
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
                            binding.txtTituloSerie.isSelected = true
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
                            showContent()
                        }
                    }

                }
            } else {
                Log.e("DetalleSerie", "Error en la consulta: ${myResponse.code()}")
            }
        }
    }

    private fun EliminarManga(idManga: Int, idUsuario: Int, idSerie: Int) {
        showLoadingState()
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
                                            showContent()
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

    private fun AgregarManga(idManga: Int, idUsuario: Int, idSerie: Int, precio: Float) {
        showLoadingState()
        CoroutineScope(Dispatchers.IO).launch {
            val request = AgregarMangaRequest(idManga, idUsuario, precio)

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
                                            showContent()
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
        Log.d("NavigateToDetail", "ID manga: $id_manga")
        Log.d("NavigateToDetail", "ID usuario: $id_usuario")

        val intent = Intent(this, DetalleMangaActivity::class.java)
        intent.putExtra(DetalleMangaActivity.MANGA_ID, id_manga) // Pasar el ID como un extra
        intent.putExtra(DetalleMangaActivity.USER_ID, id_usuario)
        startActivity(intent)
    }



}