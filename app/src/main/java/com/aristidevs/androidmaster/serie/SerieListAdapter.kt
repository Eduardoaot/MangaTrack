package com.aristidevs.androidmaster.serie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.serie.SerieListHolder
import com.aristidevs.androidmaster.serie.SerieListaItemResponse
import com.google.firebase.firestore.FirebaseFirestore

class SerieListAdapter(var serieList: List<SerieListaItemResponse> = emptyList()) :
    RecyclerView.Adapter<SerieListHolder>() {

    private val db = FirebaseFirestore.getInstance()

    fun fetchDataFromFirebase(userId: String) {
        db.collection("usuarios")
            .document(userId)
            .collection("series")  // Aquí cambio de "mangas" a "series"
            .get()
            .addOnSuccessListener { result ->
                serieList = result.map { document ->
                    // Mapea los datos del documento
                    val serieNom = document.getString("serieNom") ?: ""
                    val seriePorcentaje = document.getDouble("seriePorcentaje") ?: 0.0
                    val serieEstado = document.getString("serieEstado") ?: ""
                    val serieImagen = document.getString("serieImagen") ?: ""
                    val serieId = document.getString("serieId") ?: ""
                    SerieListaItemResponse(serieNom, seriePorcentaje, serieEstado, serieImagen, serieId)
                }
                notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Manejo de errores
                println("Error al obtener los datos: $exception")
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerieListHolder {
        return SerieListHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_serielist, parent, false)
        )
    }

    override fun onBindViewHolder(viewholder: SerieListHolder, position: Int) {
        val item = serieList[position]
        viewholder.bind(item)
    }

    override fun getItemCount() = serieList.size
}