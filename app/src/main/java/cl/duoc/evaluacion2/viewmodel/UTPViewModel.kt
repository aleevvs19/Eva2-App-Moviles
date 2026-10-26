
package cl.duoc.evaluacion2.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class EstadoUTP(val etiqueta: String) {
    BORRADOR("Borrador"),
    EN_REVISION("En revisión"),
    AUTORIZADO("Autorizado"),
    RECHAZADO("Rechazado"),
    PUBLICADO("Publicado"),
    DESHABILITADO("Deshabilitado")
}

data class DatosContenidoUTP(
    val asignaturaId: String,
    val unidad: String,
    val codigoOA: String,
    val descripcionOA: String,
    val explicacion: String,
    val tituloActividad: String,
    val pregunta: String
)

data class ContenidoUTP(
    val id: Long,
    val datos: DatosContenidoUTP,
    val estado: EstadoUTP = EstadoUTP.BORRADOR,
    val observacion: String = "",
    val actualizado: String = ""
)

data class RegistroUTP(
    val contenidoId: Long,
    val accion: String,
    val fecha: String
)

class UTPViewModel(context: Context) : ViewModel() {

    private val preferencias =
        context.applicationContext.getSharedPreferences(
            "mpazpro_utp_demo",
            Context.MODE_PRIVATE
        )

    var contenidos by mutableStateOf(leerContenidos())
        private set

    var bitacora by mutableStateOf(leerBitacora())
        private set

    private fun fechaActual(): String {
        return LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        )
    }

    private fun leerContenidos(): List<ContenidoUTP> {
        return try {
            val array = JSONArray(
                preferencias.getString(
                    "contenidos",
                    "[]"
                ) ?: "[]"
            )

            (0 until array.length()).mapNotNull { i ->
                val json = array.optJSONObject(i)
                    ?: return@mapNotNull null

                val estado = EstadoUTP.values()
                    .firstOrNull {
                        it.name == json.optString("estado")
                    } ?: EstadoUTP.BORRADOR

                ContenidoUTP(
                    id = json.optLong("id"),
                    datos = DatosContenidoUTP(
                        asignaturaId = json.optString("asignaturaId"),
                        unidad = json.optString("unidad"),
                        codigoOA = json.optString("codigoOA"),
                        descripcionOA = json.optString("descripcionOA"),
                        explicacion = json.optString("explicacion"),
                        tituloActividad = json.optString("tituloActividad"),
                        pregunta = json.optString("pregunta")
                    ),
                    estado = estado,
                    observacion = json.optString("observacion"),
                    actualizado = json.optString("actualizado")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun leerBitacora(): List<RegistroUTP> {
        return try {
            val array = JSONArray(
                preferencias.getString(
                    "bitacora",
                    "[]"
                ) ?: "[]"
            )

            (0 until array.length()).mapNotNull { i ->
                val json = array.optJSONObject(i)
                    ?: return@mapNotNull null

                RegistroUTP(
                    contenidoId = json.optLong("contenidoId"),
                    accion = json.optString("accion"),
                    fecha = json.optString("fecha")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun guardarContenidos() {
        val array = JSONArray()

        contenidos.forEach { contenido ->
            val datos = contenido.datos

            array.put(
                JSONObject()
                    .put("id", contenido.id)
                    .put("asignaturaId", datos.asignaturaId)
                    .put("unidad", datos.unidad)
                    .put("codigoOA", datos.codigoOA)
                    .put("descripcionOA", datos.descripcionOA)
                    .put("explicacion", datos.explicacion)
                    .put("tituloActividad", datos.tituloActividad)
                    .put("pregunta", datos.pregunta)
                    .put("estado", contenido.estado.name)
                    .put("observacion", contenido.observacion)
                    .put("actualizado", contenido.actualizado)
            )
        }

        preferencias.edit()
            .putString("contenidos", array.toString())
            .apply()
    }

    private fun registrar(
        id: Long,
        accion: String
    ) {
        bitacora = listOf(
            RegistroUTP(id, accion, fechaActual())
        ) + bitacora

        val array = JSONArray()

        bitacora.forEach { registro ->
            array.put(
                JSONObject()
                    .put("contenidoId", registro.contenidoId)
                    .put("accion", registro.accion)
                    .put("fecha", registro.fecha)
            )
        }

        preferencias.edit()
            .putString("bitacora", array.toString())
            .apply()
    }

    fun guardarContenido(
        idEditar: Long?,
        datos: DatosContenidoUTP
    ): Boolean {

        val limpio = datos.copy(
            unidad = datos.unidad.trim(),
            codigoOA = datos.codigoOA.trim(),
            descripcionOA = datos.descripcionOA.trim(),
            explicacion = datos.explicacion.trim(),
            tituloActividad = datos.tituloActividad.trim(),
            pregunta = datos.pregunta.trim()
        )

        val asignaturas = setOf(
            "lenguaje",
            "matematica",
            "ciencias",
            "historia"
        )

        if (limpio.asignaturaId !in asignaturas ||
            limpio.unidad.isBlank() ||
            limpio.descripcionOA.isBlank() ||
            limpio.explicacion.isBlank() ||
            limpio.tituloActividad.isBlank() ||
            limpio.pregunta.isBlank()
        ) {
            return false
        }

        if (idEditar == null) {

            val nuevoId =
                (contenidos.maxOfOrNull { it.id } ?: 0L) + 1L

            contenidos = contenidos + ContenidoUTP(
                id = nuevoId,
                datos = limpio,
                actualizado = fechaActual()
            )

            guardarContenidos()
            registrar(nuevoId, "Contenido creado como borrador")

        } else {

            val anterior = contenidos.firstOrNull {
                it.id == idEditar
            } ?: return false

            if (anterior.estado != EstadoUTP.BORRADOR &&
                anterior.estado != EstadoUTP.RECHAZADO
            ) {
                return false
            }

            contenidos = contenidos.map {
                if (it.id == idEditar) {
                    it.copy(
                        datos = limpio,
                        estado = EstadoUTP.BORRADOR,
                        observacion = "",
                        actualizado = fechaActual()
                    )
                } else {
                    it
                }
            }

            guardarContenidos()
            registrar(idEditar, "Borrador modificado")
        }

        return true
    }

    private fun cambiarEstado(
        id: Long,
        estadosPermitidos: Set<EstadoUTP>,
        nuevoEstado: EstadoUTP,
        observacion: String = ""
    ): Boolean {

        val anterior = contenidos.firstOrNull {
            it.id == id
        } ?: return false

        if (anterior.estado !in estadosPermitidos) {
            return false
        }

        if (nuevoEstado == EstadoUTP.RECHAZADO &&
            observacion.isBlank()
        ) {
            return false
        }

        contenidos = contenidos.map {
            if (it.id == id) {
                it.copy(
                    estado = nuevoEstado,
                    observacion = observacion.trim(),
                    actualizado = fechaActual()
                )
            } else {
                it
            }
        }

        guardarContenidos()
        registrar(id, "Estado cambiado a: ${nuevoEstado.etiqueta}")

        return true
    }

    fun enviarRevision(id: Long): Boolean {
        return cambiarEstado(
            id,
            setOf(EstadoUTP.BORRADOR),
            EstadoUTP.EN_REVISION
        )
    }

    fun aprobar(id: Long): Boolean {
        return cambiarEstado(
            id,
            setOf(EstadoUTP.EN_REVISION),
            EstadoUTP.AUTORIZADO
        )
    }

    fun rechazar(
        id: Long,
        observacion: String
    ): Boolean {
        return cambiarEstado(
            id,
            setOf(EstadoUTP.EN_REVISION),
            EstadoUTP.RECHAZADO,
            observacion
        )
    }

    fun publicar(id: Long): Boolean {
        return cambiarEstado(
            id,
            setOf(EstadoUTP.AUTORIZADO),
            EstadoUTP.PUBLICADO
        )
    }

    fun deshabilitar(id: Long): Boolean {
        return cambiarEstado(
            id,
            setOf(EstadoUTP.PUBLICADO),
            EstadoUTP.DESHABILITADO
        )
    }
}

class UTPViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (!modelClass.isAssignableFrom(
                UTPViewModel::class.java
            )
        ) {
            throw IllegalArgumentException(
                "ViewModel no reconocido"
            )
        }

        return UTPViewModel(context) as T
    }
}
