
package cl.duoc.evaluacion2.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

data class ResultadoMatematica(
    val completada: Boolean = false,
    val aciertos: Int = 0,
    val total: Int = 3
)

data class ResultadoAsignatura(
    val completada: Boolean = false,
    val aciertos: Int = 0,
    val total: Int = 3
)

class EstudianteViewModel(
    context: Context
) : ViewModel() {

    private val preferencias =
        context.applicationContext.getSharedPreferences(
            "mpazpro_progreso_demo",
            Context.MODE_PRIVATE
        )

    private val otrasAsignaturas = setOf(
        "lenguaje",
        "ciencias",
        "historia"
    )

    // Mantener el progreso anterior de Matemática
    var resultadoMatematica by mutableStateOf(
        ResultadoMatematica(
            completada = preferencias.getBoolean(
                "matematica_completada",
                false
            ),
            aciertos = preferencias.getInt(
                "matematica_aciertos",
                0
            ),
            total = preferencias.getInt(
                "matematica_total",
                3
            )
        )
    )
        private set

    // Resultados de las otras tres asignaturas
    var resultadosPorAsignatura by mutableStateOf(
        otrasAsignaturas.associateWith { id ->

            ResultadoAsignatura(
                completada = preferencias.getBoolean(
                    "${id}_completada",
                    false
                ),
                aciertos = preferencias.getInt(
                    "${id}_aciertos",
                    0
                ),
                total = preferencias.getInt(
                    "${id}_total",
                    3
                )
            )
        }
    )
        private set

    fun guardarResultadoMatematica(
        aciertos: Int,
        total: Int
    ) {

        preferencias.edit()
            .putBoolean("matematica_completada", true)
            .putInt("matematica_aciertos", aciertos)
            .putInt("matematica_total", total)
            .apply()

        resultadoMatematica = ResultadoMatematica(
            completada = true,
            aciertos = aciertos,
            total = total
        )
    }

    fun guardarResultadoAsignatura(
        id: String,
        aciertos: Int,
        total: Int
    ) {

        if (id !in otrasAsignaturas) return

        preferencias.edit()
            .putBoolean("${id}_completada", true)
            .putInt("${id}_aciertos", aciertos)
            .putInt("${id}_total", total)
            .apply()

        resultadosPorAsignatura =
            resultadosPorAsignatura + (
                    id to ResultadoAsignatura(
                        completada = true,
                        aciertos = aciertos,
                        total = total
                    )
                    )
    }
}

class EstudianteViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (!modelClass.isAssignableFrom(
                EstudianteViewModel::class.java
            )
        ) {
            throw IllegalArgumentException(
                "ViewModel no reconocido"
            )
        }

        return EstudianteViewModel(context) as T
    }
}
