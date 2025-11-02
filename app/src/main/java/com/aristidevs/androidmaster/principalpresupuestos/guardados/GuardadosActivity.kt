package com.aristidevs.androidmaster.principalpresupuestos.guardados

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aristidevs.androidmaster.databinding.ActivityGuardadosBinding
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaActivity
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.network.RetrofitClient
import com.aristidevs.androidmaster.principalpresupuestos.detallepresupuesto.DetallePresupuestoActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit


class GuardadosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGuardadosBinding
    private lateinit var adapter: PresupuestosAdapter
    private lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGuardadosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = RetrofitClient.getRetrofit()


        val idUsuario: Int = intent.getIntExtra(DetalleMangaActivity.USER_ID, -1)
        Log.i("PresupuestosAdapter", "Desoyes de user")
        if (idUsuario != -1) {
            Log.i("PresupuestosAdapter", "Inicializando adaptador de presupuestos")
            initUI(idUsuario)
        }

        binding.btnflecha.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        val userId = intent.getIntExtra(DetalleMangaActivity.USER_ID, -1)
        if (userId != -1) {
            obtenerPresupuestos(userId) // Siempre actualiza al regresar
        }
    }

    private fun initUI(userId: Int) {
        // Al crear el adaptador:
        adapter = PresupuestosAdapter(
            onItemSelected = { presupuestoId ->
                // Manejar clic en presupuesto
                navigateToPresupuestoDetail(presupuestoId, userId)
            },
            onMangaSelected = { mangaId ->
                // Manejar clic en manga
                navigateToMangaDetail(mangaId, userId)
            },
            onDeleteSelected = { presupuestoId ->
                showDeleteConfirmation(presupuestoId, userId)
            }
        )
        binding.rvPresupuestosGuardados.layoutManager = LinearLayoutManager(this)
        binding.rvPresupuestosGuardados.adapter = adapter
        obtenerPresupuestos(userId)
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

    private fun BorrarPresupuesto(presupuestoId: Int, userId: Int) {
        showLoadingState()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = retrofit.create(ApiServiceManga::class.java)
                    .borrarPresupuesto(presupuestoId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        // Mostrar mensaje de éxito
                        Toast.makeText(
                            this@GuardadosActivity,
                            "Presupuesto eliminado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Actualizar la lista
                        obtenerPresupuestos(userId)
                        showContent()
                    } else {
                        // Manejar error
                        Toast.makeText(
                            this@GuardadosActivity,
                            "Error al eliminar el presupuesto: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        showContent()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@GuardadosActivity,
                        "Error de conexión: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    showContent()
                }
            }
        }
    }

    private fun showDeleteConfirmation(presupuestoId: Int, userId: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar este presupuesto?")
            .setPositiveButton("Eliminar") { _, _ ->
                BorrarPresupuesto(presupuestoId, userId)
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun navigateToMangaDetail(mangaId: Int, userId: Int) {
        val intent = Intent(this, DetalleMangaActivity::class.java)
        intent.putExtra(DetalleMangaActivity.MANGA_ID, mangaId)
        intent.putExtra(DetalleMangaActivity.USER_ID, userId)
        startActivity(intent)
    }

    private fun navigateToPresupuestoDetail(presupuestoId: Int, userId: Int) {
        val intent = Intent(this, DetallePresupuestoActivity::class.java)
        intent.putExtra(DetalleMangaActivity.PLAN_ID, presupuestoId)
        intent.putExtra(DetalleMangaActivity.USER_ID, userId)
        startActivity(intent)
    }

    private fun navigateToDetail(idPresupuesto: Int) {
        val intent = Intent(this, DetallePresupuestoActivity::class.java).apply {
            putExtra("ID_PRESUPUESTO", idPresupuesto)
        }
        startActivity(intent)
    }

    private fun obtenerPresupuestos(idUsuario: Int) {
        showLoadingState()
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse = retrofit.create(ApiServiceManga::class.java)
                .obtenerPresupuestosGuardados(idUsuario.toString())

            if (myResponse.isSuccessful) {

                val response: GuardadosDataResponse? = myResponse.body()

                if (response != null) {
                    runOnUiThread {
                        val detalles = response.presupuestos
                        adapter.updateList(detalles)
                        showContent()
                    }
                }
            } else {
                Toast.makeText(
                    this@GuardadosActivity,
                    "Error al obtener los presupuestos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}