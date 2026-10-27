
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
    onAbrirInforme: () -> Unit,
    onVolver: () -> Unit
) {

    // ASIGNATURA SELECCIONADA
    var asignaturaId by rememberSaveable {
        mutableStateOf("matematica")
    }

    // FECHA DE DISPONIBILIDAD
    var fecha by rememberSaveable(asignaturaId) {
        mutableStateOf(
            asignaciones[asignaturaId]?.fecha ?: ""
        )
    }

    // MAXIMO DE INTENTOS
    var intentos by rememberSaveable(asignaturaId) {
        mutableStateOf(
            asignaciones[asignaturaId]
                ?.maxIntentos?.toString() ?: "2"
        )
    }

    // MENSAJES
    var mensaje by remember {
        mutableStateOf("")
    }

    var guardado by remember {
        mutableStateOf(false)
    }

    // AVISO SOLICITADO POR EL PROFESOR
    var mostrarAviso by remember {
        mutableStateOf(false)
    }

    // OBJETIVO DE APRENDIZAJE SELECCIONADO
    val objetivoSeleccionado =
        objetivosDocenteDemo.first {
            it.id == asignaturaId
        }

    // MOSTRAR VENTANA INFORMATIVA
    if (mostrarAviso) {

        AvisoFuturaActualizacion(
            onCerrar = {
                mostrarAviso = false
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

        // ENCABEZADO
        Text(
            text = "👩‍🏫 Panel Docente",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "MPAZ PRO 1.º - Primero Básico"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Curso de prueba: 1.º Básico A",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CONFIGURACION DE ACTIVIDADES
        Text(
            text = "Configurar actividad y prueba",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Selecciona una asignatura",
            style = MaterialTheme.typography.titleMedium
        )

        // ASIGNATURAS
        objetivosDocenteDemo.forEach { objetivo ->

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = asignaturaId == objetivo.id,
                    onClick = {
                        asignaturaId = objetivo.id
                        mensaje = ""
                        guardado = false
                    }
                )

                Text(
                    text = objetivo.asignatura,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // UNIDAD Y OBJETIVO DE APRENDIZAJE
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

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = objetivoSeleccionado.unidad,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Objetivo de aprendizaje de ejemplo",
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = objetivoSeleccionado.objetivo
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // FORMULARIO DE FECHA
        OutlinedTextField(
            value = fecha,
            onValueChange = {
                fecha = it
                mensaje = ""
                guardado = false
            },
            label = {
                Text("Fecha de disponibilidad")
            },
            placeholder = {
                Text("AAAA-MM-DD")
            },
            supportingText = {
                Text("Ejemplo: 2026-11-10")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // FORMULARIO DE INTENTOS
        OutlinedTextField(
            value = intentos,
            onValueChange = {
                intentos = it
                mensaje = ""
                guardado = false
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

        Spacer(modifier = Modifier.height(20.dp))

        // GUARDAR CONFIGURACION
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

                if (correcto) {

                    mensaje =
                        "Configuración guardada correctamente"

                    // Mostrar el aviso de futura actualizacion
                    mostrarAviso = true

                } else {

                    mensaje =
                        "Revisa la fecha y los intentos. " +
                                "Usa una fecha actual o futura " +
                                "y entre 1 y 5 intentos."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Guardar configuración")
        }

        // RESULTADO DEL FORMULARIO
        if (mensaje.isNotEmpty()) {

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = mensaje,
                color = if (guardado) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(20.dp))

        // CONFIGURACIONES GUARDADAS
        Text(
            text = "Configuraciones guardadas",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (asignaciones.isEmpty()) {

            Text(
                text = "Todavía no hay actividades configuradas."
            )

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

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Disponible desde: " +
                                        asignacion.fecha
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Máximo de intentos: " +
                                        asignacion.maxIntentos
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(20.dp))

        // NUEVA SECCION: INFORME PEDAGOGICO
        Text(
            text = "📊 Informes pedagógicos",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Consulta una vista de demostración " +
                    "del informe pedagógico de un estudiante.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // BOTON PARA ABRIR INFORME
        Button(
            onClick = onAbrirInforme,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("📊 Ver informe pedagógico")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Los resultados individuales y la " +
                    "validación definitiva del informe " +
                    "se incorporarán en una futura actualización.",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(28.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(20.dp))

        // BOTON VOLVER AL INICIO
        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Volver al inicio")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
