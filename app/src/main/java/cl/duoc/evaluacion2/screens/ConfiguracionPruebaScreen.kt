package cl.duoc.evaluacion2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.evaluacion2.local.RepositorioUnidades
import cl.duoc.evaluacion2.viewmodel.AsignacionDocente

/** Pantalla nueva: configuración docente por unidad con calendario. */
@Composable
fun ConfiguracionPruebaScreen(
    asignaciones: Map<String, AsignacionDocente>,
    onGuardar: (String, String, Int) -> Boolean,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var asignaturaId by rememberSaveable { mutableStateOf("matematica") }
    var fecha by rememberSaveable(asignaturaId) {
        mutableStateOf(asignaciones[asignaturaId]?.fecha ?: "")
    }
    var intentos by rememberSaveable(asignaturaId) {
        mutableStateOf((asignaciones[asignaturaId]?.maxIntentos ?: 2).toString())
    }
    var numeroUnidad by rememberSaveable(asignaturaId) {
        mutableIntStateOf(RepositorioUnidades.unidadAsignada(context, asignaturaId))
    }
    var mensaje by remember { mutableStateOf("") }
    var mostrarAviso by remember { mutableStateOf(false) }
    val unidades = RepositorioUnidades.obtener(context, asignaturaId)
    val unidad = unidades.firstOrNull { it.numero == numeroUnidad }

    if (mostrarAviso) {
        AvisoFuturaActualizacion(onCerrar = { mostrarAviso = false })
    }

    Column(
        modifier = Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("📅 Configuración de pruebas", style = MaterialTheme.typography.headlineSmall)
        Text("Curso de prueba: 1.º Básico A")
        Text("Asignatura", style = MaterialTheme.typography.titleMedium)

        RepositorioUnidades.asignaturas.forEach { (id, nombre) ->
            Row {
                RadioButton(
                    selected = asignaturaId == id,
                    onClick = { asignaturaId = id; mensaje = "" }
                )
                Text(nombre, modifier = Modifier.padding(top = 12.dp))
            }
        }

        Text("Seleccionar unidad", style = MaterialTheme.typography.titleMedium)
        unidades.forEach { item ->
            Row {
                RadioButton(
                    selected = numeroUnidad == item.numero,
                    onClick = { numeroUnidad = item.numero; mensaje = "" }
                )
                Text("${item.numero}. ${item.titulo}", modifier = Modifier.padding(top = 12.dp))
            }
        }
        if (unidad != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Objetivo de aprendizaje (ejemplo):")
                    Text(unidad.objetivo)
                }
            }
        }
        Text("Elige la fecha desde el calendario")
        SelectorFecha(
            fecha = fecha,
            onFechaSeleccionada = { fecha = it; mensaje = "" },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = intentos,
            onValueChange = { intentos = it.filter(Char::isDigit).take(1); mensaje = "" },
            label = { Text("Máximo de intentos (1 a 5)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val n = intentos.toIntOrNull()
                if (fecha.isBlank() || n == null || n !in 1..5) {
                    mensaje = "Selecciona una fecha y entre 1 y 5 intentos."
                } else if (onGuardar(asignaturaId, fecha, n)) {
                    RepositorioUnidades.asignarUnidad(context, asignaturaId, numeroUnidad)
                    mensaje = "Configuración guardada localmente."
                    mostrarAviso = true
                } else {
                    mensaje = "Fecha inválida. Selecciona hoy o un día futuro."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar configuración") }
        if (mensaje.isNotEmpty()) Text(mensaje)
        Text(
            "La unidad elegida se almacena localmente como demostración. " +
                    "La disponibilidad y los intentos se guardan por asignatura.",
            style = MaterialTheme.typography.bodySmall
        )
        OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Volver al Docente")
        }
    }
}
