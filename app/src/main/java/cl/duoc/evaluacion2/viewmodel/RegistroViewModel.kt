
package cl.duoc.evaluacion2.viewmodel

import android.util.Base64
import android.util.Patterns
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
import java.security.SecureRandom
import java.util.Locale
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class RegistroViewModel(
    private val usuarioDao: UsuarioDao
) : ViewModel() {

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf("")
        private set

    fun limpiarError() {
        error = ""
    }

    fun registrar(
        nombre: String,
        correo: String,
        contrasena: String,
        confirmarContrasena: String,
        onExito: () -> Unit
    ) {

        if (cargando) return

        val nombreLimpio = nombre.trim()
        val correoLimpio = correo.trim()
            .lowercase(Locale.ROOT)

        error = ""

        if (nombreLimpio.isBlank()) {
            error = "Ingresa tu nombre."
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(
                correoLimpio
            ).matches()
        ) {
            error = "Ingresa un correo válido."
            return
        }

        if (contrasena.length < 6) {
            error = "La contraseña debe tener al menos 6 caracteres."
            return
        }

        if (contrasena != confirmarContrasena) {
            error = "Las contraseñas no coinciden."
            return
        }

        cargando = true

        viewModelScope.launch {

            try {

                val resultado = withContext(
                    Dispatchers.IO
                ) {

                    val saltBytes = ByteArray(16)
                    SecureRandom().nextBytes(saltBytes)

                    val saltTexto = Base64.encodeToString(
                        saltBytes,
                        Base64.NO_WRAP
                    )

                    val especificacion = PBEKeySpec(
                        contrasena.toCharArray(),
                        saltBytes,
                        120000,
                        256
                    )

                    val passwordHash = try {

                        val factory =
                            SecretKeyFactory.getInstance(
                                "PBKDF2WithHmacSHA256"
                            )

                        Base64.encodeToString(
                            factory.generateSecret(
                                especificacion
                            ).encoded,
                            Base64.NO_WRAP
                        )

                    } finally {
                        especificacion.clearPassword()
                    }

                    usuarioDao.insertarUsuario(
                        Usuario(
                            correo = correoLimpio,
                            nombre = nombreLimpio,
                            perfil = "estudiante",
                            passwordHash = passwordHash,
                            salt = saltTexto
                        )
                    )
                }

                if (resultado == -1L) {

                    error = "Este correo ya está registrado."

                } else {

                    error = ""
                    onExito()
                }

            } catch (e: Exception) {

                error = "No fue posible registrar el usuario."

            } finally {

                cargando = false
            }
        }
    }
}

class RegistroViewModelFactory(
    private val usuarioDao: UsuarioDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (!modelClass.isAssignableFrom(
                RegistroViewModel::class.java
            )
        ) {
            throw IllegalArgumentException(
                "ViewModel no reconocido"
            )
        }

        return RegistroViewModel(usuarioDao) as T
    }
}
