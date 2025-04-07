package com.aristidevs.androidmaster.detallesmanga

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
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
            // Acción para regresar a la actividad anterior utilizando onBackPressedDispatcher
            onBackPressedDispatcher.onBackPressed()
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
                        // Regalado: Llamar a la función con precio en 0
                        Toast.makeText(this, "El manga fue regalado", Toast.LENGTH_SHORT).show()
                        AgregarManga(id_manga, id_usuario, precio ?: 0f)
                    }
                    0f -> {
                        // No sé: El precio es 0
                        precio = 0f
                        Toast.makeText(this, "Precio desconocido, asignado 0", Toast.LENGTH_SHORT).show()
                        AgregarManga(id_manga, id_usuario, 0f)
                    }
                    else -> {
                        // Verificar que `precio` no sea null antes de hacer la resta
                        val diferenciaPrecio = (precio ?: 0f) - precioIngresado
                        Toast.makeText(this, "Precio ingresado: $precioIngresado\nDiferencia: $diferenciaPrecio", Toast.LENGTH_SHORT).show()

                        // Llamamos a AgregarManga con la diferencia
                        AgregarManga(id_manga, id_usuario, diferenciaPrecio)
                    }
                }
            }
        }



        binding.botonEliminarManga.setOnClickListener {
            EliminarManga(id_manga, id_usuario)
        }
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
                        binding.imagenLectura.isVisible = false  // Hacer invisible la imagen
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
                        // Aquí puedes actualizar la UI si el manga fue agregado
                        binding.imagenLectura.isVisible = true  // Hacer visible la imagen (ajustar según el caso)
                        binding.btnMarcarLeido.isVisible = true  // Hacer visible el botón "Marcar leído"
                        binding.botonEliminarManga.isVisible = true  // Hacer visible el botón de eliminar
                        binding.btnAgregarManga.isVisible = false  // Hacer invisible el botón de agregar
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
                        val estadoLectura = mangaDetalle?.estadoLectura
                        val imagenLectura = findViewById<ImageView>(R.id.imagenLectura)
                        if (estadoLectura == "Pendiente" || estadoLectura == "Sin Leer") {
                            imagenLectura.setImageResource(R.mipmap.pom)
                        } else if (estadoLectura == "Leido") {
                            imagenLectura.setImageResource(R.mipmap.pomcheack)
                        }
                        val mangaNum = mangaDetalle?.mangaNum ?: 0f  // Si es null, asignamos un valor predeterminado (0f)
                        val mangaNumText = if (mangaNum == mangaNum.toInt().toFloat()) {
                            mangaNum.toInt().toString() // Si es entero, lo mostramos sin decimales
                        } else {
                            mangaNum.toString() // Si tiene decimales, lo mostramos completo
                        }
                        val estadoAgregado = mangaDetalle?.estadoAgregado
                        Log.i("aristidevs", "Valor de estadoAgregado: $estadoAgregado")
                        if (estadoAgregado?.toInt() == 0) {
                            binding.imagenLectura.isVisible = false  // Hacer invisible la imagen
                            binding.btnMarcarLeido.isVisible = false  // Hacer invisible el botón "Marcar Leído"
                            binding.btnAgregarManga.isVisible = true
                            binding.botonEliminarManga.isVisible = false
                            Log.i("aristidevs", "Paso por valor a 0")
                        } else{
                            binding.imagenLectura.isVisible = true  // Hacer visible la imagen
                            binding.btnMarcarLeido.isVisible = true  // Hacer visible el botón "Marcar Leído"5BA4F5
                            binding.btnAgregarManga.isVisible = false
                            binding.botonEliminarManga.isVisible = true
                            // Cambiar el estilo del botón "Agregar Manga"
                            Log.i("aristidevs", "Paso por valor a 1")
                        }
                        Log.i("aristidevs", "Valor de despues del if: $estadoAgregado")
                        Log.i("aristidevs", "Ya mamo")
                        if (mangaDetalle != null) {
                            // Cargar la imagen con Picasso
                            Picasso.get()
                                .load(mangaDetalle.mangaImg)  // URL de la imagen
                                .into(binding.imgManga)  // ImageView donde se cargará la imagen
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
                            val nuevoEstadoLectura = when (estadoLectura) {
                                "Pendiente", "Sin Leer" -> 3  // Si es Pendiente o Sin Leer, el estado es 2
                                "Leido" -> 2  // Si es Leido, el estado es 1
                                else -> return@runOnUiThread  // Si no es ninguno de estos, no hacemos nada
                            }
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