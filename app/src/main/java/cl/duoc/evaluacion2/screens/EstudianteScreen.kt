
package cl.duoc.evaluacion2.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import cl.duoc.evaluacion2.viewmodel.AsignacionDocente
import cl.duoc.evaluacion2.viewmodel.ResultadoAsignatura
import cl.duoc.evaluacion2.viewmodel.ResultadoMatematica

import java.time.LocalDate

data class Asignatura(
    val id: String,
    val nombre: String,
    val emoji: String,
    val descripcion: String
)

// ASIGNATURAS DE PRIMERO BASICO
private val asignaturas = listOf(

    Asignatura(
        id = "lenguaje",
        nombre = "Lenguaje y Comunicación",
        emoji = "📖",
        descripcion = "Vocales y palabras"
    ),

    Asignatura(
        id = "matematica",
        nombre = "Matemática",
        emoji = "🔢",
        descripcion = "Números y conteo"
    ),

    Asignatura(
        id = "ciencias",
        nombre = "Ciencias Naturales",
        emoji = "🌱",
        descripcion = "Seres vivos y naturaleza"
    ),

    Asignatura(
        id = "historia",
        nombre = "Historia, Geografía y Ciencias Sociales",
        emoji = "🌎",
        descripcion = "Familia y comunidad"
    )
)

// COMPROBAR DISPONIBILIDAD SEGUN EL DOCENTE
fun estaAsignaturaDisponible(
    asignacion: AsignacionDocente?
): Boolean {

    if (asignacion == null) {
        return false
    }

    return try {

        val fechaDisponible =
            LocalDate.parse(asignacion.fecha)

        !fechaDisponible.isAfter(
            LocalDate.now()
        )

    } catch (e: Exception) {

        false
    }
}

// PANTALLA PRINCIPAL DEL ESTUDIANTE
@Composable
fun EstudianteScreen(
    resultadoMatematica: ResultadoMatematica,
    resultadosPorAsignatura: Map<String, ResultadoAsignatura>,
    asignaciones: Map<String, AsignacionDocente>,
    onAbrirAsignatura: (String) -> Unit,
    onAbrirPruebaFinal: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {

    // TOTAL DE ASIGNATURAS HABILITADAS
    val habilitadas = asignaturas.count { asignatura ->

        estaAsignaturaDisponible(
            asignaciones[asignatura.id]
        )
    }

    // COMPROBAR SI UNA PRACTICA ESTA COMPLETADA
    fun practicaCompletada(id: String): Boolean {

        return if (id == "matematica") {

            resultadoMatematica.completada

        } else {

            resultadosPorAsignatura[
                id
            ]?.completada == true
        }
    }

    // TOTAL DE ACTIVIDADES COMPLETADAS Y HABILITADAS
    val completadas = asignaturas.count { asignatura ->

        val disponible = estaAsignaturaDisponible(
            asignaciones[asignatura.id]
        )

        disponible && practicaCompletada(
            asignatura.id
        )
    }

    // PRUEBAS FINALES DESBLOQUEADAS
    val pruebasDisponibles = asignaturas.filter { asignatura ->

        val disponible = estaAsignaturaDisponible(
            asignaciones[asignatura.id]
        )

        disponible && practicaCompletada(
            asignatura.id
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        // ENCABEZADO
        Text(
            text = "¡Hola, estudiante! 👋",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bienvenido a MPAZ PRO 1.º"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // SECCION ASIGNATURAS
        Text(
            text = "Mis asignaturas",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Tu profesor habilitará las actividades " +
                    "que puedes realizar.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TARJETAS DE LAS CUATRO ASIGNATURAS
        asignaturas.forEach { asignatura ->

            val asignacion = asignaciones[
                asignatura.id
            ]

            val disponible = estaAsignaturaDisponible(
                asignacion
            )

            val estado = when {

                asignacion == null ->
                    "🔒 Pendiente de asignación"

                !disponible ->
                    "⏳ Disponible desde: ${asignacion.fecha}"

                else ->
                    "✅ Actividad disponible"
            }

            // MOSTRAR EL ULTIMO RESULTADO
            val resultado = if (
                asignatura.id == "matematica"
            ) {

                if (resultadoMatematica.completada) {

                    "${resultadoMatematica.aciertos}/" +
                            "${resultadoMatematica.total}"

                } else {

                    null
                }

            } else {

                resultadosPorAsignatura[
                    asignatura.id
                ]?.takeIf {

                    it.completada

                }?.let {

                    "${it.aciertos}/${it.total}"
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        enabled = disponible
                    ) {

                        onAbrirAsignatura(
                            asignatura.id
                        )
                    }
            ) {

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = asignatura.emoji,
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = asignatura.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = asignatura.descripcion,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = estado,
                            color = if (disponible) {

                                MaterialTheme.colorScheme.primary

                            } else {

                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )

                        // RESULTADO DE LA PRACTICA
                        if (disponible && resultado != null) {

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Último resultado: $resultado"
                            )
                        }

                        // CONFIGURACION DEL DOCENTE
                        if (disponible && asignacion != null) {

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Intentos configurados " +
                                        "para prueba final: " +
                                        asignacion.maxIntentos,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = if (disponible) {
                            "›"
                        } else {
                            "🔒"
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECCION PROGRESO
        Text(
            text = "Mi progreso",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        val progreso = if (habilitadas > 0) {

            completadas.toFloat() / habilitadas

        } else {

            0f
        }

        LinearProgressIndicator(
            progress = {
                progreso
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$completadas de $habilitadas " +
                    "actividades disponibles completadas"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "El progreso corresponde a las " +
                    "actividades de demostración.",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(28.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(20.dp))

        // NUEVA SECCION: PRUEBAS FINALES
        Text(
            text = "📝 Pruebas finales",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Completa primero la actividad de una " +
                    "asignatura para desbloquear su prueba final.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (pruebasDisponibles.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "🔒 Pruebas bloqueadas",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Todavía no tienes pruebas finales " +
                                "disponibles. Completa una actividad " +
                                "habilitada por tu profesor."
                    )
                }
            }

        } else {

            pruebasDisponibles.forEach { asignatura ->

                OutlinedButton(
                    onClick = {

                        onAbrirPruebaFinal(
                            asignatura.id
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "${asignatura.emoji} " +
                                "Prueba final: ${asignatura.nombre}"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Las pruebas finales funcionan " +
                        "en modo demostración. Los intentos " +
                        "reales se implementarán en una " +
                        "futura actualización.",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(20.dp))

        // CERRAR SESION
        OutlinedButton(
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Cerrar sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// PANTALLA AUXILIAR DE ASIGNATURA
@Composable
fun PantallaAsignaturaScreen(
    id: String,
    onVolver: () -> Unit
) {

    val asignatura = asignaturas.find {
        it.id == id
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = asignatura?.emoji ?: "📚",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = asignatura?.nombre ?: "Asignatura",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Contenido no disponible todavía."
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVolver
        ) {

            Text("Volver")
        }
    }
}
