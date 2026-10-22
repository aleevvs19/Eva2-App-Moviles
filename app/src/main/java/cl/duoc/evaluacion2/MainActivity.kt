
package cl.duoc.evaluacion2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.evaluacion2.navigation.AppNavigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun PantallaInicio(
    onPerfilSeleccionado: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "MPAZ PRO 1.º",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Escuela Escritora Marcela Paz"
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Selecciona tu perfil",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                onPerfilSeleccionado("estudiante")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Estudiante")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                onPerfilSeleccionado("docente")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Docente")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                onPerfilSeleccionado("utp")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Jefatura UTP")
        }
    }
}
