
package cl.duoc.evaluacion2.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.time.LocalDate

data class ObjetivoDocenteDemo(
    val id: String,
    val asignatura: String,
    val unidad: String,
    val objetivo: String
)

// Contenidos demostrativos.
// Posteriormente se reemplazaran por OA oficiales
// y unidades aprobadas por UTP.
val objetivosDocenteDemo = listOf(
    ObjetivoDocenteDemo(
        "lenguaje",
        "Lenguaje y Comunicación",
        "Vocales y palabras",
        "Reconocer vocales en palabras"
    ),
    ObjetivoDocenteDemo(
        "matematica",
        "Matemática",
        "Números y conteo",
        "Contar objetos del 1 al 10"
    ),
    ObjetivoDocenteDemo(
        "ciencias",
        "Ciencias Naturales",
        "Los seres vivos",
        "Reconocer seres vivos y sus necesidades"
    ),
    ObjetivoDocenteDemo(
        "historia",
        "Historia, Geografía y Ciencias Sociales",
        "Mi familia y comunidad",
        "Reconocer integrantes y espacios de la comunidad"
    )
)

data class AsignacionDocente(
    val asignaturaId: String,
    val fecha: String,
    val maxIntentos: Int
)

class DocenteViewModel(
    context: Context
) : ViewModel() {

    private val preferencias =
        context.applicationContext.getSharedPreferences(
            "mpazpro_asignaciones_demo",
            Context.MODE_PRIVATE
        )

    var asignaciones by mutableStateOf(
        cargarAsignaciones()
    )
        private set

    private fun cargarAsignaciones():
            Map<String, AsignacionDocente> {

        return objetivosDocenteDemo.mapNotNull { objetivo ->

            val id = objetivo.id

            val fecha = preferencias.getString(
                "fecha_$id",
                null
            )

            if (fecha != null) {
                id to AsignacionDocente(
                    asignaturaId = id,
                    fecha = fecha,
                    maxIntentos = preferencias.getInt(
                        "intentos_$id",
                        2
                    )
                )
            } else {
                null
            }

        }.toMap()
    }

    fun guardarAsignacion(
        asignaturaId: String,
        fechaTexto: String,
        maxIntentos: Int
    ): Boolean {

        if (objetivosDocenteDemo.none {
                it.id == asignaturaId
            }
        ) {
            return false
        }

        if (maxIntentos !in 1..5) {
            return false
        }

        val fecha = try {
            LocalDate.parse(fechaTexto.trim())
        } catch (e: Exception) {
            return false
        }

        if (fecha.isBefore(LocalDate.now())) {
            return false
        }

        preferencias.edit()
            .putString(
                "fecha_$asignaturaId",
                fecha.toString()
            )
            .putInt(
                "intentos_$asignaturaId",
                maxIntentos
            )
            .apply()

        asignaciones = cargarAsignaciones()

        return true
    }
}

class DocenteViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (!modelClass.isAssignableFrom(
                DocenteViewModel::class.java
            )
        ) {
            throw IllegalArgumentException(
                "ViewModel no reconocido"
            )
        }

        return DocenteViewModel(context) as T
    }
}
