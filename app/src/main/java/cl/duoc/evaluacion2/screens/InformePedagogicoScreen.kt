
package cl.duoc.evaluacion2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InformePedagogicoScreen(
    onVolver: () -> Unit
) {

    val asignaturas = listOf(
        "Lenguaje y Comunicación",
        "Matemática",
        "Ciencias Naturales",
        "Historia y Geografía"
    )

    var nombreEstudiante by rememberSaveable {
        mutableStateOf("Estudiante ficticio")
    }

    var asignatura by rememberSaveable {
        mutableStateOf("Matemática")
    }

    var observacion by rememberSaveable {
        mutableStateOf("")
    }

    var mostrarAviso by remember {
        mutableStateOf(false)
    }

    if (mostrarAviso) {

        AlertDialog(
            onDismissRequest = {
                mostrarAviso = false
            },
            title = {
                Text("Para una futura actualización")
            },
            text = {
                Text(
                    "Esta pantalla es una demostración. " +
                            "La validación y el almacenamiento de " +
                            "informes pedagógicos se implementarán " +
                            "cuando se conecten los resultados " +
                            "individuales de los estudiantes."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarAviso = false
                    }
                ) {
                    Text("Entendido")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = "📊 Informe Pedagógico",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Panel exclusivo para Docente",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Vista previa de informe",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = nombreEstudiante,
            onValueChange = {
                nombreEstudiante = it
            },
            label = {
                Text("Estudiante ficticio")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Asignatura",
            style = MaterialTheme.typography.titleMedium
        )

        asignaturas.forEach { nombre ->

            Row(
                verticalAlignment =
                    androidx.compose.ui.Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = asignatura == nombre,
                    onClick = {
                        asignatura = nombre
                    }
                )

                Text(nombre)
            }
        }

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Resultados de aprendizaje",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Asignatura: $asignatura"
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Rendimiento por OA: pendiente de integración."
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Fortalezas y aspectos por reforzar: " +
                            "se calcularán con reglas basadas " +
                            "en resultados individuales."
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = observacion,
            onValueChange = {
                observacion = it
            },
            label = {
                Text("Observación pedagógica del docente")
            },
            placeholder = {
                Text(
                    "Escribe una observación ficticia"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Los informes deben ser revisados " +
                    "y validados por el docente.",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                mostrarAviso = true
            },
            enabled =
                nombreEstudiante.isNotBlank() &&
                        observacion.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Probar validación del informe")
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al Panel Docente")
        }
    }
}
