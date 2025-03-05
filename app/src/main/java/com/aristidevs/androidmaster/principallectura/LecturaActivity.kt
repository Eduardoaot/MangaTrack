package com.aristidevs.androidmaster.principallectura

import CustomMarkerView
import MangasPendientesAdapter
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ActivityLecturaBinding
import com.aristidevs.androidmaster.databinding.DialogAgregarLecturaBinding
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaActivity
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaActivity.Companion.USER_ID
import com.aristidevs.androidmaster.detallesmanga.MarcarLeidoRequest
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.manga.LecturaDataResponse
import com.aristidevs.androidmaster.network.RetrofitClient
import com.aristidevs.androidmaster.principallectura.agregarpendientes.AgregarPendientesActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit


class LecturaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLecturaBinding // Cambia al binding correcto
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: LecturaAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLecturaBinding.inflate(layoutInflater) // Infla el binding
        setContentView(binding.root) // Usa la vista raíz del binding
        retrofit = RetrofitClient.getRetrofit()
        val id_usuario: Int = intent.getIntExtra(DetalleMangaActivity.USER_ID, -1)


        initUI(id_usuario)
        // Configura la gráfica

        binding.btnflecha.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        val userId = intent.getIntExtra(USER_ID, -1)
        initUI(userId)
    }

    private fun initUI(idUsuario: Int) {
        adapter = LecturaAdapter(
            onItemSelected = { idManga -> navigateToDetail(idManga, idUsuario) },
            onSpecificButtonClick = { idManga ->
                mostrarDialogoConfirmacion(idManga, idUsuario)
            },
            seleccionarParaLeer = { idManga ->
                leerManga(idManga, idUsuario)
            }
        )
        binding.rvMangasListaAAgregar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvMangasListaAAgregar.setHasFixedSize(true) // Si las vistas no cambian de tamaño, esto mejora rendimiento
        binding.rvMangasListaAAgregar.adapter = adapter
        searchDataLectura(idUsuario)

        binding.btnAgregarLectura.setOnClickListener {
            AgregarLectura(idUsuario)
        }

        binding.btnAgregarMeta.setOnClickListener {
            AgregarMeta(idUsuario)
        }
    }

    private fun leerManga(idManga: Int, idUsuario: Int) {
        // Crear el AlertDialog
        val alertDialog = AlertDialog.Builder(this) // "this" es el contexto de la actividad o fragmento
            .setTitle("Confirmar") // Título del diálogo
            .setMessage("¿Ya leyó este manga?") // Mensaje del diálogo
            .setPositiveButton("Aceptar") { dialog, which ->
                // Si el usuario presiona "Aceptar", llamamos a la función cambiarAEstadoPendiente
                cambiarAEstadoLeido(idManga, idUsuario)
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                // Si el usuario presiona "Cancelar", simplemente cerramos el diálogo
                dialog.dismiss()
            }
            .create()

        // Mostrar el diálogo
        alertDialog.show()
    }

    private fun cambiarAEstadoLeido(idManga: Int, idUsuario: Int) {
        val marcarLeidoRequest = MarcarLeidoRequest(idManga, idUsuario, 3)
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
                        runOnUiThread {
                            initUI(idUsuario)
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

    private fun mostrarDialogoConfirmacion(idManga: Int, idUsuario: Int) {
        // Crear el AlertDialog
        val alertDialog = AlertDialog.Builder(this) // "this" es el contexto de la actividad o fragmento
            .setTitle("Confirmar") // Título del diálogo
            .setMessage("¿Deseas poner este manga en pendiente?") // Mensaje del diálogo
            .setPositiveButton("Aceptar") { dialog, which ->
                // Si el usuario presiona "Aceptar", llamamos a la función cambiarAEstadoPendiente
                cambiarAEstadoPendiente(idManga, idUsuario)
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                // Si el usuario presiona "Cancelar", simplemente cerramos el diálogo
                dialog.dismiss()
            }
            .create()

        // Mostrar el diálogo
        alertDialog.show()
    }

    private fun cambiarAEstadoPendiente(idManga: Int, idUsuario: Int) {
        val marcarLeidoRequest = MarcarLeidoRequest(idManga, idUsuario, 1)
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
                        runOnUiThread {
                            actualizarListaGeneral(idUsuario)
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

    private fun navigateToDetail(id_manga: Int, id_usuario: Int) {
        Log.d("NavigateToDetail", "ID serie: $id_manga")
        Log.d("NavigateToDetail", "ID usuario: $id_usuario")

        val intent = Intent(this, DetalleMangaActivity::class.java)
        intent.putExtra(DetalleMangaActivity.MANGA_ID, id_manga) // Pasar el ID como un extra
        intent.putExtra(DetalleMangaActivity.USER_ID, id_usuario)
        startActivity(intent)
    }

    private fun actualizarListaGeneral(idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            // Llamamos al servicio de la API para obtener los datos
            val myResponse = retrofit.create(ApiServiceManga::class.java).searchDataLectura(idUsuario.toString())
            if (myResponse.isSuccessful) {
                Log.i("aristidevs", "Petición exitosa")
                val response: LecturaDataResponse? = myResponse.body()
                if (response != null) {
                    // Si la respuesta no es nula, logueamos los datos recibidos
                    Log.i("aristidevs", "Response data: $response")  // Log de toda la respuesta
                    val lecturaData = response.lecturaDataTotal
                    // Log para ver los detalles específicos de la lecturaData
                    Log.i("aristidevs", "Lectura Data: Total leídos: ${lecturaData.mangasLeidosTot}, Total leídos este año: ${lecturaData.mangasLeidosAnio}, Total leídos este mes: ${lecturaData.mangasLeidosMes}")

                    // Actualiza los valores de los TextViews con la data recibida
                    runOnUiThread {
                        adapter.updateList(lecturaData.listaMangasSinLeer)
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

    private fun searchDataLectura(idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            // Llamamos al servicio de la API para obtener los datos
            val myResponse = retrofit.create(ApiServiceManga::class.java).searchDataLectura(idUsuario.toString())

            if (myResponse.isSuccessful) {
                Log.i("aristidevs", "Petición exitosa")
                val response: LecturaDataResponse? = myResponse.body()
                if (response != null) {
                    // Si la respuesta no es nula, logueamos los datos recibidos
                    Log.i("aristidevs", "Response data: $response")  // Log de toda la respuesta
                    val lecturaData = response.lecturaDataTotal
                    // Log para ver los detalles específicos de la lecturaData
                    Log.i("aristidevs", "Lectura Data: Total leídos: ${lecturaData.mangasLeidosTot}, Total leídos este año: ${lecturaData.mangasLeidosAnio}, Total leídos este mes: ${lecturaData.mangasLeidosMes}")

                    // Actualiza los valores de los TextViews con la data recibida
                    runOnUiThread {
                        binding.NumeroTotalLeidos.text = lecturaData.mangasLeidosTot.toString()
                        binding.NumeroTotalLeidosAnio.text = lecturaData.mangasLeidosAnio.toString()
                        binding.NumeroTotalLeidosMes.text = lecturaData.mangasLeidosMes.toString()


                        setupBarChart(lecturaData.months, lecturaData.mangasAniadidos, lecturaData.mangasLeidos)
                        // Actualiza el RecyclerView con la lista de mangas sin leer
                        adapter.updateList(lecturaData.listaMangasSinLeer)
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

    private fun AgregarLectura(idUsuario: Int){
        val intent = Intent(this, AgregarPendientesActivity::class.java)
        intent.putExtra(DetalleMangaActivity.USER_ID, idUsuario)
        startActivity(intent)
    }


    private fun obtenerLecturaYMeta(idUsuario: Int, dialogView: View) {
        CoroutineScope(Dispatchers.IO).launch {
            // Llamamos a la API para obtener los datos de mangas leídos y meta
            val myResponse = retrofit.create(ApiServiceManga::class.java)
                .obtenerMeta(idUsuario.toString())  // Llamamos a la API con el idUsuario
            if (myResponse.isSuccessful) {
                Log.i("aristidevs", "Petición exitosa")
                val response: ObtenerMetaDataResponse? = myResponse.body()
                if (response != null) {
                    runOnUiThread {
                        // Actualizamos los TextViews con los datos de la respuesta
                        dialogView.findViewById<TextView>(R.id.txtMeta).text = "Tu meta es de: ${response.mangasLeidosMes}"
                        dialogView.findViewById<TextView>(R.id.txtLeidos).text = "Llevas leídos: ${response.mangasLeidosTot}"
                    }
                }
            } else {
                Log.i("aristidevs", "Error en la petición: ${myResponse.code()} - ${myResponse.message()}")
                runOnUiThread {
                }
            }
        }
    }

    private fun AgregarMeta(idUsuario: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_meta, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val metaEditText: EditText = dialogView.findViewById(R.id.editMeta)
        val btnAplicar: Button = dialogView.findViewById(R.id.btnAplicar)
        val btnCerrar: Button = dialogView.findViewById(R.id.btnCerrar)

        obtenerLecturaYMeta(idUsuario, dialogView)

        btnAplicar.setOnClickListener {
            val meta = metaEditText.text.toString()
            if (meta.isNotEmpty()) {
                Toast.makeText(applicationContext, "Meta actualizada exitosamente", Toast.LENGTH_SHORT).show()
                actualizarMeta(idUsuario, meta)
            } else {
                showToast("Por favor, ingresa una meta válida.")
            }
        }

        btnCerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }




    // Método para mostrar un Toast
    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }


    private fun actualizarMeta(idUsuario: Int, meta: String) {
        // Creamos la solicitud de actualización de meta
        val actualizarMetaRequest = AgregarMetaRequest(idUsuario, meta)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Hacemos la solicitud POST
                val response = retrofit.create(ApiServiceManga::class.java)
                    .actualizarMeta(actualizarMetaRequest)
                if (response.isSuccessful) {
                    // Si la respuesta es exitosa, mostramos un mensaje
                    val responseBody = response.body()
                    if (responseBody != null) {
                        runOnUiThread {
                            Toast.makeText(applicationContext, "Meta actualizada exitosamente", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("aristidevs", "Error al actualizar la meta: ${response.code()} - ${response.message()}")
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Error al actualizar la meta", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("aristidevs", "Error al hacer la solicitud: ${e.message}")

            }
        }
    }



    private fun setupBarChart(meses: List<String>, listamangas: List<Int>, listaleidos: List<Int>) {
        // Datos de ejemplo: meses, mangas añadidos y mangas leídos
        val months = meses
        val mangasAnadidos = listamangas
        val mangasLeidos = listaleidos

        // Crear las entradas para la gráfica
        val entriesAnadidos = mutableListOf<BarEntry>()
        val entriesLeidos = mutableListOf<BarEntry>()

        for (i in months.indices) {
            entriesAnadidos.add(BarEntry(i.toFloat() - 0.2f, mangasAnadidos[i].toFloat())) // Mangas añadidos (desplazados a la izquierda)
            entriesLeidos.add(BarEntry(i.toFloat() + 0.2f, mangasLeidos[i].toFloat()))     // Mangas leídos (desplazados a la derecha)
        }

        // Crear los conjuntos de datos
        val dataSetAnadidos = BarDataSet(entriesAnadidos, "").apply {
            color = Color.parseColor("#5BA4F5") // Color azul para mangas añadidos
            setDrawValues(false) // Ocultar valores encima de las barras
        }

        val dataSetLeidos = BarDataSet(entriesLeidos, "").apply {
            color = Color.parseColor("#FFBB86FC") // Color morado para mangas leídos
            setDrawValues(false) // Ocultar valores encima de las barras
        }

        // Configurar el ancho de las barras
        val barData = BarData(dataSetAnadidos, dataSetLeidos).apply {
            barWidth = 0.4f // Ancho de las barras
        }

        // Asignar los datos a la gráfica
        binding.barChart.apply {
            data = barData

            // Configurar el eje X
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false) // Ocultar líneas de la cuadrícula
                setDrawAxisLine(true)   // Mostrar la línea del eje X
                setDrawLabels(true)     // Mostrar etiquetas del eje X (meses)
                valueFormatter = IndexAxisValueFormatter(months) // Asignar los meses como etiquetas
                granularity = 1f       // Evitar duplicación de etiquetas

                // Aumentar el tamaño de las letras de los meses
                textSize = 12f // Ajusta este valor al tamaño que desees
            }

            // Configurar el eje Y izquierdo
            axisLeft.apply {
                axisMinimum = 0f // Valor mínimo del eje Y
                setDrawGridLines(false) // Ocultar líneas de la cuadrícula
                setDrawAxisLine(false)  // Ocultar la línea del eje Y
                setDrawLabels(false)    // Ocultar etiquetas del eje Y
            }

            // Deshabilitar el eje Y derecho
            axisRight.isEnabled = false

            // Deshabilitar la leyenda
            legend.isEnabled = false

            // Deshabilitar la descripción
            description.isEnabled = false

            // Desactivar el zoom
            setPinchZoom(false)
            isScaleXEnabled = false
            isScaleYEnabled = false

            // Configurar el MarkerView
            val markerView = CustomMarkerView(this@LecturaActivity, R.layout.custom_marker_view, months)
            markerView.chartView = this
            marker = markerView

            // Animación
            animateY(500) // Animación de 1 segundo


            // Refrescar la gráfica
            invalidate()
        }
    }


}