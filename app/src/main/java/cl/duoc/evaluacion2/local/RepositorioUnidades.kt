
package cl.duoc.evaluacion2.local

import android.content.Context

data class UnidadEducativa(
    val asignaturaId: String,
    val numero: Int,
    val titulo: String,
    val objetivo: String
)

object RepositorioUnidades {

    val asignaturas = listOf(
        "lenguaje" to "Lenguaje y Comunicación",
        "matematica" to "Matemática",
        "ciencias" to "Ciencias Naturales",
        "historia" to "Historia y Geografía"
    )

    private val contenidosIniciales = mapOf(
        "lenguaje" to listOf(
            "Vocales y sonidos" to "Reconocer vocales en palabras sencillas",
            "Palabras y sílabas" to "Identificar sílabas y formar palabras",
            "Lectura inicial" to "Comprender oraciones breves"
        ),
        "matematica" to listOf(
            "Números y conteo" to "Contar objetos del 1 al 10",
            "Sumas y restas" to "Resolver operaciones sencillas",
            "Formas y patrones" to "Reconocer figuras y secuencias"
        ),
        "ciencias" to listOf(
            "Los seres vivos" to "Reconocer plantas y animales",
            "Cuidado del cuerpo" to "Identificar hábitos saludables",
            "Nuestro entorno" to "Observar cambios en el ambiente"
        ),
        "historia" to listOf(
            "Mi familia" to "Reconocer integrantes de la familia",
            "Mi comunidad" to "Identificar lugares de la comunidad",
            "Pasado y presente" to "Reconocer cambios a lo largo del tiempo"
        )
    )

    private fun preferencias(context: Context) =
        context.applicationContext.getSharedPreferences(
            "mpazpro_unidades_demo",
            Context.MODE_PRIVATE
        )

    fun obtener(
        context: Context,
        asignaturaId: String
    ): List<UnidadEducativa> {

        val definiciones =
            contenidosIniciales[asignaturaId] ?: return emptyList()

        val prefs = preferencias(context)

        return definiciones.mapIndexed { indice, contenido ->
            val numero = indice + 1

            UnidadEducativa(
                asignaturaId = asignaturaId,
                numero = numero,
                titulo = prefs.getString(
                    "titulo_${asignaturaId}_$numero",
                    contenido.first
                ) ?: contenido.first,
                objetivo = prefs.getString(
                    "objetivo_${asignaturaId}_$numero",
                    contenido.second
                ) ?: contenido.second
            )
        }
    }

    fun guardar(
        context: Context,
        unidad: UnidadEducativa
    ): Boolean {

        if (
            unidad.asignaturaId !in contenidosIniciales ||
            unidad.numero !in 1..3 ||
            unidad.titulo.isBlank() ||
            unidad.objetivo.isBlank()
        ) return false

        return preferencias(context).edit()
            .putString(
                "titulo_${unidad.asignaturaId}_${unidad.numero}",
                unidad.titulo.trim()
            )
            .putString(
                "objetivo_${unidad.asignaturaId}_${unidad.numero}",
                unidad.objetivo.trim()
            )
            .commit()
    }

    fun unidadAsignada(
        context: Context,
        asignaturaId: String
    ): Int =
        preferencias(context)
            .getInt("asignada_$asignaturaId", 1)
            .coerceIn(1, 3)

    fun asignarUnidad(
        context: Context,
        asignaturaId: String,
        numero: Int
    ) {
        if (
            asignaturaId in contenidosIniciales &&
            numero in 1..3
        ) {
            preferencias(context).edit()
                .putInt("asignada_$asignaturaId", numero)
                .apply()
        }
    }
}
