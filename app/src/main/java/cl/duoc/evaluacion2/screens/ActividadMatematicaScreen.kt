
package cl.duoc.evaluacion2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class PreguntaMatematica(
    val pregunta: String,
    val imagen: String,
    val opciones: List<String>,
    val respuestaCorrecta: Int,
    val explicacion: String
)

private val preguntasMatematica = listOf(

    PreguntaMatematica(
        pregunta = "¿Cuántas manzanas hay?",
        imagen = "🍎 🍎 🍎",
        opciones = listOf("2", "3", "4"),
        respuestaCorrecta = 1,
        explicacion = "Hay tres manzanas."
    ),

    PreguntaMatematica(
        pregunta = "¿Cuánto es 2 + 2?",
        imagen = "⭐ ⭐ + ⭐ ⭐",
        opciones = listOf("3", "4", "5"),
        respuestaCorrecta = 1,
        explicacion = "Dos más dos es cuatro."
    ),

    PreguntaMatematica(
        pregunta = "¿Qué número viene después del 5?",
        imagen = "1  2  3  4  5  ❓",
        opciones = listOf("4", "7", "6"),
        respuestaCorrecta = 2,
        explicacion = "Después del cinco viene el seis."
    )
)

@Composable
fun ActividadMatematicaScreen(
    onFinalizar: (Int, Int) -> Unit,
    onVolver: () -> Unit
) {

    var indicePregunta by remember {
        mutableIntStateOf(0)
    }

    var respuestas by remember {
        mutableStateOf(listOf<Int>())
    }

    var finalizado by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "🔢 Matemática",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (!finalizado) {

            val pregunta = preguntasMatematica[
                indicePregunta
            ]

            var seleccion by remember(indicePregunta) {
                mutableIntStateOf(-1)
            }

            Text(
                text = "Pregunta ${indicePregunta + 1} de ${preguntasMatematica.size}"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = {
                    (indicePregunta + 1).toFloat() /
                            preguntasMatematica.size
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = pregunta.imagen,
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = pregunta.pregunta,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            pregunta.opciones.forEachIndexed {
                    indice, opcion ->

                OutlinedButton(
                    onClick = {
                        seleccion = indice
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor =
                            if (seleccion == indice)
                                MaterialTheme.colorScheme
                                    .primaryContainer
                            else
                                MaterialTheme.colorScheme
                                    .surface
                    )
                ) {

                    Text(
                        text = opcion,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                enabled = seleccion >= 0,
                onClick = {

                    val nuevasRespuestas =
                        respuestas + seleccion

                    respuestas = nuevasRespuestas

                    if (
                        indicePregunta ==
                        preguntasMatematica.lastIndex
                    ) {

                        val aciertos =
                            preguntasMatematica.indices.count {
                                    i ->
                                nuevasRespuestas[i] ==
                                        preguntasMatematica[i]
                                            .respuestaCorrecta
                            }

                        onFinalizar(
                            aciertos,
                            preguntasMatematica.size
                        )

                        finalizado = true

                    } else {
                        indicePregunta++
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    if (
                        indicePregunta ==
                        preguntasMatematica.lastIndex
                    ) {
                        "Finalizar actividad"
                    } else {
                        "Siguiente pregunta"
                    }
                )
            }

        } else {

            val aciertos =
                preguntasMatematica.indices.count {
                        i ->
                    respuestas[i] ==
                            preguntasMatematica[i]
                                .respuestaCorrecta
                }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (
                    aciertos == preguntasMatematica.size
                ) "🏆 ¡Excelente trabajo!"
                else "⭐ ¡Actividad completada!",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tu resultado: $aciertos de ${preguntasMatematica.size}",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Revisa tus respuestas",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            preguntasMatematica.forEachIndexed {
                    indice, pregunta ->

                val correcto =
                    respuestas[indice] ==
                            pregunta.respuestaCorrecta

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = if (correcto)
                                "✅ Respuesta correcta"
                            else
                                "❌ Respuesta incorrecta"
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(pregunta.pregunta)

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            "Tu respuesta: " +
                                    pregunta.opciones[
                                        respuestas[indice]
                                    ]
                        )

                        Text(
                            "Respuesta correcta: " +
                                    pregunta.opciones[
                                        pregunta.respuestaCorrecta
                                    ]
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(pregunta.explicacion)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver a mis asignaturas")
            }
        }
    }
}
