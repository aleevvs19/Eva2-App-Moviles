
package cl.duoc.evaluacion2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class Pregunta(
    val texto: String,
    val imagen: String,
    val opciones: List<String>,
    val correcta: Int,
    val explicacion: String
)

private data class Actividad(
    val titulo: String,
    val preguntas: List<Pregunta>
)

private fun obtenerActividad(id: String): Actividad? {

    return when (id) {

        "lenguaje" -> Actividad(
            titulo = "📖 Lenguaje y Comunicación",
            preguntas = listOf(
                Pregunta(
                    "¿Cuál de estas letras es una vocal?",
                    "🔤",
                    listOf("M", "A", "P"),
                    1,
                    "La letra A es una vocal."
                ),
                Pregunta(
                    "¿Con qué vocal empieza OSO?",
                    "🐻 OSO",
                    listOf("E", "O", "I"),
                    1,
                    "La palabra OSO comienza con O."
                ),
                Pregunta(
                    "¿Qué palabra comienza con M?",
                    "🔤 M",
                    listOf("Sol", "Mesa", "Pato"),
                    1,
                    "Mesa comienza con la letra M."
                )
            )
        )

        "ciencias" -> Actividad(
            titulo = "🌱 Ciencias Naturales",
            preguntas = listOf(
                Pregunta(
                    "¿Cuál de estos es un ser vivo?",
                    "🌳",
                    listOf("Piedra", "Árbol", "Mesa"),
                    1,
                    "El árbol es un ser vivo."
                ),
                Pregunta(
                    "¿Qué necesitan las plantas para crecer?",
                    "🌻",
                    listOf(
                        "Agua y luz",
                        "Juguetes",
                        "Zapatos"
                    ),
                    0,
                    "Las plantas necesitan agua y luz."
                ),
                Pregunta(
                    "¿Qué animal pone huevos?",
                    "🐔",
                    listOf("Perro", "Gato", "Gallina"),
                    2,
                    "La gallina pone huevos."
                )
            )
        )

        "historia" -> Actividad(
            titulo = "🌎 Historia y Geografía",
            preguntas = listOf(
                Pregunta(
                    "¿Quién puede formar parte de una familia?",
                    "👨‍👩‍👧",
                    listOf(
                        "Un semáforo",
                        "Una abuela",
                        "Una pelota"
                    ),
                    1,
                    "Una abuela puede formar parte de una familia."
                ),
                Pregunta(
                    "¿Qué utilizamos para saber qué día es?",
                    "📅",
                    listOf(
                        "Calendario",
                        "Cuchara",
                        "Zapato"
                    ),
                    0,
                    "El calendario nos ayuda a conocer los días."
                ),
                Pregunta(
                    "¿Cuál de estos lugares puede estar en nuestra comunidad?",
                    "🏘️",
                    listOf(
                        "La Luna",
                        "Una plaza",
                        "Una nube"
                    ),
                    1,
                    "La plaza es un lugar de la comunidad."
                )
            )
        )

        else -> null
    }
}

@Composable
fun ActividadOtraAsignaturaScreen(
    asignaturaId: String,
    onFinalizar: (String, Int, Int) -> Unit,
    onVolver: () -> Unit
) {

    val actividad = remember(asignaturaId) {
        obtenerActividad(asignaturaId)
    }

    if (actividad == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Asignatura no disponible")

            Spacer(Modifier.height(16.dp))

            Button(onClick = onVolver) {
                Text("Volver")
            }
        }
        return
    }

    var indice by remember(asignaturaId) {
        mutableIntStateOf(0)
    }

    var respuestas by remember(asignaturaId) {
        mutableStateOf<List<Int>>(emptyList())
    }

    var finalizada by remember(asignaturaId) {
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
            text = actividad.titulo,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(24.dp))

        if (!finalizada) {

            val pregunta = actividad.preguntas[indice]

            var seleccion by remember(
                asignaturaId,
                indice
            ) {
                mutableIntStateOf(-1)
            }

            Text(
                "Pregunta ${indice + 1} de ${actividad.preguntas.size}"
            )

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = {
                    (indice + 1).toFloat() /
                            actividad.preguntas.size
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(30.dp))

            Text(
                text = pregunta.imagen,
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = pregunta.texto,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(24.dp))

            pregunta.opciones.forEachIndexed { i, opcion ->

                OutlinedButton(
                    onClick = { seleccion = i },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor =
                            if (seleccion == i)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = opcion,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(20.dp))

            Button(
                enabled = seleccion >= 0,
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    val nuevasRespuestas =
                        respuestas + seleccion

                    respuestas = nuevasRespuestas

                    if (indice == actividad.preguntas.lastIndex) {

                        val aciertos =
                            actividad.preguntas.indices.count { i ->
                                nuevasRespuestas[i] ==
                                        actividad.preguntas[i].correcta
                            }

                        onFinalizar(
                            asignaturaId,
                            aciertos,
                            actividad.preguntas.size
                        )

                        finalizada = true

                    } else {
                        indice++
                    }
                }
            ) {
                Text(
                    if (indice == actividad.preguntas.lastIndex)
                        "Finalizar actividad"
                    else
                        "Siguiente pregunta"
                )
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al menú")
            }

        } else {

            val aciertos =
                actividad.preguntas.indices.count { i ->
                    respuestas.getOrNull(i) ==
                            actividad.preguntas[i].correcta
                }

            Text(
                text = "🏆 ¡Actividad completada!",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Resultado: $aciertos de ${actividad.preguntas.size}",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(24.dp))

            actividad.preguntas.forEachIndexed { i, pregunta ->

                val seleccionada = respuestas[i]
                val correcta = seleccionada == pregunta.correcta

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            if (correcta)
                                "✅ Respuesta correcta"
                            else
                                "❌ Respuesta incorrecta"
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(pregunta.texto)

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Tu respuesta: " +
                                    pregunta.opciones[seleccionada]
                        )

                        Text(
                            "Respuesta correcta: " +
                                    pregunta.opciones[pregunta.correcta]
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(pregunta.explicacion)
                    }
                }

                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver a mis asignaturas")
            }
        }
    }
}
