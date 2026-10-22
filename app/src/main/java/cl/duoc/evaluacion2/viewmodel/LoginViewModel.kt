
package cl.duoc.evaluacion2.viewmodel

import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.evaluacion2.local.Usuario
import cl.duoc.evaluacion2.local.UsuarioDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class LoginViewModel(
    private val usuarioDao: UsuarioDao
) : ViewModel() {

    var preparado by mutableStateOf(false)
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf("")
        private set

    init {
        crearUsuariosIniciales()
    }

    private fun crearUsuariosIniciales() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {

                    val cuentas = listOf(
                        Triple(
                            "estudiante01@mpazpro.test",
                            "Estudiante de prueba",
                            "estudiante"
                        ),
                        Triple(
                            "docente01@mpazpro.test",
                            "Docente de prueba",
                            "docente"
                        ),
                        Triple(
                            "utp01@mpazpro.test",
                            "Jefatura UTP de prueba",
                            "utp"
                        )
                    )

                    for ((correo, nombre, perfil) in cuentas) {

                        val existente =
                            usuarioDao.buscarPorCorreo(correo)

                        if (existente == null) {

                            val salt = ByteArray(16)
                            SecureRandom().nextBytes(salt)

                            val hash = withContext(
                                Dispatchers.Default
                            ) {
                                calcularHash("1234", salt)
                            }

                            val usuario = Usuario(
                                correo = correo,
                                nombre = nombre,
                                perfil = perfil,
                                passwordHash = Base64.encodeToString(
                                    hash,
                                    Base64.NO_WRAP
                                ),
                                salt = Base64.encodeToString(
                                    salt,
                                    Base64.NO_WRAP
                                )
                            )

                            usuarioDao.insertarUsuario(usuario)
                        }
                    }
                }

                preparado = true

            } catch (e: Exception) {
                error = "Error al preparar la base de datos"
            }
        }
    }

    fun ingresar(
        correo: String,
        contrasena: String,
        perfil: String,
        onExito: () -> Unit
    ) {
        if (!preparado || cargando) return

        viewModelScope.launch {
            cargando = true
            error = ""

            try {
                val usuario = usuarioDao.buscarPorCorreo(
                    correo.trim().lowercase()
                )

                val valido = if (
                    usuario != null &&
                    usuario.perfil == perfil
                ) {

                    withContext(Dispatchers.Default) {

                        val salt = Base64.decode(
                            usuario.salt,
                            Base64.NO_WRAP
                        )

                        val hashGuardado = Base64.decode(
                            usuario.passwordHash,
                            Base64.NO_WRAP
                        )

                        val hashIngresado = calcularHash(
                            contrasena,
                            salt
                        )

                        MessageDigest.isEqual(
                            hashGuardado,
                            hashIngresado
                        )
                    }

                } else {
                    false
                }

                if (valido) {
                    onExito()
                } else {
                    error = "Correo o contraseña incorrectos"
                }

            } catch (e: Exception) {
                error = "No se pudo validar el usuario"
            } finally {
                cargando = false
            }
        }
    }

    private fun calcularHash(
        contrasena: String,
        salt: ByteArray
    ): ByteArray {

        val especificacion = PBEKeySpec(
            contrasena.toCharArray(),
            salt,
            120000,
            256
        )

        return try {
            SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(especificacion)
                .encoded
        } finally {
            especificacion.clearPassword()
        }
    }
}

class LoginViewModelFactory(
    private val usuarioDao: UsuarioDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (!modelClass.isAssignableFrom(
                LoginViewModel::class.java
            )
        ) {
            throw IllegalArgumentException(
                "ViewModel desconocido"
            )
        }

        return LoginViewModel(usuarioDao) as T
    }
}
