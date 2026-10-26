
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
import cl.duoc.evaluacion2.screens.EstudianteScreen
import cl.duoc.evaluacion2.screens.ActividadMatematicaScreen
import cl.duoc.evaluacion2.screens.ActividadOtraAsignaturaScreen
import cl.duoc.evaluacion2.screens.DocenteScreen
import cl.duoc.evaluacion2.screens.UTPScreen
import cl.duoc.evaluacion2.screens.estaAsignaturaDisponible

import cl.duoc.evaluacion2.viewmodel.LoginViewModel
import cl.duoc.evaluacion2.viewmodel.LoginViewModelFactory

import cl.duoc.evaluacion2.viewmodel.EstudianteViewModel
import cl.duoc.evaluacion2.viewmodel.EstudianteViewModelFactory

import cl.duoc.evaluacion2.viewmodel.DocenteViewModel
import cl.duoc.evaluacion2.viewmodel.DocenteViewModelFactory

import cl.duoc.evaluacion2.viewmodel.UTPViewModel
import cl.duoc.evaluacion2.viewmodel.UTPViewModelFactory

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

    val estudianteViewModel: EstudianteViewModel = viewModel(
        factory = remember(context) {
            EstudianteViewModelFactory(
                context.applicationContext
            )
        }
    )

    val docenteViewModel: DocenteViewModel = viewModel(
        factory = remember(context) {
            DocenteViewModelFactory(
                context.applicationContext
            )
        }
    )

    val utpViewModel: UTPViewModel = viewModel(
        factory = remember(context) {
            UTPViewModelFactory(
                context.applicationContext
            )
        }
    )

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {

        // SELECCION DE PERFIL
        composable("inicio") {

            PantallaInicio(
                onPerfilSeleccionado = { perfil ->
                    navController.navigate("login/$perfil")
                }
            )
        }

        // LOGIN CON ROOM
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

        // PANEL ESTUDIANTE
        composable("estudiante") {

            EstudianteScreen(
                resultadoMatematica =
                    estudianteViewModel.resultadoMatematica,

                resultadosPorAsignatura =
                    estudianteViewModel.resultadosPorAsignatura,

                asignaciones =
                    docenteViewModel.asignaciones,

                onAbrirAsignatura = { id ->

                    val asignacion =
                        docenteViewModel.asignaciones[id]

                    if (estaAsignaturaDisponible(asignacion)) {

                        if (id == "matematica") {

                            navController.navigate(
                                "actividad/matematica"
                            )

                        } else {

                            navController.navigate(
                                "actividad-otra/$id"
                            )
                        }
                    }
                },

                onCerrarSesion = {

                    navController.popBackStack(
                        "inicio",
                        false
                    )
                }
            )
        }

        // ACTIVIDAD MATEMATICA
        composable("actividad/matematica") {

            val disponible = estaAsignaturaDisponible(
                docenteViewModel.asignaciones["matematica"]
            )

            if (disponible) {

                ActividadMatematicaScreen(
                    onFinalizar = { aciertos, total ->

                        estudianteViewModel
                            .guardarResultadoMatematica(
                                aciertos,
                                total
                            )
                    },

                    onVolver = {
                        navController.popBackStack()
                    }
                )

            } else {

                PantallaActividadBloqueada(
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // OTRAS TRES ASIGNATURAS
        composable("actividad-otra/{id}") { entrada ->

            val id = entrada.arguments
                ?.getString("id") ?: ""

            val disponible = estaAsignaturaDisponible(
                docenteViewModel.asignaciones[id]
            )

            if (disponible) {

                ActividadOtraAsignaturaScreen(
                    asignaturaId = id,

                    onFinalizar = {
                            asignaturaId, aciertos, total ->

                        estudianteViewModel
                            .guardarResultadoAsignatura(
                                asignaturaId,
                                aciertos,
                                total
                            )
                    },

                    onVolver = {
                        navController.popBackStack()
                    }
                )

            } else {

                PantallaActividadBloqueada(
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // PANEL DOCENTE
        composable("docente") {

            DocenteScreen(
                asignaciones =
                    docenteViewModel.asignaciones,

                onGuardar = {
                        asignaturaId, fecha, intentos ->

                    docenteViewModel.guardarAsignacion(
                        asignaturaId,
                        fecha,
                        intentos
                    )
                },

                onVolver = {

                    navController.popBackStack(
                        "inicio",
                        false
                    )
                }
            )
        }

        // PANEL JEFATURA UTP
        composable("utp") {

            UTPScreen(
                viewModel = utpViewModel,

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
fun PantallaActividadBloqueada(
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
            text = "🔒 Actividad no disponible",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Tu profesor todavía no ha " +
                    "habilitado esta actividad."
        )

        Spacer(Modifier.height(24.dp))

        Button(onClick = onVolver) {
            Text("Volver")
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

        Spacer(Modifier.height(16.dp))

        Text(text = descripcion)

        Spacer(Modifier.height(24.dp))

        Button(onClick = onVolver) {
            Text("Volver al inicio")
        }
    }
}
