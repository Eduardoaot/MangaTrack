package com.aristidevs.androidmaster.manga

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.aristidevs.androidmaster.databinding.ActivityMangaListBinding
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaActivity
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaActivity.Companion.MANGA_ID
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaActivity.Companion.USER_ID
import com.aristidevs.androidmaster.network.RetrofitClient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class MangaListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMangaListBinding
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: MangaListAdapter
    private var shouldUpdateList = false

    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMangaListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = RetrofitClient.getRetrofit()

        val userId = intent.getIntExtra("USER_ID", -1)

        // Verificar si el userId es válido (no es -1)
        if (userId != -1) {
            // Pasar el userId a initUI
            initUI(userId.toString())
        }

        binding.btnflecha.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId != -1) {

            initUI(userId.toString())
        }
    }

    private fun initUI(userId: String) {
        adapter = MangaListAdapter { navigateToDetail(it, userId.toInt()) }
        binding.rvMangaList.setHasFixedSize(true)
        binding.rvMangaList.layoutManager =
            GridLayoutManager(this, 3,
                GridLayoutManager.VERTICAL, false)
        binding.rvMangaList.adapter = adapter

        binding.searchView.setIconifiedByDefault(false)
        // Llama a la función `searchByID` directamente con el ID deseado
        searchByID(userId)


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchMangaByName(query.orEmpty(), userId)
                return false
            }
            override fun onQueryTextChange(newText: String?) = false
        })

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Cuando el usuario presiona "Enter".
                if (query.isNullOrEmpty()) {
                    // Si el campo de búsqueda está vacío, llama a searchByID(id).
                    searchByID(userId)
                } else {
                    // Si hay texto, realiza la búsqueda por nombre.
                    searchMangaByName(query, userId)
                }
                return true // Indica que el evento fue manejado.
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Cuando el texto en el SearchView cambia.
                if (newText.isNullOrEmpty()) {
                    // Si el texto está vacío (por ejemplo, el usuario hizo clic en la "X").
                    searchByID(userId)
                }
                return true // Indica que el evento fue manejado.
            }
        })
    }

    private fun searchByID(id: String){
        binding.progressBar.isVisible = true
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse = retrofit.create(ApiServiceManga::class.java).getMangaByID(id) // Cambia a `getMangaByID`
            if(myResponse.isSuccessful){
                Log.i("aristidevs", "Funciona")
                val response: MangaListDataResponse? = myResponse.body()
                if(response != null){
                    runOnUiThread {
                        adapter.updateList(response.mangaLista)
                        binding.progressBar.isVisible = false
                    }
                }
            } else {
                Log.i("aristidevs", "no funciona que pedo")
            }
        }
    }

    private fun searchMangaByName(name: String, userId: String) {
        binding.progressBar.isVisible = true
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse = retrofit.create(ApiServiceManga::class.java).getMangaByID(userId)
            if (myResponse.isSuccessful) {
                val response: MangaListDataResponse? = myResponse.body()
                runOnUiThread {
                    if (response != null) {
                        // Filtrar por serieNom que coincida con el nombre ingresado
                        val filteredList = response.
                        mangaLista.filter { it.serieNom.contains(name, ignoreCase = true) }
                        adapter.updateList(filteredList)
                    }
                    binding.progressBar.isVisible = false
                }
            } else {
                Log.i("aristidevs", "Error en la búsqueda")
            }
        }
    }

    private fun navigateToDetail(id_manga: Int, id_usuario: Int) {
        Log.d("NavigateToDetail", "ID serie: $id_manga")
        Log.d("NavigateToDetail", "ID usuario: $id_usuario")

        val intent = Intent(this, DetalleMangaActivity::class.java)
        intent.putExtra(MANGA_ID, id_manga) // Pasar el ID como un extra
        intent.putExtra(USER_ID, id_usuario)
        startActivity(intent)
    }

}