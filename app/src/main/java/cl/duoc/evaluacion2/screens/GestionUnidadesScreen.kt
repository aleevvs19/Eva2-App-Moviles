package cl.duoc.evaluacion2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.evaluacion2.local.RepositorioUnidades
import cl.duoc.evaluacion2.local.UnidadEducativa

/** Pantalla nueva: UTP puede editar tres unidades ficticias por asignatura. */
@Composable
fun GestionUnidadesScreen(onVolver: () -> Unit) {
    val context = LocalContext.current
    var asignaturaId by rememberSaveable { mutableStateOf("matematica") }
    var unidades by remember { mutableStateOf(RepositorioUnidades.obtener(context, asignaturaId)) }
    var unidadEditando by remember { mutableStateOf<UnidadEducativa?>(null) }
    var titulo by remember { mutableStateOf("") }
    var objetivo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var mostrarAviso by remember { mutableStateOf(false) }

    if (mostrarAviso) {
        AvisoFuturaActualizacion(onCerrar = { mostrarAviso = false })
    }

    if (unidadEditando != null) {
        AlertDialog(
            onDismissRequest = { unidadEditando = null },
            title = { Text("Editar unidad ${unidadEditando!!.numero}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Nombre de la unidad") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = objetivo,
                        onValueChange = { objetivo = it },
                        label = { Text("Objetivo de aprendizaje (ejemplo)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = titulo.isNotBlank() && objetivo.isNotBlank(),
                    onClick = {
                        val actualizada = unidadEditando!!.copy(
                            titulo = titulo.trim(), objetivo = objetivo.trim()
                        )
                        if (RepositorioUnidades.guardar(context, actualizada)) {
                            unidades = RepositorioUnidades.obtener(context, asignaturaId)
                            mensaje = "Unidad ${actualizada.numero} guardada localmente."
                            mostrarAviso = true
                            unidadEditando = null
                        } else {
                            mensaje = "No fue posible guardar la unidad."
                        }
                    }
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { unidadEditando = null }) { Text("Cancelar") }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("📚 Gestión de unidades", style = MaterialTheme.typography.headlineSmall)
        Text("Jefatura UTP — tres unidades editables por asignatura")
        Text("Selecciona una asignatura", style = MaterialTheme.typography.titleMedium)
        RepositorioUnidades.asignaturas.forEach { (id, nombre) ->
            Row {
                RadioButton(
                    selected = asignaturaId == id,
                    onClick = {
                        asignaturaId = id
                        unidades = RepositorioUnidades.obtener(context, id)
                        mensaje = ""
                    }
                )
                Text(nombre, modifier = Modifier.padding(top = 12.dp))
            }
        }

        unidades.forEach { unidad ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Unidad ${unidad.numero}", style = MaterialTheme.typography.labelLarge)
                    Text(unidad.titulo, style = MaterialTheme.typography.titleMedium)
                    Text("Objetivo: ${unidad.objetivo}")
                    Button(
                        onClick = {
                            unidadEditando = unidad
                            titulo = unidad.titulo
                            objetivo = unidad.objetivo
                        }
                    ) { Text("✏️ Editar unidad") }
                }
            }
        }
        if (mensaje.isNotEmpty()) Text(mensaje, color = MaterialTheme.colorScheme.primary)
        Text(
            "Estos títulos y objetivos son ejemplos de demostración; " +
                    "no corresponden todavía a una aprobación curricular definitiva.",
            style = MaterialTheme.typography.bodySmall
        )
        OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Volver a UTP")
        }
    }
}
