
package cl.duoc.evaluacion2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    perfil: String,
    cargando: Boolean,
    mensajeError: String,
    onIngresar: (String, String) -> Unit,
    onVolver: () -> Unit,
    onRegistrarse: () -> Unit
) {

    var correo by rememberSaveable(perfil) {
        mutableStateOf("")
    }

    var contrasena by remember {
        mutableStateOf("")
    }

    var mostrarContrasena by rememberSaveable {
        mutableStateOf(false)
    }

    val nombrePerfil = when (perfil.lowercase()) {
        "estudiante" -> "Estudiante"
        "docente" -> "Docente"
        "utp" -> "Jefatura UTP"
        else -> "Usuario"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {

        Text(
            text = "MPAZ PRO 1.º",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Iniciar sesión - $nombrePerfil",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            visualTransformation = if (mostrarContrasena) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = {
                mostrarContrasena = !mostrarContrasena
            }
        ) {
            Text(
                if (mostrarContrasena) {
                    "Ocultar contraseña"
                } else {
                    "Mostrar contraseña"
                }
            )
        }

        Spacer(Modifier.height(20.dp))

        if (mensajeError.isNotEmpty()) {

            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(Modifier.height(12.dp))
        }

        Button(
            onClick = {
                onIngresar(correo, contrasena)
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {

            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Iniciar sesión")
            }
        }

        if (perfil.lowercase() == "estudiante") {

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = onRegistrarse,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
