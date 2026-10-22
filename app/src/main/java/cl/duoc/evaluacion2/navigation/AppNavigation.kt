
package cl.duoc.evaluacion2.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import cl.duoc.evaluacion2.PantallaInicio
import cl.duoc.evaluacion2.local.AppDatabase
import cl.duoc.evaluacion2.screens.LoginScreen
import cl.duoc.evaluacion2.viewmodel.LoginViewModel
import cl.duoc.evaluacion2.viewmodel.LoginViewModelFactory

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current

    val database = remember(context) {
        AppDatabase.obtenerBaseDatos(context)
    }

    val usuarioDao = remember(database) {
        database.usuarioDao()
    }

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {

        composable("inicio") {
            PantallaInicio(
                onPerfilSeleccionado = { perfil ->
                    navController.navigate("login/$perfil")
                }
            )
        }

        composable("login/{perfil}") { entrada ->

            val perfil = entrada.arguments
                ?.getString("perfil") ?: "estudiante"

            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(usuarioDao)
            )

            LoginScreen(
                perfil = perfil,

                cargando = loginViewModel.cargando ||
                        !loginViewModel.preparado,

                mensajeError = loginViewModel.error,

                onIngresar = { correo, contrasena ->

                    loginViewModel.ingresar(
                        correo = correo,
                        contrasena = contrasena,
                        perfil = perfil,

                        onExito = {
                            navController.navigate(perfil) {
                                popUpTo("inicio") {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                },

                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable("estudiante") {
            PantallaPerfil(
                titulo = "Panel Estudiante",
                descripcion = "Mis asignaturas y actividades",
                onVolver = {
                    navController.popBackStack(
                        "inicio",
                        false
                    )
                }
            )
        }

        composable("docente") {
            PantallaPerfil(
                titulo = "Panel Docente",
                descripcion = "Gestión de actividades",
                onVolver = {
                    navController.popBackStack(
                        "inicio",
                        false
                    )
                }
            )
        }

        composable("utp") {
            PantallaPerfil(
                titulo = "Panel Jefatura UTP",
                descripcion = "Administración pedagógica",
                onVolver = {
                    navController.popBackStack(
                        "inicio",
                        false
                    )
                }
            )
        }
    }
}

@Composable
fun PantallaPerfil(
    titulo: String,
    descripcion: String,
    onVolver: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = descripcion)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVolver
        ) {
            Text("Volver al inicio")
        }
    }
}
