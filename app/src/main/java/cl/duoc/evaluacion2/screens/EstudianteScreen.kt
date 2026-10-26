
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

private val asignaturas = listOf(

    Asignatura(
        "lenguaje",
        "Lenguaje y Comunicación",
        "📖",
        "Vocales y palabras"
    ),

    Asignatura(
        "matematica",
        "Matemática",
        "🔢",
        "Números y conteo"
    ),

    Asignatura(
        "ciencias",
        "Ciencias Naturales",
        "🌱",
        "Seres vivos y naturaleza"
    ),

    Asignatura(
        "historia",
        "Historia, Geografía y Ciencias Sociales",
        "🌎",
        "Familia y comunidad"
    )
)

// Comprueba si existe una asignacion
// y si ya llego su fecha de disponibilidad.
fun estaAsignaturaDisponible(
    asignacion: AsignacionDocente?
): Boolean {

    if (asignacion == null) {
        return false
    }

    return try {

        val fechaDisponible =
            LocalDate.parse(asignacion.fecha)

        !fechaDisponible.isAfter(LocalDate.now())

    } catch (e: Exception) {
        false
    }
}

@Composable
fun EstudianteScreen(
    resultadoMatematica: ResultadoMatematica,
    resultadosPorAsignatura: Map<String, ResultadoAsignatura>,
    asignaciones: Map<String, AsignacionDocente>,
    onAbrirAsignatura: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {

    val habilitadas = asignaturas.count {
        estaAsignaturaDisponible(
            asignaciones[it.id]
        )
    }

    val completadas = asignaturas.count { asignatura ->

        val disponible = estaAsignaturaDisponible(
            asignaciones[asignatura.id]
        )

        if (!disponible) {
            false
        } else if (asignatura.id == "matematica") {
            resultadoMatematica.completada
        } else {
            resultadosPorAsignatura[
                asignatura.id
            ]?.completada == true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = "¡Hola, estudiante! 👋",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text("Bienvenido a MPAZ PRO 1.º")

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Mis asignaturas",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Tu profesor habilitará las actividades que puedes realizar.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))

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

                    Spacer(Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = asignatura.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = asignatura.descripcion,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = estado,
                            color = if (disponible) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )

                        if (disponible && resultado != null) {

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "Último resultado: $resultado"
                            )
                        }

                        if (disponible && asignacion != null) {

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "Intentos permitidos en " +
                                        "prueba final: " +
                                        asignacion.maxIntentos,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Text(
                        if (disponible) "›" else "🔒"
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Mi progreso",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        val progreso = if (habilitadas > 0) {
            completadas.toFloat() / habilitadas
        } else {
            0f
        }

        LinearProgressIndicator(
            progress = { progreso },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "$completadas de $habilitadas " +
                    "actividades disponibles completadas"
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "El progreso corresponde a las " +
                    "actividades de demostración.",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}

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
            text = asignatura?.nombre ?: "Asignatura",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        Text("Contenido no disponible todavía")

        Spacer(Modifier.height(24.dp))

        Button(onClick = onVolver) {
            Text("Volver")
        }
    }
}
