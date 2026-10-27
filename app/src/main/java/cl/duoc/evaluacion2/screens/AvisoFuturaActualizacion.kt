package cl.duoc.evaluacion2.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AvisoFuturaActualizacion(
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            Text("Para una futura actualización")
        },
        text = {
            Text(
                "Esta función se encuentra en modo demostración. " +
                        "Los datos se guardan localmente. " +
                        "La conexión con la base de datos académica " +
                        "se incorporará en una futura actualización."
            )
        },
        confirmButton = {
            TextButton(onClick = onCerrar) {
                Text("Entendido")
            }
        }
    )
}
