import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.principallectura.ItemMangaPendiente
import com.aristidevs.androidmaster.principallectura.MangasPendientesHolder

class MangasPendientesAdapter(
    private var detalles: List<ItemMangaPendiente> = emptyList(),
) : RecyclerView.Adapter<MangasPendientesHolder>() {

    private val selectedIds = mutableListOf<Int>() // Almacena los IDs seleccionados

    fun updateList(newList: List<ItemMangaPendiente>) {
        detalles = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangasPendientesHolder {
        return MangasPendientesHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_manga_pendiente, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MangasPendientesHolder, position: Int) {
        holder.bind(detalles[position])
    }

    override fun getItemCount() = detalles.size
}






