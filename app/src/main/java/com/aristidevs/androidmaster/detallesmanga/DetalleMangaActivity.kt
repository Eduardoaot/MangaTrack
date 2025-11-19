package com.aristidevs.androidmaster.detallesmanga

import android.content.Context
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
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ActivityDetalleMangaBinding
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.network.RetrofitClient
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class DetalleMangaActivity : AppCompatActivity() {
    companion object{
        const val SERIE_ID = "serie_id"
        const val USER_ID = "user_id"
        const val MANGA_ID = "manga_id"
        const val PLAN_ID = "plan_id"
    }
    private lateinit var binding: ActivityDetalleMangaBinding
    private lateinit var retrofit: Retrofit
    private var precio: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleMangaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = RetrofitClient.getRetrofit()

        val id_manga: Int = intent.getIntExtra(MANGA_ID, -1)
        val id_usuario: Int = intent.getIntExtra(USER_ID, -1)

        initUI(id_manga, id_usuario)
    }

    private fun initUI(id_manga: Int, id_usuario: Int){
        binding.btnflecha.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Configurar el botón de reintentar
        binding.retryButton.setOnClickListener {
            searchManga(id_manga, id_usuario)
        }

        searchManga(id_manga, id_usuario)
        val imagenLectura = findViewById<ImageView>(R.id.imagenLectura)

        binding.btnMarcarLeido.setOnClickListener {
            MarcarLeido(id_manga, id_usuario, imagenLectura)
        }
        binding.btnAgregarManga.setOnClickListener {
            mostrarDialogoAgregarManga(this) { precioIngresado ->
                when (precioIngresado) {
                    -1f -> {
                        Toast.makeText(this, "El manga fue regalado", Toast.LENGTH_SHORT).show()
                        AgregarManga(id_manga, id_usuario, precio ?: 0f)
                    }
                    0f -> {
                        precio = 0f
                        Toast.makeText(this, "Precio desconocido, asignado 0", Toast.LENGTH_SHORT).show()
                        AgregarManga(id_manga, id_usuario, 0f)
                    }
                    else -> {
                        val diferenciaPrecio = (precio ?: 0f) - precioIngresado
                        Toast.makeText(this, "Precio ingresado: $precioIngresado\nDiferencia: $diferenciaPrecio", Toast.LENGTH_SHORT).show()
                        AgregarManga(id_manga, id_usuario, diferenciaPrecio)
                    }
                }
            }
        }

        binding.botonEliminarManga.setOnClickListener {
            EliminarManga(id_manga, id_usuario)
        }
    }

    // Función para mostrar el loading
    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.mainContent.visibility = View.GONE
        binding.errorView.visibility = View.GONE
    }

    // Función para mostrar el contenido
    private fun showContent() {
        binding.progressBar.visibility = View.GONE
        binding.mainContent.visibility = View.VISIBLE
        binding.errorView.visibility = View.GONE
    }

    // Función para mostrar error
    private fun showError(message: String = "Error al cargar datos") {
        binding.progressBar.visibility = View.GONE
        binding.mainContent.visibility = View.GONE
        binding.errorView.visibility = View.VISIBLE
        binding.errorText.text = message
    }

    private fun EliminarManga(idManga: Int, idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = EliminarYAgregarMangaRequest(idManga, idUsuario)
            try {
                val myResponse = retrofit.create(ApiServiceManga::class.java).eliminarManga(request)
                if (myResponse.isSuccessful) {
                    val message = myResponse.body()?.message ?: "Manga eliminado correctamente"
                    runOnUiThread {
                        Toast.makeText(this@DetalleMangaActivity, message, Toast.LENGTH_SHORT).show()
                        binding.imagenLectura.isVisible = false
                        binding.btnMarcarLeido.isVisible = false
                        binding.botonEliminarManga.isVisible = false
                        binding.btnAgregarManga.isVisible = true
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@DetalleMangaActivity, "Error al eliminar manga", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@DetalleMangaActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun mostrarDialogoAgregarManga(context: Context, onPrecioIngresado: (Float) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Ingrese el precio por el que compró el manga:")

        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(input)

        builder.setPositiveButton("Aceptar") { _, _ ->
            val textoIngresado = input.text.toString()
            val precioIngresado = textoIngresado.toFloatOrNull() ?: 0f
            onPrecioIngresado(precioIngresado)
        }

        builder.setNeutralButton("No sé") { _, _ ->
            onPrecioIngresado(0f)
        }

        builder.setNegativeButton("Regalado") { _, _ ->
            onPrecioIngresado(-1f)
        }

        builder.show()
    }

    private fun AgregarManga(idManga: Int, idUsuario: Int, precioFinal: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = AgregarMangaRequest(idManga, idUsuario, precioFinal)
            try {
                val myResponse = retrofit.create(ApiServiceManga::class.java).agregarManga(request)
                if (myResponse.isSuccessful) {
                    val message = myResponse.body()?.message ?: "Manga agregado correctamente"
                    val idMangaAgregado = myResponse.body()?.idMangaAgregado
                    runOnUiThread {
                        Toast.makeText(this@DetalleMangaActivity, message, Toast.LENGTH_SHORT).show()
                        binding.imagenLectura.isVisible = true
                        binding.btnMarcarLeido.isVisible = true
                        binding.botonEliminarManga.isVisible = true
                        binding.btnAgregarManga.isVisible = false
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@DetalleMangaActivity, "Error al agregar manga", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@DetalleMangaActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun searchManga(id_manga: Int, id_usuario: Int) {
        // MOSTRAR LOADING
        runOnUiThread {
            showLoading()
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val myResponse = retrofit.create(ApiServiceManga::class.java)
                    .searchAllDataManga(id_manga.toString(), id_usuario.toString())

                if (myResponse.isSuccessful) {
                    Log.i("aristidevs", "Funciona")
                    val response: DetalleMangaDataResponse? = myResponse.body()

                    if (response != null) {
                        runOnUiThread {
                            // MOSTRAR CONTENIDO
                            showContent()

                            val mangaDetalle = response.detallesManga.firstOrNull()
                            precio = mangaDetalle?.precio
                            println(precio)
                            val estadoLectura = mangaDetalle?.estadoLectura
                            val imagenLectura = findViewById<ImageView>(R.id.imagenLectura)

                            if (estadoLectura == "Pendiente" || estadoLectura == "Sin Leer") {
                                imagenLectura.setImageResource(R.mipmap.pom)
                            } else if (estadoLectura == "Leido") {
                                imagenLectura.setImageResource(R.mipmap.pomcheack)
                            }

                            val mangaNum = mangaDetalle?.mangaNum ?: 0f
                            val mangaNumText = if (mangaNum == mangaNum.toInt().toFloat()) {
                                mangaNum.toInt().toString()
                            } else {
                                mangaNum.toString()
                            }

                            val estadoAgregado = mangaDetalle?.estadoAgregado
                            Log.i("aristidevs", "Valor de estadoAgregado: $estadoAgregado")

                            if (estadoAgregado?.toInt() == 0) {
                                binding.imagenLectura.isVisible = false
                                binding.btnMarcarLeido.isVisible = false
                                binding.btnAgregarManga.isVisible = true
                                binding.botonEliminarManga.isVisible = false
                                Log.i("aristidevs", "Paso por valor a 0")
                            } else {
                                binding.imagenLectura.isVisible = true
                                binding.btnMarcarLeido.isVisible = true
                                binding.btnAgregarManga.isVisible = false
                                binding.botonEliminarManga.isVisible = true
                                Log.i("aristidevs", "Paso por valor a 1")
                            }

                            Log.i("aristidevs", "Valor de despues del if: $estadoAgregado")
                            Log.i("aristidevs", "Ya mamo")

                            if (mangaDetalle != null) {
                                Picasso.get()
                                    .load(mangaDetalle.mangaImg)
                                    .into(binding.imgManga)
                                binding.txtTituloMangaYnum.text = "${mangaDetalle.serieNom} - ${mangaNumText}"
                                binding.txtDescripcion.text = mangaDetalle.mangaDesc
                                binding.txtAutor.text = mangaDetalle.serieAut
                            }
                        }
                    } else {
                        runOnUiThread {
                            showError("No se encontraron datos del manga")
                        }
                    }
                } else {
                    Log.i("aristidevs", "No funciona, algo salió mal")
                    runOnUiThread {
                        showError("Error al cargar el manga")
                    }
                }
            } catch (e: Exception) {
                Log.e("aristidevs", "Error: ${e.message}")
                runOnUiThread {
                    showError("Error de conexión: ${e.message}")
                }
            }
        }
    }

    private fun MarcarLeido(id_manga: Int, id_usuario: Int, imagenLectura: ImageView) {
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse = retrofit.create(ApiServiceManga::class.java)
                .searchAllDataManga(id_manga.toString(), id_usuario.toString())

            if (myResponse.isSuccessful) {
                Log.i("aristidevs", "Funciona")
                val response: DetalleMangaDataResponse? = myResponse.body()

                if (response != null) {
                    runOnUiThread {
                        val mangaDetalle = response.detallesManga.firstOrNull()
                        val estadoLectura = mangaDetalle?.estadoLectura
                        if (estadoLectura != null) {
                            val nuevoEstadoLectura = when (estadoLectura) {
                                "Pendiente", "Sin Leer" -> 3
                                "Leido" -> 2
                                else -> return@runOnUiThread
                            }
                            val marcarLeidoRequest = MarcarLeidoRequest(id_manga, id_usuario, nuevoEstadoLectura)

                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val responsePost = retrofit.create(ApiServiceManga::class.java)
                                        .modificarLectura(marcarLeidoRequest)

                                    if (responsePost.isSuccessful) {
                                        val responseBody = responsePost.body()
                                        if (responseBody != null) {
                                            Log.i("MarcarLeido", "Estado de lectura actualizado a: ${responseBody.estadoLectura}")

                                            runOnUiThread {
                                                if (responseBody.estadoLectura == 2 || responseBody.estadoLectura == 1) {
                                                    imagenLectura.setImageResource(R.mipmap.pom)
                                                } else if (responseBody.estadoLectura == 3) {
                                                    imagenLectura.setImageResource(R.mipmap.pomcheack)
                                                }
                                            }
                                        }
                                    } else {
                                        Log.e("MarcarLeido", "Error en la actualización del estado de lectura.")
                                    }
                                } catch (e: Exception) {
                                    Log.e("MarcarLeido", "Error al hacer la solicitud POST: ${e.message}")
                                }
                            }
                        }
                    }
                }
            } else {
                Log.i("aristidevs", "No funciona, algo salió mal")
            }
        }
    }
}