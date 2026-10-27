
package cl.duoc.evaluacion2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class PreguntaFinalDemo(
    val texto: String,
    val opciones: List<String>,
    val respuestaCorrecta: Int
)

private data class PruebaDemo(
    val titulo: String,
    val preguntas: List<PreguntaFinalDemo>
)

private fun obtenerPruebaDemo(id: String): PruebaDemo? {

    return when (id) {

        "matematica" -> PruebaDemo(
            "Matemática",
            listOf(
                PreguntaFinalDemo(
                    "¿Cuánto es 4 + 1?",
                    listOf("3", "5", "6"),
                    1
                ),
                PreguntaFinalDemo(
                    "¿Qué número viene antes del 7?",
                    listOf("8", "5", "6"),
                    2
                )
            )
        )

        "lenguaje" -> PruebaDemo(
            "Lenguaje y Comunicación",
            listOf(
                PreguntaFinalDemo(
                    "¿Qué palabra comienza con M?",
                    listOf("Mesa", "Sol", "Pato"),
                    0
                ),
                PreguntaFinalDemo(
                    "¿Cuál de estas letras es una vocal?",
                    listOf("P", "E", "T"),
                    1
                )
            )
        )

        "ciencias" -> PruebaDemo(
            "Ciencias Naturales",
            listOf(
                PreguntaFinalDemo(
                    "¿Qué necesita una planta para crecer?",
                    listOf("Zapatos", "Agua", "Juguetes"),
                    1
                ),
                PreguntaFinalDemo(
                    "¿Cuál es un ser vivo?",
                    listOf("Piedra", "Silla", "Perro"),
                    2
                )
            )
        )

        "historia" -> PruebaDemo(
            "Historia y Geografía",
            listOf(
                PreguntaFinalDemo(
                    "¿Dónde podemos jugar en nuestra comunidad?",
                    listOf("Plaza", "Nube", "Luna"),
                    0
                ),
                PreguntaFinalDemo(
                    "¿Qué utilizamos para saber qué día es?",
                    listOf("Cuchara", "Calendario", "Zapato"),
                    1
                )
            )
        )

        else -> null
    }
}

@Composable
fun PruebaFinalScreen(
    asignaturaId: String,
    maxIntentos: Int,
    onVolver: () -> Unit
) {

    val prueba = remember(asignaturaId) {
        obtenerPruebaDemo(asignaturaId)
    }

    if (prueba == null) {
        PantallaPruebaBloqueada(onVolver)
        return
    }

    var respuestas by remember(asignaturaId) {
        mutableStateOf<Map<Int, Int>>(emptyMap())
    }

    var finalizada by remember(asignaturaId) {
        mutableStateOf(false)
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
                    "La prueba final funciona como demostración. " +
                            "Sus intentos y calificaciones todavía no " +
                            "se registran en la base de datos académica."
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
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = "📝 Prueba Final",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = prueba.titulo,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Simulación de evaluación final",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Intentos máximos configurados: $maxIntentos"
        )

        Text(
            text = "Esta demostración no descuenta intentos.",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(24.dp))

        if (!finalizada) {

            prueba.preguntas.forEachIndexed { indice, pregunta ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "Pregunta ${indice + 1}",
                            style = MaterialTheme.typography.labelLarge
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = pregunta.texto,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.height(12.dp))

                        pregunta.opciones.forEachIndexed {
                                indiceOpcion, opcion ->

                            Row(
                                verticalAlignment =
                                    androidx.compose.ui.Alignment.CenterVertically
                            ) {

                                RadioButton(
                                    selected =
                                        respuestas[indice] == indiceOpcion,
                                    onClick = {
                                        respuestas = respuestas + (
                                                indice to indiceOpcion
                                                )
                                    }
                                )

                                Text(text = opcion)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    finalizada = true
                    mostrarAviso = true
                },
                enabled =
                    respuestas.size == prueba.preguntas.size,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Finalizar simulación")
            }

        } else {

            val aciertos = prueba.preguntas.indices.count { i ->
                respuestas[i] ==
                        prueba.preguntas[i].respuestaCorrecta
            }

            Text(
                text = "🏆 Resultado de demostración",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "$aciertos de ${prueba.preguntas.size} respuestas correctas",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(20.dp))

            prueba.preguntas.forEachIndexed { indice, pregunta ->

                val respuestaElegida = respuestas[indice]

                val correcta =
                    respuestaElegida == pregunta.respuestaCorrecta

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = if (correcta) {
                                "✅ Correcta"
                            } else {
                                "❌ Incorrecta"
                            }
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(pregunta.texto)

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Tu respuesta: " +
                                    pregunta.opciones[
                                        respuestaElegida ?: 0
                                    ]
                        )

                        Text(
                            "Respuesta correcta: " +
                                    pregunta.opciones[
                                        pregunta.respuestaCorrecta
                                    ]
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    respuestas = emptyMap()
                    finalizada = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Repetir simulación")
            }
        }

        Spacer(Modifier.height(20.dp))

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a mis asignaturas")
        }
    }
}

@Composable
fun PantallaPruebaBloqueada(
    onVolver: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "🔒 Prueba final no disponible",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "Primero debes tener una actividad habilitada " +
                    "por el docente y completar su práctica."
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
