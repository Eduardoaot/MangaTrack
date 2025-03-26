package com.aristidevs.androidmaster.buscador

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ActivityBuscadorBinding
import com.aristidevs.androidmaster.databinding.ActivityInicioSesionMainBinding
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.manga.MangaListAdapter
import com.aristidevs.androidmaster.network.RetrofitClient
import com.aristidevs.androidmaster.superheroapp.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BuscadorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuscadorBinding
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: BuscadorAdapter  // Declaración del adaptador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuscadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getIntExtra("USER_ID", -1)
        retrofit = RetrofitClient.getRetrofit()

        initUI(userId)  // Configura la UI
    }

    private fun initUI(userId: Int) {
        binding.searchView.setIconifiedByDefault(false)

        adapter = BuscadorAdapter { navigateToDetail(it, userId) }
        binding.rvResutados.setHasFixedSize(true)
        binding.rvResutados.layoutManager =
            GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)
        binding.rvResutados.adapter = adapter

        binding.btnflecha.setOnClickListener {
            // Acción para regresar a la actividad anterior utilizando onBackPressedDispatcher
            onBackPressedDispatcher.onBackPressed()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    // Ejecutar la búsqueda solo cuando se presiona Enter
                    searchSeries(it)
                }
                return true  // Esto indica que el evento ha sido manejado
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // No hacer nada cuando el texto cambia
                return false
            }
        })
    }

    private fun searchSeries(name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Ejecutamos la solicitud a la API usando el nombre como parámetro
            val myResponse = retrofit.create(ApiServiceManga::class.java).searchSeries(name)

            if (myResponse.isSuccessful) {
                // Verificamos si la respuesta fue exitosa y obtenemos el cuerpo de la respuesta
                val response: BuscadorDataResponse? = myResponse.body()

                if (response != null) {
                    // Accedemos directamente a la lista de series desde `result`
                    val busquedaLista = response.busquedaLista

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

    fun navigateToDetail(name: String, id: Int) {
        // Crear el Intent para abrir la siguiente actividad
        val intent = Intent(this, BuscadorDetallesActivity::class.java)

        // Poner el valor 'name' como un extra en el Intent
        intent.putExtra("name", name)  // 'name' es el valor que deseas pasar
        intent.putExtra("USER_ID", id)
        // Log para mostrar la información que se pasa
        Log.d("NavigateToDetail", "Información pasada: $name")

        // Iniciar la actividad
        startActivity(intent)
    }

}
