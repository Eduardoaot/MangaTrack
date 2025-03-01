package com.aristidevs.androidmaster.buscador

import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.aristidevs.androidmaster.databinding.ActivityBuscadorDetallesBinding
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaActivity.Companion.SERIE_ID
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaActivity.Companion.USER_ID
import com.aristidevs.androidmaster.detallesserie.DetalleSerieActivity
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class BuscadorDetallesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuscadorDetallesBinding
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: BuscadorDetallesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retrofit = RetrofitClient.getRetrofit()

        binding = ActivityBuscadorDetallesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name") ?: ""
        val userId = intent.getIntExtra("USER_ID", -1)

        initUI(name, userId)

        binding.btnflecha.setOnClickListener {
            // Acción para regresar a la actividad anterior utilizando onBackPressedDispatcher
            onBackPressedDispatcher.onBackPressed()
        }


    }

    private fun initUI(name: String, id: Int) {

        adapter = BuscadorDetallesAdapter { navigateToDetail(it, id) }
        binding.rvResutados.setHasFixedSize(true)
        binding.rvResutados.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)
        binding.rvResutados.adapter = adapter

        binding.txtBusqueda.text = name

        searchSeries(name)

    }

    private fun searchSeries(name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Ejecutamos la solicitud a la API usando el nombre como parámetro
            val myResponse = retrofit.create(ApiServiceManga::class.java).searchSearchDataSeries(name)

            if (myResponse.isSuccessful) {
                // Verificamos si la respuesta fue exitosa y obtenemos el cuerpo de la respuesta
                val response: BuscadorDetallesDataResponse? = myResponse.body()

                if (response != null) {
                    // Accedemos directamente a la lista de series desde `result`
                    val busquedaLista = response.busquedaListaDetalles

                    // Volvemos al hilo principal para actualizar la UI
                    runOnUiThread {
                        // Actualizamos el RecyclerView con los resultados
                        adapter.updateList(busquedaLista)  // Actualizamos la lista del RecyclerView con los resultados
                    }
                }
            } else {
                // Si la respuesta no es exitosa, mostramos un mensaje en el log
                Log.i("BuscadorActivity", "Error en la búsqueda")
                runOnUiThread {
                    // Aquí puedes agregar el manejo del error si lo necesitas
                }
            }
        }
    }

    private fun navigateToDetail(id_serie: Int, id_usuario: Int) {

        Log.d("NavigateToDetail", "ID serie: $id_serie")
        Log.d("NavigateToDetail", "ID usuario: $id_usuario")

        val intent = Intent(this, DetalleSerieActivity::class.java)
        intent.putExtra(SERIE_ID, id_serie.toInt()) // Pasar el ID como un extra
        intent.putExtra(USER_ID, id_usuario)
        startActivity(intent)
    }
}