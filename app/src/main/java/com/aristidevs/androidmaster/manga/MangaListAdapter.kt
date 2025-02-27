package com.aristidevs.androidmaster.manga

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.google.firebase.firestore.FirebaseFirestore

class MangaListAdapter(var mangaList: List<MangaListaItemResponse> = emptyList()) :
    RecyclerView.Adapter<MangaListHolder>() {

    private val db = FirebaseFirestore.getInstance()

    fun fetchDataFromFirebase(userId: String) {
        db.collection("usuarios")
            .document(userId)
            .collection("mangas")
            .get()
            .addOnSuccessListener { result ->
                mangaList = result.map { document ->
                    // No necesitas Gson, simplemente mapea los datos del documento
                    val mangaNum = document.getDouble("mangaNum") ?: 0.0
                    val mangaImg = document.getString("mangaImg") ?: ""
                    val serieNom = document.getString("serieNom") ?: ""
                    MangaListaItemResponse(mangaNum, mangaImg, serieNom)
                }
                notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Manejo de errores
                println("Error al obtener los datos: $exception")
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaListHolder {
        return MangaListHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_mangalist, parent, false)
        )
    }

    override fun onBindViewHolder(viewholder: MangaListHolder, position: Int) {
        val item = mangaList[position]
        viewholder.bind(item)
    }

    override fun getItemCount() = mangaList.size
}
