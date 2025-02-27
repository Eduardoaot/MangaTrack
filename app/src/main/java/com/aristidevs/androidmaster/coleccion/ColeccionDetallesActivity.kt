package com.aristidevs.androidmaster.coleccion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ActivityColeccionDetallesBinding
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.manga.MangaListAdapter
//import com.aristidevs.androidmaster.manga.MangaListDataResponse
import com.aristidevs.androidmaster.serie.SerieListActivity
import com.aristidevs.androidmaster.manga.MangaListActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ColeccionDetallesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityColeccionDetallesBinding
    private lateinit var adapter: MangaListAdapter
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityColeccionDetallesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = "eduardo@gmail.com"
        initUI(userId)

        binding.btnflecha.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initUI(userId: String) {
        // Ocultar todos los elementos al inicio
        binding.rvMangaListColeccion.isVisible = false
        binding.txtTotalComics.isVisible = false
        binding.txtTotalSeries.isVisible = false
        binding.txtCompletados.isVisible = false
        binding.txtPorCompletar.isVisible = false
        binding.barraDeProgresoCircular.isVisible = false
        binding.tvPercentage.isVisible = false
        binding.comics.isVisible = false
        binding.series.isVisible = false
        binding.misseries.isVisible = false
        binding.comicsagregados.isVisible = false
        binding.progressBarCarga.isVisible = true // Mostrar el ProgressBar

        adapter = MangaListAdapter()
        binding.rvMangaListColeccion.setHasFixedSize(true)
        binding.rvMangaListColeccion.layoutManager =
            GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
        binding.rvMangaListColeccion.adapter = adapter

        adapter.fetchDataFromFirebase(userId)

        binding.btnActivitySerieList.setOnClickListener {
            navigateToSerieList()  // Navegar a la actividad de Series
        }
        binding.btnActivityMangaList.setOnClickListener {
            navigateToMangaList()  // Navegar a la actividad de Mangas
        }

        db.collection("usuarios").document(userId)
            .collection("coleccion").document("coleccionDetalles")
            .get().addOnSuccessListener { document ->
                val totalMangas = document.getDouble("TotalMangas")?.toInt() ?: 0
                val totalSeries = document.getDouble("TotalSeries")?.toInt() ?: 0
                val seriesCom = document.getDouble("seriesCom")?.toInt() ?: 0
                val seriesPorCom = document.getDouble("seriesPorCom")?.toInt() ?: 0
                val porcentajeSerie = (document.getDouble("porcentajeSerie") ?: 0.0) * 100

                binding.txtTotalComics.text = totalMangas.toString()
                binding.txtTotalSeries.text = totalSeries.toString()
                binding.txtCompletados.text = "Completados: $seriesCom"
                binding.txtPorCompletar.text = "Por completar: $seriesPorCom"
                binding.tvPercentage.text = "$porcentajeSerie%"
                binding.barraDeProgresoCircular.progress = porcentajeSerie.toInt()

                // Ocultar el ProgressBar y mostrar los elementos
                binding.progressBarCarga.isVisible = false
                binding.rvMangaListColeccion.isVisible = true
                binding.txtTotalComics.isVisible = true
                binding.txtTotalSeries.isVisible = true
                binding.txtCompletados.isVisible = true
                binding.txtPorCompletar.isVisible = true
                binding.barraDeProgresoCircular.isVisible = true
                binding.tvPercentage.isVisible = true
                binding.comics.isVisible = true
                binding.series.isVisible = true
                binding.misseries.isVisible = true
                binding.comicsagregados.isVisible = true
            }.addOnFailureListener {
                // Ocultar el ProgressBar en caso de error
                binding.progressBarCarga.isVisible = false
            }
    }



    private fun navigateToMangaList() {
        val intent = Intent(this, MangaListActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSerieList() {
        val intent = Intent(this, SerieListActivity::class.java)
        startActivity(intent)
    }


















//    private lateinit var binding: ActivityColeccionDetallesBinding
//    private lateinit var retrofit: Retrofit
//    private lateinit var adapter: MangaListAdapter
//    private var db = FirebaseFirestore.getInstance()
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityColeccionDetallesBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        retrofit = getRetrofit()
//        val userId = intent.getIntExtra("USER_ID", -1)
//
//        // Verificar si el userId es válido (no es -1)
//        if (userId != -1) {
//            // Pasar el userId a initUI
//            initUI(userId.toString())
//        }
//
//        binding.btnflecha.setOnClickListener {
//            // Acción para regresar a la actividad anterior utilizando onBackPressedDispatcher
//            onBackPressedDispatcher.onBackPressed()
//        }
//
//
//    }
//
//    private fun initUI(userId: String) {
//        adapter = MangaListAdapter()
//        binding.rvMangaListColeccion.setHasFixedSize(true)
//        binding.rvMangaListColeccion.layoutManager =
//            GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
//        binding.rvMangaListColeccion.adapter = adapter
//        binding.btnActivitySerieList.setOnClickListener {
//            navigateToSerieList(userId.toInt())  // Navegar a la actividad de Series
//        }
//        binding.btnActivityMangaList.setOnClickListener {
//            navigateToMangaList(userId.toInt())  // Navegar a la actividad de Mangas
//        }
//
//        db.collection("coleccion").document("usuario1").get().addOnSuccessListener { binding.txtTotalComics.text = it.get("TotalMangas") as String? }
//
//
//    }
//
//    private fun navigateToMangaList(id: Int) {
//        val intent = Intent(this, MangaListActivity::class.java)
//        intent.putExtra("USER_ID", id)
//        startActivity(intent)
//    }
//
//    private fun navigateToSerieList(id: Int) {
//        val intent = Intent(this, SerieListActivity::class.java)
//        intent.putExtra("USER_ID", id)
//        startActivity(intent)
//    }
//
//    private fun searchColeccionByID(id: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val myResponse = retrofit.create(ApiServiceManga::class.java).searchColeccionById(id)
//            if (myResponse.isSuccessful) {
//                Log.i("aristidevs", "Funciona")
//                val response: ColeccionDetallesDataResponse? = myResponse.body()
//                if (response != null) {
//                    runOnUiThread {
//                        // Extraer los valores directamente como enteros, asegurándose de que no sea nulo
//                        val mangasTotales = response.coleccionDetalle.mangasTot?.toIntOrNull() ?: 0
//                        val seriesTotales = response.coleccionDetalle.seriesTot?.toIntOrNull() ?: 0
//                        val seriesPorCompletar = response.coleccionDetalle.seriesPorComTot?.toIntOrNull() ?: 0
//                        val seriesCompletadas = response.coleccionDetalle.seriesComTot?.toIntOrNull() ?: 0
//                        val porcentajeSeries = response.coleccionDetalle.seriesPorentaje ?: 0.0
//
//                        // Asignar los valores a los TextView
//                        binding.txtTotalSeries.text = seriesTotales.toString()
//                        binding.txtPorCompletar.text = "Por completar: " +seriesPorCompletar.toString()
//                        binding.txtCompletados.text =  "Completados: " +seriesCompletadas.toString()
//                        binding.tvPercentage.text = "${porcentajeSeries.toInt()}%" // Convertir a entero para mostrar el porcentaje
//
//                        // Actualizar el CircularProgressIndicator
//                        binding.barraDeProgresoCircular.progress = porcentajeSeries.toInt() // El progreso es el porcentaje entero
//                    }
//                }
//            } else {
//                    Log.i("aristidevs", "No funciona, algo salió mal")
//            }
//        }
//    }


//    private fun searchMangaByID(id: String) {
//        binding.progressBarColeccion.isVisible = true
//        CoroutineScope(Dispatchers.IO).launch {
//            val myResponse = retrofit.create(ApiServiceManga::class.java).getMangaByID(id) // Cambia a `getMangaByID`
//            if (myResponse.isSuccessful) {
//                Log.i("aristidevs", "Funciona")
//                val response: MangaListDataResponse? = myResponse.body()
//                if (response != null) {
//                    runOnUiThread {
//                        // Limitar a solo 3 mangas
//                        val limitedList = response.mangaLista.take(3)
//                        adapter.updateList(limitedList)
//                        binding.progressBarColeccion.isVisible = false
//                    }
//                }
//            } else {
//                Log.i("aristidevs", "no funciona que pedo")
//            }
//        }
//    }


    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://192.168.1.69:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
