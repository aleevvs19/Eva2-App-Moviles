
package cl.duoc.evaluacion2.screens

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
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
    onVolver: () -> Unit
) {

    var correo by rememberSaveable {
        mutableStateOf("")
    }

    var contrasena by rememberSaveable {
        mutableStateOf("")
    }

    var mostrarContrasena by rememberSaveable {
        mutableStateOf(false)
    }

    var errorLocal by remember {
        mutableStateOf("")
    }

    val nombrePerfil = when (perfil) {
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "MPAZ PRO 1.º",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Escuela Escritora Marcela Paz")

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Perfil: $nombrePerfil")

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                errorLocal = ""
            },
            label = { Text("Correo electrónico") },
            placeholder = { Text("Ingresa tu correo") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                errorLocal = ""
            },
            label = { Text("Contraseña") },
            placeholder = { Text("Ingresa tu contraseña") },
            singleLine = true,
            visualTransformation =
                if (mostrarContrasena) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth()
        )

        TextButton(
            onClick = {
                mostrarContrasena = !mostrarContrasena
            }
        ) {
            Text(
                if (mostrarContrasena)
                    "Ocultar contraseña"
                else
                    "Mostrar contraseña"
            )
        }

        if (errorLocal.isNotEmpty()) {
            Text(
                text = errorLocal,
                color = MaterialTheme.colorScheme.error
            )
        } else if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                val correoLimpio =
                    correo.trim().lowercase()

                if (correoLimpio.isEmpty() ||
                    contrasena.isEmpty()
                ) {

                    errorLocal = "Completa todos los campos"

                } else if (
                    !Patterns.EMAIL_ADDRESS
                        .matcher(correoLimpio)
                        .matches()
                ) {

                    errorLocal = "Ingresa un correo válido"

                } else {

                    errorLocal = ""
                    onIngresar(correoLimpio, contrasena)
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (cargando)
                    "Procesando..."
                else
                    "Ingresar"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Aplicación educativa - 1.º Básico",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
