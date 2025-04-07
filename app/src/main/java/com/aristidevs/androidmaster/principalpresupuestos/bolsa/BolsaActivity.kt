package com.aristidevs.androidmaster.principalpresupuestos.bolsa

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ActivityBolsaBinding
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaActivity
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class BolsaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBolsaBinding
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: PlanAdapter
    private var mangasBolsa: MutableList<Int> = mutableListOf() // Lista mutable para guardar los IDs
    private var totalPrecio: Float = 0f
    private var descuento = 0.0f
    private var nombreBolsa: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBolsaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = RetrofitClient.getRetrofit()

        val idUsuario: Int = intent.getIntExtra(DetalleMangaActivity.USER_ID, -1)
        mangasBolsa = intent.getIntegerArrayListExtra("MANGAS_BOLSA") ?: mutableListOf() // Recuperar la lista

        binding.btnflecha.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val btnGuardarPresupuestos = findViewById<CardView>(R.id.btnGuardarPresupuestos)
        btnGuardarPresupuestos.setOnClickListener {
            guardarPresupuestos(mangasBolsa, descuento, nombreBolsa, idUsuario)
        }

        initUI(idUsuario, mangasBolsa)

        // DEBUG: Mostrar en logs los valores recibidos
        Log.d("BolsaActivity", "ID Usuario: $idUsuario")
        Log.d("BolsaActivity", "Mangas en bolsa: $mangasBolsa")

        // Cargar mangas con estado
    }

    private fun guardarPresupuestos(
        mangasBolsa: MutableList<Int>,
        descuento: Float,
        nombreBolsa: String?,
        idUsuario: Int
    ) {

        if (mangasBolsa.isNullOrEmpty()) {
            // Mostrar un mensaje de error si la lista de mangas está vacía o el nombre de la bolsa está vacío
            Toast.makeText(this, "La lista está vacía. Por favor, vuelve a realizar el plan.", Toast.LENGTH_SHORT).show()
            return // No realizar la solicitud si la lista de mangas o el nombre de la bolsa están vacíos
        }

        // Verificar si el nombre de la bolsa está vacío, null o solo contiene espacios
        if (nombreBolsa.isNullOrBlank()) {
            // Mostrar un mensaje de error si el nombre está vacío o es null
            Toast.makeText(this, "El nombre de la bolsa es obligatorio.", Toast.LENGTH_SHORT).show()
            return // No realizar la solicitud si el nombre está vacío
        }

        // Si el nombre está lleno, procedemos con la solicitud POST
        val guardarBolsaRequest = GuardarBolsaRequest(
            mangas_bolsa = mangasBolsa,
            descuento = descuento,
            nombreBolsa = nombreBolsa,
            idUsuario = idUsuario
        )
        showLoadingState()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Realizar la solicitud POST, sin esperar respuesta
                retrofit.create(ApiServiceManga::class.java).botonGuardarPresupuesto(guardarBolsaRequest)

                // Si llegamos aquí, la solicitud fue exitosa
                runOnUiThread {
                    // Realiza la acción después de que se haya guardado correctamente (por ejemplo, ir a la pantalla anterior)
                    Toast.makeText(this@BolsaActivity, "Presupuesto guardado correctamente", Toast.LENGTH_SHORT).show()
                    finish() // Regresa a la pantalla anterior
                    showContent()
                }

            } catch (e: Exception) {
                Log.e("Error", "Error al guardar presupuesto: ${e.message}")
            }
        }

    }

    private fun initUI(idUsuario: Int, mangasBolsa: List<Int>) {
        adapter = PlanAdapter(
            onAddClick = { idManga, precio, isAdded ->
                if (isAdded) {
                    // Lógica cuando el manga es agregado
                    eliminarManga(idManga, precio)
                } else {
                    eliminarManga(idManga, precio)
                }
            },
            onItemSelected = { idManga ->
                navigateToDetail(idManga, idUsuario)
            }
        )

        binding.rvMangasEnBolsa.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvMangasEnBolsa.layoutManager = linearLayoutManager
        binding.rvMangasEnBolsa.adapter = adapter


        val radioGroupDescuentos: RadioGroup = findViewById(R.id.radioGroupDescuentos)

        // Establecer un listener para el RadioGroup
        radioGroupDescuentos.setOnCheckedChangeListener { group, checkedId ->
            // Verificar qué RadioButton está seleccionado y asignar el descuento correspondiente
            when (checkedId) {
                R.id.rbDescuento0 -> descuento = 0.00f  // 10% de descuento
                R.id.rbDescuento10 -> descuento = 0.10f  // 10% de descuento
                R.id.rbDescuento20 -> descuento = 0.20f  // 20% de descuento
                R.id.rbDescuento25 -> descuento = 0.25f  // 25% de descuento
                R.id.rbDescuento30 -> descuento = 0.3f  // 30% de descuento
                R.id.rbDescuento3x2 -> descuento = 1.0f  // 3x2 (no es un porcentaje, asumiendo que da 100% descuento)
            }
            // Mostrar el valor de descuento seleccionado (opcional)
            println("Descuento seleccionado: $descuento")
            mostrarMangasEnBolsa(mangasBolsa)
        }

        val editTextNombreBolsa: EditText = findViewById(R.id.editNombreBolsa)

        editTextNombreBolsa.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // No es necesario hacer nada aquí
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // Actualizar la variable con el nuevo texto
                nombreBolsa = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable?) {
                // No es necesario hacer nada aquí
            }
        })


        mostrarMangasEnBolsa(mangasBolsa)
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

    private fun eliminarManga(idManga: Int, precio: Float) {
        // Eliminar el manga de la lista de la bolsa
        mangasBolsa.remove(idManga)

        // Ajustar el precio total
        totalPrecio -= precio

        // Actualizar la vista mostrando las mangas restantes en la bolsa
        mostrarMangasEnBolsa(mangasBolsa)
    }

    private fun mostrarMangasEnBolsa(mangasBolsa: List<Int>) {
        showLoadingState()
        CoroutineScope(Dispatchers.IO).launch {
            val requestData = AgregarPlanRequest(listaIds = mangasBolsa) // Enviar lista de IDs
            val myResponse = retrofit.create(ApiServiceManga::class.java).buscarMangasPost(requestData)

            if (myResponse.isSuccessful) {
                val response: AgregarPlanResponse? = myResponse.body()

                if (response != null) {
                    var totalSum = 0f
                    var subtotal = 0f
                    var descuentoCalculado = 0f
                    val mangasConDescuento = mutableListOf<Pair<PlanElementosItemResponse, Boolean>>()
                    val mangasRecibo = mutableListOf<PlanElementosItemResponse>()

                    response.mangasPendientes.forEach { manga ->
                        subtotal += manga.precio

                        // Si el descuento es 3x2, agregamos el manga con descuento del 100%
                        if (descuento == 1.0f) {
                            mangasConDescuento.add(manga to true)  // Manga con descuento
                        } else {
                            mangasConDescuento.add(manga to false)  // Manga sin descuento
                            val precioConDescuento = manga.precio * (1 - descuento)
                            totalSum += precioConDescuento
                        }

                        mangasRecibo.add(manga)
                    }

                    if (descuento == 1.0f) {
                        // Ordenamos los mangas por precio (de menor a mayor)
                        mangasConDescuento.sortBy { it.first.precio }

                        // Calculamos el número de grupos completos de 3 mangas
                        val totalGruposDe3 = mangasConDescuento.size / 3

                        // Sumamos el precio total de todos los mangas en mangasConDescuento
                        mangasConDescuento.forEach {
                            totalSum += it.first.precio
                        }

                        // Encontramos los índices de los mangas con los precios más bajos (igual al número de grupos)
                        val indicesConDescuento = mutableListOf<Int>()
                        for (i in 0 until totalGruposDe3) {
                            indicesConDescuento.add(i)  // Agregamos un índice por cada grupo
                        }

                        // Mark the elements with the smallest prices for the discount (true) and subtract their prices from totalSum
                        for (i in indicesConDescuento) {
                            val precioMasBajo = mangasConDescuento[i].first.precio
                            mangasConDescuento[i] = mangasConDescuento[i].copy(second = true)  // Marca como descuento del 100%
                            totalSum -= precioMasBajo  // Restamos el precio del manga más barato
                        }

                        // Ahora, aseguramos que los demás mangas tengan descuento como false
                        for (i in mangasConDescuento.indices) {
                            if (i !in indicesConDescuento) {  // Si el índice no está marcado como descuento
                                mangasConDescuento[i] = mangasConDescuento[i].copy(second = false)  // No aplica descuento
                            }
                        }
                    }

                    descuentoCalculado = subtotal - totalSum
                    totalPrecio = totalSum
                    var totalMangas = response.mangasPendientes.size

                    runOnUiThread {
                        binding.txtSubTotal.text = "SubTotal: $${String.format("%.2f", subtotal)}"
                        binding.txtDescuento.text = "Descuento: $${String.format("%.2f", descuentoCalculado)}"
                        binding.txtTotal.text = "Total: $${String.format("%.2f", totalPrecio)}"
                        binding.txtTotalMangasBolsa.text = "Total: $totalMangas"
                        adapter.updateList(response.mangasPendientes)

                        // Aseguramos de pasar los mangas y la información del descuento al adapter
                        val adapter = ReciboAdapter(mangasConDescuento, descuento)
                        val recyclerView: RecyclerView = findViewById(R.id.rvRecibo)
                        recyclerView.layoutManager = LinearLayoutManager(this@BolsaActivity)
                        recyclerView.adapter = adapter

                        Log.i("BolsaActivity", "Mangas en bolsa actualizados correctamente.")
                        println(mangasBolsa)
                        showContent()
                    }
                } else {
                    Log.e("BolsaActivity", "Respuesta vacía de la API.")
                }
            } else {
                Log.e("BolsaActivity", "Error en la consulta: ${myResponse.code()}")
            }
        }
    }

    private fun navigateToDetail(id_manga: Int, id_usuario: Int) {
        val intent = Intent(this, DetalleMangaActivity::class.java)
        intent.putExtra(DetalleMangaActivity.MANGA_ID, id_manga)
        intent.putExtra(DetalleMangaActivity.USER_ID, id_usuario)
        startActivity(intent)
    }
}

