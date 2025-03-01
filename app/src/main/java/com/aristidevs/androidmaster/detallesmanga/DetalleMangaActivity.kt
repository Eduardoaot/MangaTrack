package com.aristidevs.androidmaster.detallesmanga

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ActivityBuscadorDetallesBinding
import com.aristidevs.androidmaster.databinding.ActivityDetalleMangaBinding
import com.aristidevs.androidmaster.databinding.ActivityInicioSesionBinding
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.network.RetrofitClient
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

class DetalleMangaActivity : AppCompatActivity() {

    companion object{
        const val SERIE_ID = "serie_id"
        const val USER_ID = "user_id"
        const val MANGA_ID = "manga_id"
    }

    private lateinit var binding: ActivityDetalleMangaBinding
    private lateinit var retrofit: Retrofit
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
            // Acción para regresar a la actividad anterior utilizando onBackPressedDispatcher
            onBackPressedDispatcher.onBackPressed()
        }
        searchManga(id_manga, id_usuario)
        val imagenLectura = findViewById<ImageView>(R.id.imagenLectura)

        binding.btnMarcarLeido.setOnClickListener {
            MarcarLeido(id_manga, id_usuario, imagenLectura)
        }

        binding.btnAgregarManga.setOnClickListener {
            AgregarManga(id_manga,id_usuario)
        }

        binding.botonEliminarManga.setOnClickListener {
            EliminarManga(id_manga, id_usuario)
        }
    }

    private fun EliminarManga(idManga: Int, idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = EliminarYAgregarMangaRequest(idManga, idUsuario)

            try {
                // Realiza la llamada a la API para eliminar el manga
                val myResponse = retrofit.create(ApiServiceManga::class.java).eliminarManga(request)

                if (myResponse.isSuccessful) {
                    // Si la respuesta es exitosa
                    val message = myResponse.body()?.message ?: "Manga eliminado correctamente"

                    // Actualizamos la interfaz en el hilo principal usando runOnUiThread
                    runOnUiThread {
                        Toast.makeText(this@DetalleMangaActivity, message, Toast.LENGTH_SHORT).show()
                        binding.imagenLectura.isVisible = false  // Hacer invisible la imagen
                        binding.btnMarcarLeido.isVisible = false

                        // Cambiar la visibilidad de los botones
                        binding.botonEliminarManga.isVisible = false
                        binding.btnAgregarManga.isVisible = true
                    }
                } else {
                    // Si la respuesta no es exitosa
                    runOnUiThread {
                        Toast.makeText(this@DetalleMangaActivity, "Error al eliminar manga", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Manejo de errores de red
                runOnUiThread {
                    Toast.makeText(this@DetalleMangaActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun AgregarManga(idManga: Int, idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = EliminarYAgregarMangaRequest(idManga, idUsuario)

            try {
                // Realiza la llamada a la API para agregar el manga
                val myResponse = retrofit.create(ApiServiceManga::class.java).agregarManga(request)

                if (myResponse.isSuccessful) {
                    // Si la respuesta es exitosa
                    val message = myResponse.body()?.message ?: "Manga agregado correctamente"
                    val idMangaAgregado = myResponse.body()?.idMangaAgregado

                    // Actualizamos la interfaz en el hilo principal usando runOnUiThread
                    runOnUiThread {
                        Toast.makeText(this@DetalleMangaActivity, message, Toast.LENGTH_SHORT).show()
                        // Aquí puedes actualizar la UI si el manga fue agregado
                        binding.imagenLectura.isVisible = true  // Hacer visible la imagen (ajustar según el caso)
                        binding.btnMarcarLeido.isVisible = true  // Hacer visible el botón "Marcar leído"

                        // Cambiar la visibilidad de los botones
                        binding.botonEliminarManga.isVisible = true  // Hacer visible el botón de eliminar
                        binding.btnAgregarManga.isVisible = false  // Hacer invisible el botón de agregar
                    }
                } else {
                    // Si la respuesta no es exitosa
                    runOnUiThread {
                        Toast.makeText(this@DetalleMangaActivity, "Error al agregar manga", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Manejo de errores de red
                runOnUiThread {
                    Toast.makeText(this@DetalleMangaActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun searchManga(id_manga: Int, id_usuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse = retrofit.create(ApiServiceManga::class.java)
                .searchAllDataManga(id_manga.toString(), id_usuario.toString())

            if (myResponse.isSuccessful) {
                Log.i("aristidevs", "Funciona")
                val response: DetalleMangaDataResponse? = myResponse.body()

                if (response != null) {
                    runOnUiThread {
                        val mangaDetalle = response.detallesManga.firstOrNull()

                        // Verificar si mangaDetalle no es null antes de acceder a estadoLectura
                        val estadoLectura = mangaDetalle?.estadoLectura

                        // Si estadoLectura no es nulo, asignamos la imagen correspondiente
                        val imagenLectura = findViewById<ImageView>(R.id.imagenLectura)

                        if (estadoLectura == "Pendiente" || estadoLectura == "Sin Leer") {
                            // Si el estado es Pendiente o Sin Leer, asignar la imagen "pom"
                            imagenLectura.setImageResource(R.mipmap.pom)
                        } else if (estadoLectura == "Leido") {
                            // Si el estado es Leido, asignar la imagen "pomcheack"
                            imagenLectura.setImageResource(R.mipmap.pomcheack)
                        }

                        // Procesar el número del manga
                        val mangaNum = mangaDetalle?.mangaNum ?: 0f  // Si es null, asignamos un valor predeterminado (0f)

                        val mangaNumText = if (mangaNum == mangaNum.toInt().toFloat()) {
                            mangaNum.toInt().toString() // Si es entero, lo mostramos sin decimales
                        } else {
                            mangaNum.toString() // Si tiene decimales, lo mostramos completo
                        }

                        val estadoAgregado = mangaDetalle?.estadoAgregado
                        Log.i("aristidevs", "Valor de estadoAgregado: $estadoAgregado")


                        if (estadoAgregado?.toInt() == 0) {
                            // Si estadoAgregado es 0:
                            binding.imagenLectura.isVisible = false  // Hacer invisible la imagen
                            binding.btnMarcarLeido.isVisible = false  // Hacer invisible el botón "Marcar Leído"

                            // Cambiar el estilo del botón "Agregar Manga"
                            binding.btnAgregarManga.isVisible = true
                            binding.botonEliminarManga.isVisible = false
                            Log.i("aristidevs", "Paso por valor a 0")
                        } else{
                            // Si estadoAgregado es 1:
                            binding.imagenLectura.isVisible = true  // Hacer visible la imagen
                            binding.btnMarcarLeido.isVisible = true  // Hacer visible el botón "Marcar Leído"5BA4F5

                            binding.btnAgregarManga.isVisible = false
                            binding.botonEliminarManga.isVisible = true
                            // Cambiar el estilo del botón "Agregar Manga"
                            Log.i("aristidevs", "Paso por valor a 1")
                        }
                        Log.i("aristidevs", "Valor de despues del if: $estadoAgregado")
                        Log.i("aristidevs", "Ya mamo")

                        // Si existe el detalle del manga
                        if (mangaDetalle != null) {
                            // Cargar la imagen con Picasso
                            Picasso.get()
                                .load(mangaDetalle.mangaImg)  // URL de la imagen
                                .into(binding.imgManga)  // ImageView donde se cargará la imagen
                            // Asignar el resto de los valores a los TextView
                            binding.txtTituloMangaYnum.text = "${mangaDetalle.serieNom} - ${mangaNumText}"
                            binding.txtDescripcion.text = mangaDetalle.mangaDesc
                            binding.txtAutor.text = mangaDetalle.serieAut
                        }
                    }
                }
            } else {
                Log.i("aristidevs", "No funciona, algo salió mal")
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
                            // Determinamos el nuevo estado de lectura
                            val nuevoEstadoLectura = when (estadoLectura) {
                                "Pendiente", "Sin Leer" -> 3  // Si es Pendiente o Sin Leer, el estado es 2
                                "Leido" -> 2  // Si es Leido, el estado es 1
                                else -> return@runOnUiThread  // Si no es ninguno de estos, no hacemos nada
                            }
                            // Creamos el objeto para el cuerpo de la solicitud POST
                            val marcarLeidoRequest = MarcarLeidoRequest(id_manga, id_usuario, nuevoEstadoLectura)
                            // Realizamos el POST con Retrofit
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val responsePost = retrofit.create(ApiServiceManga::class.java)
                                        .modificarLectura(marcarLeidoRequest)

                                    if (responsePost.isSuccessful) {
                                        val responseBody = responsePost.body()
                                        if (responseBody != null) {
                                            // Si la respuesta es exitosa, actualizamos la imagen
                                            Log.i("MarcarLeido", "Estado de lectura actualizado a: ${responseBody.estadoLectura}")

                                            // Actualizamos la imagen en función del nuevo estado de lectura
                                            if (responseBody.estadoLectura == 2 || responseBody.estadoLectura == 1) {
                                                imagenLectura.setImageResource(R.mipmap.pom)  // Imagen para "Pendiente" o "Sin Leer"
                                            } else if (responseBody.estadoLectura == 3) {
                                                imagenLectura.setImageResource(R.mipmap.pomcheack)  // Imagen para "Leído"
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