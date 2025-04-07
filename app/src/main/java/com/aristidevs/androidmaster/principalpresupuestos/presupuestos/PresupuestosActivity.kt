package com.aristidevs.androidmaster.principalpresupuestos.presupuestos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ActivityPresupuestosBinding
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaActivity
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.network.RetrofitClient
import com.aristidevs.androidmaster.principalpresupuestos.bolsa.BolsaActivity
import com.aristidevs.androidmaster.principalpresupuestos.guardados.GuardadosActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class PresupuestosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPresupuestosBinding
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: PendienteMangaAdapter
    private var totalPrecio: Float = 0f
    private val mangasEstados = mutableMapOf<Int, Boolean>() // Map de idManga a su estado (true/false)
    private val mangasBolsa = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresupuestosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = RetrofitClient.getRetrofit()

        val id_usuario: Int = intent.getIntExtra(DetalleMangaActivity.USER_ID, -1)

        binding.btnflecha.setOnClickListener {
            onBackPressed()
        }

        findViewById<CardView>(R.id.btnPresupuestosGuardados).setOnClickListener {
            navigateToPresupuestosGuardados(id_usuario)
        }

        findViewById<CardView>(R.id.btnBolsa).setOnClickListener {
            navigateToBolsa(id_usuario, mangasBolsa)
        }

        // Inicializar la interfaz de usuario
        initUI(id_usuario)
    }

    private fun navigateToPresupuestosGuardados(idUsuario: Int) {
        val intent = Intent(this, GuardadosActivity::class.java)
        intent.putExtra(DetalleMangaActivity.USER_ID, idUsuario)
        startActivity(intent)
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

    override fun onResume() {
        super.onResume()

        // Limpiar estados temporales
        mangasEstados.clear()
        mangasBolsa.clear()
        totalPrecio = 0f
        binding.txtTotalBolsa.text = "Total: $${String.format("%.2f", 0.0)}"

        // Reiniciar UI
        binding.searchView.setQuery("", false)
        binding.searchView.clearFocus()
        binding.spinnerPrecios.setSelection(0)

        // Obtener ID de usuario del Intent (si no está en la propiedad de clase)
        val idUsuario = intent.getIntExtra(DetalleMangaActivity.USER_ID, -1)
        if (idUsuario != -1) {
            // Recargar datos frescos desde la API
            obtenerMangasFaltantes(idUsuario)
        }
    }


    private fun navigateToBolsa(idUsuario: Int, mangasBolsa: MutableList<Int>) {
        if (mangasBolsa.isEmpty()) {
            Toast.makeText(this, "Por favor, agrega algún manga antes de continuar.", Toast.LENGTH_SHORT).show()
            return // No continúa con la navegación
        }

        val intent = Intent(this, BolsaActivity::class.java)
        intent.putExtra(DetalleMangaActivity.USER_ID, idUsuario)
        intent.putIntegerArrayListExtra("MANGAS_BOLSA", ArrayList(mangasBolsa)) // Pasar la lista de IDs
        startActivity(intent)
    }


    private fun initUI(idUsuario: Int) {
        adapter = PendienteMangaAdapter(
            onAddClick = { idManga, precio, isAdded ->
                if (isAdded) {
                    agregarManga(idManga, precio)
                } else {
                    eliminarManga(idManga, precio)
                }
            },
            onItemSelected = { idManga ->
                navigateToDetail(idManga, idUsuario)
            }
        )

        binding.rvMangasFaltantes.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(this, 3)
        binding.rvMangasFaltantes.layoutManager = gridLayoutManager
        binding.rvMangasFaltantes.adapter = adapter

        obtenerMangasFaltantes(idUsuario) // Obtener mangas y precios

        binding.searchView.setIconifiedByDefault(false)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrEmpty()) {
                    obtenerMangasFaltantes(idUsuario)
                } else {
                    searchMangaByName(query, idUsuario)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    obtenerMangasFaltantes(idUsuario)
                }
                return true
            }
        })
    }

    private fun setupSpinner(mangas: List<PendienteMangaItemConEstado>) {
        val preciosUnicos = mangas.map { it.precio }.distinct().sorted()
        val opciones = listOf("Todos") + preciosUnicos.map { "$$it" } // Agregar "Todos" como opción inicial

        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPrecios.adapter = adapterSpinner

        // Manejar la selección de precios
        binding.spinnerPrecios.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = opciones[position]
                filtrarPorPrecio(selected, mangas)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada
            }
        }
    }


    private fun filtrarPorPrecio(precioSeleccionado: String, mangas: List<PendienteMangaItemConEstado>) {
        val listaFiltrada = if (precioSeleccionado == "Todos") {
            mangas
        } else {
            val precio = precioSeleccionado.replace("$", "").toFloat()
            mangas.filter { it.precio == precio }
        }

        adapter.updateList(listaFiltrada)
    }




    private fun agregarManga(idManga: Int, precio: Float) {
        mangasEstados[idManga] = true  // Guardar el estado en el Map
        mangasBolsa.add(idManga)
        totalPrecio += precio
        binding.txtTotalBolsa.text = "Total: $${String.format("%.2f", totalPrecio)}"

        // Actualizar la lista con los nuevos estados
        actualizarListaConEstado()
        println(mangasBolsa)
    }

    private fun eliminarManga(idManga: Int, precio: Float) {
        mangasEstados.remove(idManga) // Eliminar del Map en lugar de marcar como false
        mangasBolsa.remove(idManga)
        totalPrecio -= precio
        binding.txtTotalBolsa.text = "Total: $${String.format("%.2f", totalPrecio)}"

        // Actualizar la lista con los nuevos estados
        actualizarListaConEstado()
        println(mangasBolsa)
    }

    private fun actualizarListaConEstado() {
        val nuevaLista = adapter.mangasConEstado.map { manga ->
            manga.copy(isAdded = mangasEstados[manga.idManga] ?: false)
        }
        adapter.updateList(nuevaLista)
    }


    private fun navigateToDetail(id_manga: Int, id_usuario: Int) {
        val intent = Intent(this, DetalleMangaActivity::class.java)
        intent.putExtra(DetalleMangaActivity.MANGA_ID, id_manga)
        intent.putExtra(DetalleMangaActivity.USER_ID, id_usuario)
        startActivity(intent)
    }

    private fun obtenerMangasFaltantes(idUsuario: Int) {
        showLoadingState()
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse = retrofit.create(ApiServiceManga::class.java).obtenerMangasFaltantes(idUsuario.toString())
            if (myResponse.isSuccessful) {
                val response = myResponse.body()
                if (response != null) {
                    // Mapear los mangas a la nueva lista que incluye el estado (isAdded)
                    val mangasConEstado = response.mangasPendientes.map { manga ->
                        val isAdded = mangasEstados[manga.idManga] ?: false // Comprobar si el manga está agregado
                        PendienteMangaItemConEstado(
                            manga.idManga,
                            manga.mangaImg,
                            manga.mangaNum,
                            manga.precio,
                            manga.serieNom,
                            isAdded
                        )
                    }
                    runOnUiThread {
                        // Actualizar el adaptador con la nueva lista de mangas con estado
                        adapter.updateList(mangasConEstado)
                        setupSpinner(mangasConEstado)
                        showContent()
                    }
                }
            } else {
                Log.e("Pendientes", "Error en la consulta: ${myResponse.code()}")
            }
        }
    }

    private fun searchMangaByName(name: String, userId: Int) {
        showLoadingState()
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse = retrofit.create(ApiServiceManga::class.java).obtenerMangasFaltantes(userId.toString())
            if (myResponse.isSuccessful) {
                val response: PendienteListFaltantesDataResponse? = myResponse.body()
                runOnUiThread {
                    if (response != null) {
                        val filteredList = response.mangasPendientes.filter {
                            it.serieNom.contains(name, ignoreCase = true)
                        }.map { manga ->
                            val isAdded = mangasEstados[manga.idManga] ?: false
                            PendienteMangaItemConEstado(
                                manga.idManga,
                                manga.mangaImg,
                                manga.mangaNum,
                                manga.precio,
                                manga.serieNom,
                                isAdded
                            )
                        }
                        adapter.updateList(filteredList)
                        setupSpinner(filteredList)
                        showContent()
                    }
                }
            } else {
                Log.i("aristidevs", "Error en la búsqueda de mangas faltantes")
            }
        }
    }
}