package com.aristidevs.androidmaster.principalpresupuestos.detallepresupuesto

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ActivityDetallePresupuestoBinding
import com.aristidevs.androidmaster.detallesmanga.AgregarMangaRequest
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaActivity
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaDataResponse
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.network.RetrofitClient
import com.aristidevs.androidmaster.principalpresupuestos.bolsa.AgregarPlanRequest
import com.aristidevs.androidmaster.principalpresupuestos.bolsa.AgregarPlanResponse
import com.aristidevs.androidmaster.principalpresupuestos.bolsa.PlanElementosItemResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

class DetallePresupuestoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetallePresupuestoBinding
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: PlanAdapterDetalle
    private var mangasBolsa: MutableList<Int> = mutableListOf()
    private var mangasPreciosLista: MutableList<Float> = mutableListOf()
    private var totalPrecio: Float = 0f
    private var totalDescuento: Float = 0f
    private var descuento = 0.0f
    private var nombreBolsa: String? = null
    private var precio: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetallePresupuestoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar estado de carga inicial
        showLoadingState()

        retrofit = RetrofitClient.getRetrofit()
        val idPresupuesto = intent.getIntExtra(DetalleMangaActivity.PLAN_ID, -1)
        val idUsuario = intent.getIntExtra(DetalleMangaActivity.USER_ID, -1)

        if (idPresupuesto != -1) {
            cargarDatosIniciales(idPresupuesto)
        } else {
            setupUIWithEmptyState()
        }

        val btnActualizarPlan = findViewById<CardView>(R.id.btnActualizarPresupuesto)
        btnActualizarPlan.setOnClickListener {
            actualizarPresupuesto(idPresupuesto, nombreBolsa, descuento, mangasBolsa)
        }

        val btnMarcarComoComprados = findViewById<CardView>(R.id.btnMarcarComoComprados)
        btnMarcarComoComprados.setOnClickListener {
            println("Boton marcar como comprados funciona")
            println(totalDescuento)
            println("La lista de precios es: ${mangasPreciosLista}")
            println("La lista de mangas es: ${mangasBolsa}")


            marcarComoComprados(idUsuario, idPresupuesto)
        }
    }

    private fun marcarComoComprados(idUsuario: Int, idPresupuesto: Int) {
        showLoadingState()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Agregar TODOS los mangas con manejo de errores individual
                val agregadosExitosos = agregarTodosLosMangasConReintentos(idUsuario)

                if (!agregadosExitosos) {
                    throw Exception("Algunos mangas no se pudieron agregar")
                }

                // 2. Operaciones posteriores (solo si todos los mangas se agregaron)
                guardarEnMonetario(idUsuario, totalDescuento, mangasBolsa.size)
                BorrarPresupuesto(idPresupuesto, idUsuario)

                // 3. Notificación y cierre (en Main)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DetallePresupuestoActivity,
                        "✅ Todos los mangas marcados como comprados",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish() // Cierra la actividad completamente
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DetallePresupuestoActivity,
                        "❌ Error: ${e.message ?: "Operación falló"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    private suspend fun agregarTodosLosMangasConReintentos(idUsuario: Int): Boolean {
        val resultados = mutableListOf<Boolean>()

        mangasBolsa.forEachIndexed { i, idManga ->
            var intentos = 0
            var exito = false

            while (intentos < 3 && !exito) { // Reintentar hasta 3 veces por manga
                try {
                    val precio = mangasPreciosLista[i]
                    AgregarManga(idManga, idUsuario, precio)
                    exito = true
                } catch (e: Exception) {
                    intentos++
                    delay(1000) // Espera 1 segundo entre reintentos
                }
            }
            resultados.add(exito)
        }

        return resultados.all { it } // True solo si TODOS fueron exitosos
    }

    private suspend fun AgregarManga(idManga: Int, idUsuario: Int, precioFinal: Float) {
        val request = AgregarMangaRequest(idManga, idUsuario, precioFinal)
        val response = retrofit.create(ApiServiceManga::class.java).agregarManga(request)

        if (!response.isSuccessful) {
            throw Exception("Error al agregar manga $idManga: HTTP ${response.code()}")
        }
    }

    private fun guardarEnMonetario(idUsuario: Int, totalAhorrado: Float, totalMangas: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = GuardarEnMonetarioRequest(idUsuario, totalAhorrado, totalMangas)
            try {
                val myResponse = retrofit.create(ApiServiceManga::class.java).guardarEnMonetario(request)
                if (myResponse.isSuccessful) {
                    // Si la respuesta es exitosa, no hace falta acceder al cuerpo
                    runOnUiThread {
                        Toast.makeText(this@DetallePresupuestoActivity, "Manga agregado correctamente", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Si la respuesta no es exitosa, manejar el error
                    runOnUiThread {
                        Toast.makeText(this@DetallePresupuestoActivity, "Error al agregar manga", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@DetallePresupuestoActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun BorrarPresupuesto(presupuestoId: Int, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = retrofit.create(ApiServiceManga::class.java)
                    .borrarPresupuesto(presupuestoId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {

                    } else {
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DetallePresupuestoActivity,
                        "Error de conexión: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
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
        binding.progressBar.visibility = View.GONE
        binding.mainContent.visibility = View.GONE
        binding.errorView.visibility = View.VISIBLE
        binding.errorText.text = message

        binding.retryButton.setOnClickListener {
            val idPresupuesto = intent.getIntExtra(DetalleMangaActivity.PLAN_ID, -1)
            if (idPresupuesto != -1) {
                cargarDatosIniciales(idPresupuesto)
            }
        }
    }
//    e.printStackTrace()
//    showErrorState("Error de conexión: ${e.message}")

    private fun cargarDatosIniciales(idPresupuesto: Int) {
        showLoadingState()

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    retrofit.create(ApiServiceManga::class.java)
                        .obtenerDetallePresupuesto(idPresupuesto)
                }
                if (response.isSuccessful) {
                    val resultado = response.body()?.resultado

                    resultado?.let {
                        // Procesar datos
                        mangasBolsa = it.listaMangasPresupuesto.map { manga -> manga.idManga }.toMutableList()
                        descuento = it.descuento
                        nombreBolsa = it.nombrePresupuesto

                        // Configurar UI con los datos obtenidos
                        withContext(Dispatchers.Main) {
                            binding.txtNombreBolsa.text = nombreBolsa
                            actualizarDescuentoSeleccionado(descuento)
                            setupUI()
                            mostrarMangasEnBolsa(mangasBolsa)
                            showContent()
                        }
                    } ?: run {
                        showErrorState("Datos del presupuesto no disponibles")
                    }
                } else {
                    showErrorState("Error al cargar: ${response.code()}")
                }
            } catch (e: Exception) {
                showErrorState("Error de conexión: ${e.message}")
            }
        }
    }

    private fun setupUIWithEmptyState() {
        binding.txtNombreBolsa.text = "Presupuesto no disponible"
        setupUI()
        showContent()
    }

    private fun setupUI() {
        // Configuración inicial del RecyclerView
        binding.rvRecibo.layoutManager = LinearLayoutManager(this)
        binding.rvRecibo.adapter = ReciboAdapterDetalle(emptyList(), 0f)

        // Configurar listeners y adapters
        initUI(intent.getIntExtra(DetalleMangaActivity.USER_ID, -1), mangasBolsa)

        binding.btnflecha.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


    }

    private fun actualizarDescuentoSeleccionado(descuentoObtenido: Float) {
        when (descuentoObtenido) {
            0.0f -> binding.radioGroupDescuentos.check(R.id.rbDescuento0)
            0.10f -> binding.radioGroupDescuentos.check(R.id.rbDescuento10)
            0.20f -> binding.radioGroupDescuentos.check(R.id.rbDescuento20)
            0.25f -> binding.radioGroupDescuentos.check(R.id.rbDescuento25)
            0.30f -> binding.radioGroupDescuentos.check(R.id.rbDescuento30)
            1.0f -> binding.radioGroupDescuentos.check(R.id.rbDescuento3x2)
            else -> {
                binding.radioGroupDescuentos.clearCheck()
                Log.w("Presupuesto", "Descuento no reconocido: $descuentoObtenido")
            }
        }
        Log.d("Presupuesto", "RadioGroup actualizado para descuento: $descuentoObtenido")
    }

    private fun actualizarPresupuesto(
        idPresupuesto: Int,
        nombreBolsa: String?,
        descuento: Float,
        mangasBolsa: MutableList<Int>,
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
        val actualizarBolsaRequest = ActualizarBolsaRequest(
            idPresupuesto = idPresupuesto,
            nombrePresupuesto = nombreBolsa,
            descuento = descuento,
            mangas_bolsa = mangasBolsa
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Realizar la solicitud POST, sin esperar respuesta
                retrofit.create(ApiServiceManga::class.java).botonActualizarPresupuesto(actualizarBolsaRequest)

                // Si llegamos aquí, la solicitud fue exitosa
                runOnUiThread {
                    // Realiza la acción después de que se haya guardado correctamente (por ejemplo, ir a la pantalla anterior)
                    Toast.makeText(this@DetallePresupuestoActivity, "Presupuesto guardado correctamente", Toast.LENGTH_SHORT).show()
                    finish() // Regresa a la pantalla anterior
                }

            } catch (e: Exception) {
                Log.e("Error", "Error al guardar presupuesto: ${e.message}")
            }
        }

    }

    private fun initUI(idUsuario: Int, mangasBolsa: List<Int>) {
        mangasPreciosLista.clear()
        adapter = PlanAdapterDetalle(
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
            mangasPreciosLista.clear()
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

    private fun eliminarManga(idManga: Int, precio: Float) {
        mangasBolsa.remove(idManga)
        mangasPreciosLista.clear()

        // Ajustar el precio total
        totalPrecio -= precio

        // Verificar si mangasBolsa está vacío o es nulo
        if (mangasBolsa.isNullOrEmpty()) {
            // Obtener el ID del presupuesto y del usuario
            val idPresupuesto = intent.getIntExtra(DetalleMangaActivity.PLAN_ID, -1)
            val idUsuario = intent.getIntExtra(DetalleMangaActivity.USER_ID, -1)

            if (idPresupuesto != -1 && idUsuario != -1) {
                // Llamar a la función para borrar el presupuesto
                showLoadingState()
                borrarPresupuestoYFinalizar(idPresupuesto, idUsuario)
            } else {
                // Si no tenemos los IDs necesarios, simplemente finalizar la actividad
                finish()
            }
        } else {
            // Si todavía hay mangas, actualizar la vista
            mostrarMangasEnBolsa(mangasBolsa)
        }
    }

    private fun borrarPresupuestoYFinalizar(presupuestoId: Int, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = retrofit.create(ApiServiceManga::class.java)
                    .borrarPresupuesto(presupuestoId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@DetallePresupuestoActivity,
                            "Presupuesto eliminado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@DetallePresupuestoActivity,
                            "Error al eliminar el presupuesto: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    // Finalizar la actividad en cualquier caso
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DetallePresupuestoActivity,
                        "Error de conexión: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Finalizar la actividad incluso si hay error
                    finish()
                }
            }
        }
    }

    private fun mostrarMangasEnBolsa(mangasBolsa: List<Int>) {
        CoroutineScope(Dispatchers.IO).launch {
            val requestData = AgregarPlanRequest(listaIds = mangasBolsa) // Enviar lista de IDs
            val myResponse = retrofit.create(ApiServiceManga::class.java).buscarMangasPost(requestData)

            if (myResponse.isSuccessful) {
                val response: AgregarPlanResponse? = myResponse.body()

                if (response != null) {
                    var totalSum = 0f
                    var subtotal = 0f
                    val mangasConDescuento = mutableListOf<Pair<PlanElementosItemResponse, Boolean>>()
                    val mangasRecibo = mutableListOf<PlanElementosItemResponse>()

                    // Lista para almacenar los descuentos de cada manga
                    val mangasPrecios = mutableListOf<Float>()

                    response.mangasPendientes.forEach { manga ->
                        subtotal += manga.precio
                        var descuentoManga = 0f
                        var precioConDescuento = manga.precio

                        // Si el descuento es 3x2, agregamos el manga con descuento del 100%
                        if (descuento == 1.0f) {
                            mangasConDescuento.add(manga to true)  // Manga con descuento
                        } else {
                            mangasConDescuento.add(manga to false)  // Manga sin descuento
                            precioConDescuento = manga.precio * (1 - descuento)
                            totalSum += precioConDescuento
                        }

                        // Calculamos el descuento para este manga
                        descuentoManga = manga.precio - precioConDescuento
                        mangasPrecios.add(descuentoManga)  // Guardamos el monto del descuento
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
                        mangasPrecios.clear()

// Restaurar el orden original según los IDs de response.mangasPendientes
                        val ordenOriginal = response.mangasPendientes.map { it.idManga }

// Ordenamos mangasConDescuento según el orden de IDs en response.mangasPendientes
                        val mangasConDescuentoOrdenados = mangasConDescuento.sortedBy { (manga, _) -> ordenOriginal.indexOf(manga.idManga) }

// Ahora recorremos la lista ordenada y agregamos los precios a mangasPrecios
                        mangasConDescuentoOrdenados.forEach { (manga, tieneDescuento) ->
                            if (tieneDescuento) {
                                mangasPrecios.add(manga.precio)
                            } else {
                                mangasPrecios.add(0f)
                            }
                        }


                    }

                    totalDescuento = subtotal - totalSum
                    totalPrecio = totalSum
                    var totalMangas = response.mangasPendientes.size

                    // Pasamos los descuentos de cada manga
                    runOnUiThread {
                        binding.txtSubTotal.text = "SubTotal: $${String.format("%.2f", subtotal)}"
                        binding.txtDescuento.text = "Descuento: $${String.format("%.2f", totalDescuento)}"
                        binding.txtTotal.text = "Total: $${String.format("%.2f", totalPrecio)}"
                        binding.txtTotalMangasBolsa.text = "Total: $totalMangas"
                        println(mangasConDescuento)
                        println(response.mangasPendientes)
                        adapter.updateList(response.mangasPendientes)

                        // Aseguramos de pasar los mangas y la información del descuento al adapter
                        val adapter = ReciboAdapterDetalle(mangasConDescuento, descuento)
                        val recyclerView: RecyclerView = findViewById(R.id.rvRecibo)
                        recyclerView.layoutManager = LinearLayoutManager(this@DetallePresupuestoActivity)
                        recyclerView.adapter = adapter

                        // Mostrar los descuentos de cada manga (esto es solo para ejemplo)
                        println("Descuentos de cada manga:")
                        mangasPreciosLista.clear()
                            mangasPreciosLista.addAll(mangasPrecios)

                        Log.i("BolsaActivity", "Mangas en bolsa actualizados correctamente.")
                        println(mangasBolsa)
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