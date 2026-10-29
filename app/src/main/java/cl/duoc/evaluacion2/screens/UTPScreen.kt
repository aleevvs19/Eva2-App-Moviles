package cl.duoc.evaluacion2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import cl.duoc.evaluacion2.viewmodel.*

@Composable
fun UTPScreen(
    viewModel: UTPViewModel,
    onAbrirUnidades: () -> Unit,
    onVolver: () -> Unit
) {

    val asignaturas = listOf(
        "lenguaje" to "Lenguaje y Comunicación",
        "matematica" to "Matemática",
        "ciencias" to "Ciencias Naturales",
        "historia" to "Historia y Geografía"
    )

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

    var observacion by rememberSaveable {
        mutableStateOf("")
    }

    var rechazandoId by remember {
        mutableStateOf<Long?>(null)
    }

    var mensaje by remember {
        mutableStateOf("")
    }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = "📚 Panel Jefatura UTP",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text("Administración pedagógica - MPAZ PRO 1.º")

        Spacer(Modifier.height(24.dp))

        // NUEVO: gestión de las tres unidades de cada asignatura
        Button(
            onClick = onAbrirUnidades,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📚 Gestionar unidades")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = if (idEditando == null)
                "Crear contenido educativo"
            else
                "Editar contenido #$idEditando",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "Selecciona la asignatura",
            style = MaterialTheme.typography.titleMedium
        )

        asignaturas.forEach { (id, nombre) ->

            Row {
                RadioButton(
                    selected = asignatura == id,
                    onClick = { asignatura = id }
                )

                Text(
                    text = nombre,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = unidad,
            onValueChange = { unidad = it },
            label = { Text("Nombre de la unidad") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = codigoOA,
            onValueChange = { codigoOA = it },
            label = { Text("Código OA (si corresponde)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = descripcionOA,
            onValueChange = { descripcionOA = it },
            label = { Text("Descripción del objetivo de aprendizaje") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = explicacion,
            onValueChange = { explicacion = it },
            label = { Text("Explicación educativa") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = tituloActividad,
            onValueChange = { tituloActividad = it },
            label = { Text("Título de la actividad") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = pregunta,
            onValueChange = { pregunta = it },
            label = { Text("Pregunta o desafío") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(Modifier.height(16.dp))

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

                mensaje = if (correcto) {
                    limpiarFormulario()
                    "Contenido guardado como borrador"
                } else {
                    "Completa todos los campos obligatorios"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (idEditando == null)
                    "Guardar nuevo borrador"
                else
                    "Guardar cambios"
            )
        }

        if (idEditando != null) {

            TextButton(
                onClick = {
                    limpiarFormulario()
                    mensaje = ""
                }
            ) {
                Text("Cancelar edición")
            }
        }

        if (mensaje.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(mensaje)
        }

        Spacer(Modifier.height(32.dp))

        HorizontalDivider()

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Revisión de contenidos",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        if (viewModel.contenidos.isEmpty()) {
            Text("Todavía no hay contenidos creados.")
        }

        viewModel.contenidos.reversed().forEach { contenido ->

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = contenido.datos.tituloActividad,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Unidad: ${contenido.datos.unidad}"
                    )

                    Text(
                        "OA: ${contenido.datos.descripcionOA}"
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Estado: ${contenido.estado.etiqueta}",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Text(
                        "Actualizado: ${contenido.actualizado}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    if (contenido.observacion.isNotBlank()) {

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Observación: ${contenido.observacion}"
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    when (contenido.estado) {

                        EstadoUTP.BORRADOR,
                        EstadoUTP.RECHAZADO -> {

                            OutlinedButton(
                                onClick = {
                                    idEditando = contenido.id
                                    val d = contenido.datos
                                    asignatura = d.asignaturaId
                                    unidad = d.unidad
                                    codigoOA = d.codigoOA
                                    descripcionOA = d.descripcionOA
                                    explicacion = d.explicacion
                                    tituloActividad = d.tituloActividad
                                    pregunta = d.pregunta
                                    mensaje = "Editando contenido #${contenido.id}. Sube al formulario."
                                }
                            ) {
                                Text("Editar borrador")
                            }

                            Button(
                                onClick = {
                                    if (viewModel.enviarRevision(contenido.id)) {
                                        mensaje = "Contenido enviado a revisión"
                                    }
                                }
                            ) {
                                Text("Enviar a revisión")
                            }
                        }

                        EstadoUTP.EN_REVISION -> {

                            Button(
                                onClick = {
                                    viewModel.aprobar(contenido.id)
                                    mensaje = "Contenido autorizado"
                                }
                            ) {
                                Text("Aprobar")
                            }

                            OutlinedButton(
                                onClick = {
                                    rechazandoId = contenido.id
                                    observacion = ""
                                }
                            ) {
                                Text("Rechazar")
                            }

                            if (rechazandoId == contenido.id) {

                                OutlinedTextField(
                                    value = observacion,
                                    onValueChange = {
                                        observacion = it
                                    },
                                    label = {
                                        Text("Motivo del rechazo")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        val correcto = viewModel.rechazar(
                                            contenido.id,
                                            observacion
                                        )

                                        if (correcto) {
                                            rechazandoId = null
                                            observacion = ""
                                            mensaje = "Contenido rechazado"
                                        } else {
                                            mensaje = "Escribe una observación"
                                        }
                                    }
                                ) {
                                    Text("Confirmar rechazo")
                                }
                            }
                        }

                        EstadoUTP.AUTORIZADO -> {

                            Button(
                                onClick = {
                                    viewModel.publicar(contenido.id)
                                    mensaje = "Contenido publicado"
                                }
                            ) {
                                Text("Publicar")
                            }
                        }

                        EstadoUTP.PUBLICADO -> {

                            OutlinedButton(
                                onClick = {
                                    viewModel.deshabilitar(contenido.id)
                                    mensaje = "Contenido deshabilitado"
                                }
                            ) {
                                Text("Deshabilitar")
                            }
                        }

                        EstadoUTP.DESHABILITADO -> {

                            Text(
                                "Contenido fuera de publicación",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(24.dp))

        HorizontalDivider()

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Bitácora de modificaciones",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        if (viewModel.bitacora.isEmpty()) {
            Text("No existen modificaciones registradas.")
        }

        viewModel.bitacora.take(15).forEach { registro ->

            Text(
                text = "${registro.fecha} — " +
                        "Contenido #${registro.contenidoId}"
            )

            Text(
                text = registro.accion,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio")
        }
    }
}
