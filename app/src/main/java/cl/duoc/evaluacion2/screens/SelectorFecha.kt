package cl.duoc.evaluacion2.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Muestra DD/MM/AAAA y entrega AAAA-MM-DD al ViewModel.
@Composable
fun SelectorFecha(
    fecha: String,
    onFechaSeleccionada: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendario = Calendar.getInstance()
    val formatoInterno = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        isLenient = false
    }
    val fechaGuardada = runCatching {
        formatoInterno.parse(fecha)
    }.getOrNull()
    if (fechaGuardada != null) calendario.time = fechaGuardada

    val fechaLegible = if (fechaGuardada == null) {
        "Seleccionar fecha"
    } else {
        SimpleDateFormat("dd/MM/yyyy", Locale("es", "CL"))
            .format(fechaGuardada)
    }

    OutlinedButton(
        onClick = {
            val dialogo = DatePickerDialog(
                context,
                { _, anio, mes, dia ->
                    onFechaSeleccionada(
                        String.format(Locale.US, "%04d-%02d-%02d", anio, mes + 1, dia)
                    )
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            )
            val hoy = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            dialogo.datePicker.minDate = hoy.timeInMillis
            dialogo.show()
        },
        modifier = modifier.fillMaxWidth()
    ) {
        Text("📅 $fechaLegible")
    }
}