import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class StackedValueFormatter : ValueFormatter() {
    override fun getBarStackedLabel(value: Float, stackedEntry: BarEntry?): String {
        return "" // Ocultar valores apilados
    }
}