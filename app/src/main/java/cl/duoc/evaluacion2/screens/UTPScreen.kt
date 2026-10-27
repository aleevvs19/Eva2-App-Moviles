
package cl.duoc.evaluacion2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import cl.duoc.evaluacion2.viewmodel.DatosContenidoUTP
import cl.duoc.evaluacion2.viewmodel.EstadoUTP
import cl.duoc.evaluacion2.viewmodel.UTPViewModel

import kotlinx.coroutines.launch

@Composable
fun UTPScreen(
    viewModel: UTPViewModel,
    onVolver: () -> Unit
) {

    val asignaturas = listOf(
        "lenguaje" to "Lenguaje y Comunicación",
        "matematica" to "Matemática",
        "ciencias" to "Ciencias Naturales",
        "historia" to "Historia y Geografía"
    )

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // CONTROL DEL FORMULARIO
    var idEditando by remember {
        mutableStateOf<Long?>(null)
    }

    var asignatura by rememberSaveable {
        mutableStateOf("matematica")
    }

    var unidad by rememberSaveable {
        mutableStateOf("")
    }

    var codigoOA by rememberSaveable {
        mutableStateOf("")
    }

    var descripcionOA by rememberSaveable {
        mutableStateOf("")
    }

    var explicacion by rememberSaveable {
        mutableStateOf("")
    }

    var tituloActividad by rememberSaveable {
        mutableStateOf("")
    }

    var pregunta by rememberSaveable {
        mutableStateOf("")
    }

    // CONTROL DE RECHAZOS
    var observacion by rememberSaveable {
        mutableStateOf("")
    }

    var rechazandoId by remember {
        mutableStateOf<Long?>(null)
    }

    // MENSAJES
    var mensaje by remember {
        mutableStateOf("")
    }

    var mostrarAviso by remember {
        mutableStateOf(false)
    }

    // LIMPIAR FORMULARIO
    fun limpiarFormulario() {
        idEditando = null
        asignatura = "matematica"
        unidad = ""
        codigoOA = ""
        descripcionOA = ""
        explicacion = ""
        tituloActividad = ""
        pregunta = ""
    }

    // MOSTRAR RESULTADO DE UNA OPERACION
    fun mostrarResultado(
        correcto: Boolean,
        textoExito: String,
        textoError: String
    ) {

        if (correcto) {
            mensaje = textoExito

            // Aviso solicitado por el profesor
            mostrarAviso = true

        } else {
            mensaje = textoError
        }
    }

    // VENTANA INFORMATIVA
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
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {

        // ENCABEZADO
        Text(
            text = "📚 Panel Jefatura UTP",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Administración pedagógica - MPAZ PRO 1.º"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // TITULO DEL FORMULARIO
        Text(
            text = if (idEditando == null) {
                "Crear contenido educativo"
            } else {
                "Editar contenido #$idEditando"
            },
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Selecciona la asignatura",
            style = MaterialTheme.typography.titleMedium
        )

        // SELECCION DE ASIGNATURA
        asignaturas.forEach { (id, nombre) ->

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = asignatura == id,
                    onClick = {
                        asignatura = id
                        mensaje = ""
                    }
                )

                Text(
                    text = nombre,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // UNIDAD
        OutlinedTextField(
            value = unidad,
            onValueChange = {
                unidad = it
                mensaje = ""
            },
            label = {
                Text("Nombre de la unidad")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CODIGO OA
        OutlinedTextField(
            value = codigoOA,
            onValueChange = {
                codigoOA = it
                mensaje = ""
            },
            label = {
                Text("Código OA (si corresponde)")
            },
            supportingText = {
                Text("Utiliza un código oficial solo si está verificado.")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // DESCRIPCION DEL OA
        OutlinedTextField(
            value = descripcionOA,
            onValueChange = {
                descripcionOA = it
                mensaje = ""
            },
            label = {
                Text("Descripción del objetivo de aprendizaje")
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(12.dp))

        // EXPLICACION EDUCATIVA
        OutlinedTextField(
            value = explicacion,
            onValueChange = {
                explicacion = it
                mensaje = ""
            },
            label = {
                Text("Explicación educativa")
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(12.dp))

        // TITULO DE ACTIVIDAD
        OutlinedTextField(
            value = tituloActividad,
            onValueChange = {
                tituloActividad = it
                mensaje = ""
            },
            label = {
                Text("Título de la actividad")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // PREGUNTA O DESAFIO
        OutlinedTextField(
            value = pregunta,
            onValueChange = {
                pregunta = it
                mensaje = ""
            },
            label = {
                Text("Pregunta o desafío")
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(20.dp))

        // GUARDAR CONTENIDO
        Button(
            onClick = {

                val datos = DatosContenidoUTP(
                    asignaturaId = asignatura,
                    unidad = unidad,
                    codigoOA = codigoOA,
                    descripcionOA = descripcionOA,
                    explicacion = explicacion,
                    tituloActividad = tituloActividad,
                    pregunta = pregunta
                )

                val correcto = viewModel.guardarContenido(
                    idEditando,
                    datos
                )

                if (correcto) {

                    limpiarFormulario()

                    mostrarResultado(
                        correcto = true,
                        textoExito = "Contenido guardado como borrador",
                        textoError = ""
                    )

                } else {

                    mensaje =
                        "Completa los campos obligatorios. " +
                                "Solo se pueden editar borradores " +
                                "o contenidos rechazados."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                if (idEditando == null) {
                    "Guardar nuevo borrador"
                } else {
                    "Guardar cambios"
                }
            )
        }

        // CANCELAR EDICION
        if (idEditando != null) {

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    limpiarFormulario()
                    mensaje = ""
                }
            ) {
                Text("Cancelar edición")
            }
        }

        // MENSAJE DE OPERACION
        if (mensaje.isNotEmpty()) {

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(20.dp))

        // SECCION REVISION
        Text(
            text = "Revisión de contenidos",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (viewModel.contenidos.isEmpty()) {

            Text(
                text = "Todavía no hay contenidos creados."
            )
        }

        viewModel.contenidos.reversed().forEach { contenido ->

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    // DATOS DEL CONTENIDO
                    Text(
                        text = contenido.datos.tituloActividad,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val nombreAsignatura =
                        asignaturas.firstOrNull {
                            it.first == contenido.datos.asignaturaId
                        }?.second ?: contenido.datos.asignaturaId

                    Text(
                        text = "Asignatura: $nombreAsignatura"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Unidad: ${contenido.datos.unidad}"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "OA: ${contenido.datos.descripcionOA}"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Estado: ${contenido.estado.etiqueta}",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Actualizado: ${contenido.actualizado}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    // OBSERVACIONES
                    if (contenido.observacion.isNotBlank()) {

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Observación: ${contenido.observacion}"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // BOTONES SEGUN ESTADO
                    when (contenido.estado) {

                        // BORRADOR Y RECHAZADO
                        EstadoUTP.BORRADOR,
                        EstadoUTP.RECHAZADO -> {

                            OutlinedButton(
                                onClick = {

                                    idEditando = contenido.id

                                    val datos = contenido.datos

                                    asignatura = datos.asignaturaId
                                    unidad = datos.unidad
                                    codigoOA = datos.codigoOA
                                    descripcionOA = datos.descripcionOA
                                    explicacion = datos.explicacion
                                    tituloActividad = datos.tituloActividad
                                    pregunta = datos.pregunta

                                    mensaje =
                                        "Editando contenido #${contenido.id}"

                                    // Regresar arriba al formulario
                                    coroutineScope.launch {
                                        scrollState.animateScrollTo(0)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Editar contenido")
                            }

                            if (contenido.estado == EstadoUTP.BORRADOR) {

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {

                                        val correcto =
                                            viewModel.enviarRevision(
                                                contenido.id
                                            )

                                        mostrarResultado(
                                            correcto,
                                            "Contenido enviado a revisión",
                                            "No se pudo enviar a revisión"
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Enviar a revisión")
                                }

                            } else {

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Corrige este contenido y " +
                                            "guarda los cambios antes de " +
                                            "enviarlo nuevamente a revisión.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        // EN REVISION
                        EstadoUTP.EN_REVISION -> {

                            Button(
                                onClick = {

                                    val correcto =
                                        viewModel.aprobar(
                                            contenido.id
                                        )

                                    mostrarResultado(
                                        correcto,
                                        "Contenido autorizado",
                                        "No se pudo autorizar"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Aprobar contenido")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {

                                    rechazandoId = contenido.id
                                    observacion = ""
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Rechazar contenido")
                            }

                            // FORMULARIO DE RECHAZO
                            if (rechazandoId == contenido.id) {

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = observacion,
                                    onValueChange = {
                                        observacion = it
                                    },
                                    label = {
                                        Text("Motivo del rechazo")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 2
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {

                                        val correcto =
                                            viewModel.rechazar(
                                                contenido.id,
                                                observacion
                                            )

                                        if (correcto) {

                                            rechazandoId = null
                                            observacion = ""
                                        }

                                        mostrarResultado(
                                            correcto,
                                            "Contenido rechazado",
                                            "Escribe una observación " +
                                                    "para rechazar el contenido."
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Confirmar rechazo")
                                }

                                TextButton(
                                    onClick = {

                                        rechazandoId = null
                                        observacion = ""
                                    }
                                ) {
                                    Text("Cancelar rechazo")
                                }
                            }
                        }

                        // AUTORIZADO
                        EstadoUTP.AUTORIZADO -> {

                            Button(
                                onClick = {

                                    val correcto =
                                        viewModel.publicar(
                                            contenido.id
                                        )

                                    mostrarResultado(
                                        correcto,
                                        "Contenido publicado",
                                        "No se pudo publicar"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Publicar contenido")
                            }
                        }

                        // PUBLICADO
                        EstadoUTP.PUBLICADO -> {

                            OutlinedButton(
                                onClick = {

                                    val correcto =
                                        viewModel.deshabilitar(
                                            contenido.id
                                        )

                                    mostrarResultado(
                                        correcto,
                                        "Contenido deshabilitado",
                                        "No se pudo deshabilitar"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Deshabilitar contenido")
                            }
                        }

                        // DESHABILITADO
                        EstadoUTP.DESHABILITADO -> {

                            Text(
                                text = "Este contenido se encuentra " +
                                        "fuera de publicación.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(20.dp))

        // BITACORA
        Text(
            text = "Bitácora de modificaciones",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (viewModel.bitacora.isEmpty()) {

            Text(
                text = "No existen modificaciones registradas."
            )
        }

        viewModel.bitacora.take(15).forEach { registro ->

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {

                    Text(
                        text = "${registro.fecha} — " +
                                "Contenido #${registro.contenidoId}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = registro.accion,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // VOLVER
        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio")
        }
    }
}
