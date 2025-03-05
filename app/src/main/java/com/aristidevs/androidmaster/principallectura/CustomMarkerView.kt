import android.content.Context
import android.widget.TextView
import com.aristidevs.androidmaster.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class CustomMarkerView(context: Context, layoutResource: Int, private val months: List<String>) : MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    override fun refreshContent(entry: Entry, highlight: Highlight) {
        // Obtener el índice del mes redondeando entry.x
        val x = Math.round(entry.x) // Índice del mes (sin desplazamiento)
        val y = entry.y.toInt() // Valor de la barra (convertido a entero)

        // Determinar si es "Comprados" o "Leídos" usando highlight.dataSetIndex
        val label = if (highlight.dataSetIndex == 0) "Comprados" else "Leídos"

        // Mostrar la información
        tvContent.text = "${months[x]}\n$label: $y"

        super.refreshContent(entry, highlight)
    }

    override fun getOffset(): MPPointF {
        // Centrar el MarkerView sobre la barra seleccionada
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}