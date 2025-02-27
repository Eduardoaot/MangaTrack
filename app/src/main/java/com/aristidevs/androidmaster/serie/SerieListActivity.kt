package com.aristidevs.androidmaster.serie

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ActivitySerieListBinding
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.manga.MangaListAdapter
//import com.aristidevs.androidmaster.manga.MangaListDataResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SerieListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySerieListBinding
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: SerieListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySerieListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = getRetrofit()

        val userId = intent.getIntExtra("USER_ID", -1)

        // Verificar si el userId es válido (no es -1)
        if (userId != -1) {
            // Pasar el userId a initUI
            initUI(userId.toString())
        }

        // Agregar botón de flecha
        binding.btnflecha.setOnClickListener {
            // Acción para regresar a la actividad anterior utilizando onBackPressedDispatcher
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initUI(userId: String) {
        // Inicializa el adaptador y configura el RecyclerView
        adapter = SerieListAdapter()
        binding.rvSerieList.setHasFixedSize(true)
        binding.rvSerieList.layoutManager =
            GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)
        binding.rvSerieList.adapter = adapter

        binding.searchViewSerie.setIconifiedByDefault(false)  // No muestra el icono solo como una lupa

        // Llama a la función `searchByID` directamente con el ID deseado
        searchByID(userId)

        // Configura el SearchView para manejar las consultas
        binding.searchViewSerie.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Cuando el usuario presiona "Enter" o envía la búsqueda
                if (query.isNullOrEmpty()) {
                    // Si el campo de búsqueda está vacío, se realiza la búsqueda por ID
                    searchByID(userId)
                } else {
                    // Si hay texto, realiza la búsqueda por nombre
                    searchSerieByName(query, userId)
                }
                return true // Indica que el evento fue manejado
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Cuando el texto en el SearchView cambia
                if (newText.isNullOrEmpty()) {
                    // Si el texto está vacío (por ejemplo, el usuario hizo clic en la "X")
                    searchByID(userId)
                }
                return true // Indica que el evento fue manejado
            }
        })
    }


    private fun searchByID(id: String){
        binding.progressBar.isVisible = true
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse = retrofit.create(ApiServiceManga::class.java).searchSerieById(id) // Cambia a `getMangaByID`
            if(myResponse.isSuccessful){
                Log.i("aristidevs", "Funciona")
                val response: SerieListDataResponse? = myResponse.body()
                if(response != null){
                    runOnUiThread {
                        adapter.updateList(response.serieLista)
                        binding.progressBar.isVisible = false
                    }
                }
            } else {
                Log.i("aristidevs", "no funciona que pedo")
            }
        }
    }

    private fun searchSerieByName(name: String, userId: String) {
        binding.progressBar.isVisible = true
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse = retrofit.create(ApiServiceManga::class.java).searchSerieById(userId)
            if (myResponse.isSuccessful) {
                val response: SerieListDataResponse? = myResponse.body()
                runOnUiThread {
                    if (response != null) {
                        // Filtrar por serieNom que coincida con el nombre ingresado
                        runOnUiThread {
                            val filteredList = response.serieLista.filter {
                                it.SerieNom.contains(
                                    name,
                                    ignoreCase = true
                                )
                            }
                            adapter.updateList(filteredList)
                        }
                    }
                    binding.progressBar.isVisible = false
                }
            } else {
                Log.i("aristidevs", "Error en la búsqueda")
            }
        }
    }



    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://192.168.1.69:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}