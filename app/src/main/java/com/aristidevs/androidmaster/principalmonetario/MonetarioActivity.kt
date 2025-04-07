package com.aristidevs.androidmaster.principalmonetario

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aristidevs.androidmaster.databinding.ActivityMonetarioBinding
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaActivity
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.manga.MangaListAdapter
import com.aristidevs.androidmaster.manga.MangaListDataResponse
import com.aristidevs.androidmaster.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class MonetarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonetarioBinding
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: MonetarioAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonetarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = RetrofitClient.getRetrofit()

        val id_usuario: Int = intent.getIntExtra(DetalleMangaActivity.USER_ID, -1)

        binding.btnflecha.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            onBackPressed()
        }

        initUI(id_usuario)

    }

    private fun initUI(idUsuario: Int) {
        adapter = MonetarioAdapter()
        binding.rvMonetario.setHasFixedSize(true)
        // Cambia GridLayoutManager por LinearLayoutManager vertical
        binding.rvMonetario.layoutManager = LinearLayoutManager(this)
        binding.rvMonetario.adapter = adapter
        obtenerDatosMonetario(idUsuario)
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

    private fun showErrorState(message: String) {
        runOnUiThread {
            binding.progressBar.visibility = View.GONE
            binding.mainContent.visibility = View.GONE
            binding.errorView.visibility = View.VISIBLE
            binding.errorText.text = message

            binding.retryButton.setOnClickListener {
                val id_usuario = intent.getIntExtra(DetalleMangaActivity.USER_ID, -1)
                if (id_usuario != -1) {
                    obtenerDatosMonetario(id_usuario)
                }
            }
        }
    }

    private fun obtenerDatosMonetario(idUsuario: Int) {
        showLoadingState()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = retrofit.create(ApiServiceManga::class.java).obtenerEstadisticasUsuario(idUsuario)

                if (response.isSuccessful) {
                    val datos = response.body()

                    if (datos != null) {
                        runOnUiThread {
                            adapter.updateList(datos.mejoresAhorros)

                            binding.NumeroTotalLeidos.text = datos.totalComprados.toString()
                            binding.NumeroTotalLeidosAnio.text = datos.totalCompradosAnio.toString()
                            binding.NumeroTotalLeidosMes.text = datos.totalMes.toString()
                            binding.txtValorTotal.text = "Valor total: ${datos.valorTotal}$"
                            binding.txtAhorroTotal.text = "Ahorro Total: ${datos.ahorroTotal}$"
                            binding.txtGastoTotal.text = "Gasto Total: ${datos.gastoTotal}$"

                            showContent()
                        }
                    } else {
                        showErrorState("Los datos del usuario no están disponibles.")
                    }
                } else {
                    showErrorState("Error en la respuesta del servidor: ${response.code()}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                showErrorState("Error de conexión: ${e.message}")
            }
        }
    }

}