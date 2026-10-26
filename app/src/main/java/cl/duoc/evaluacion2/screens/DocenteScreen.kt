
package cl.duoc.evaluacion2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.evaluacion2.viewmodel.AsignacionDocente
import cl.duoc.evaluacion2.viewmodel.objetivosDocenteDemo

@Composable
fun DocenteScreen(
    asignaciones: Map<String, AsignacionDocente>,
    onGuardar: (String, String, Int) -> Boolean,
    onVolver: () -> Unit
) {

    var asignaturaId by rememberSaveable {
        mutableStateOf("matematica")
    }

    var fecha by rememberSaveable(asignaturaId) {
        mutableStateOf(
            asignaciones[asignaturaId]?.fecha ?: ""
        )
    }

    var intentos by rememberSaveable(asignaturaId) {
        mutableStateOf(
            asignaciones[asignaturaId]
                ?.maxIntentos?.toString() ?: "2"
        )
    }

    var mensaje by remember {
        mutableStateOf("")
    }

    var guardado by remember {
        mutableStateOf(false)
    }

    val objetivoSeleccionado =
        objetivosDocenteDemo.first {
            it.id == asignaturaId
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = "👩‍🏫 Panel Docente",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "MPAZ PRO 1.º - Primero Básico"
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Curso de prueba: 1.º Básico A",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Configurar actividad y prueba",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Selecciona una asignatura",
            style = MaterialTheme.typography.titleMedium
        )

        objetivosDocenteDemo.forEach { objetivo ->

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = asignaturaId == objetivo.id,
                    onClick = {
                        asignaturaId = objetivo.id
                        mensaje = ""
                    }
                )

                Text(
                    text = objetivo.asignatura,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Unidad de ejemplo",
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = objetivoSeleccionado.unidad,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Objetivo de aprendizaje de ejemplo",
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = objetivoSeleccionado.objetivo
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = fecha,
            onValueChange = {
                fecha = it
                mensaje = ""
            },
            label = {
                Text("Fecha de disponibilidad")
            },
            placeholder = {
                Text("AAAA-MM-DD")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = intentos,
            onValueChange = {
                intentos = it
                mensaje = ""
            },
            label = {
                Text("Máximo de intentos")
            },
            supportingText = {
                Text("Número entre 1 y 5")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {

                val numero = intentos.toIntOrNull()

                val correcto = numero != null &&
                        onGuardar(
                            asignaturaId,
                            fecha.trim(),
                            numero
                        )

                guardado = correcto

                mensaje = if (correcto) {
                    "Configuración guardada correctamente"
                } else {
                    "Revisa la fecha y los intentos. " +
                            "Usa una fecha actual o futura " +
                            "y entre 1 y 5 intentos."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar configuración")
        }

        if (mensaje.isNotEmpty()) {

            Spacer(Modifier.height(12.dp))

            Text(
                text = mensaje,
                color = if (guardado) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Configuraciones guardadas",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        if (asignaciones.isEmpty()) {

            Text("Todavía no hay actividades configuradas.")

        } else {

            objetivosDocenteDemo.forEach { objetivo ->

                val asignacion = asignaciones[objetivo.id]

                if (asignacion != null) {

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = objetivo.asignatura,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "Disponible desde: " +
                                        asignacion.fecha
                            )

                            Text(
                                text = "Máximo de intentos: " +
                                        asignacion.maxIntentos
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio")
        }
    }
}
