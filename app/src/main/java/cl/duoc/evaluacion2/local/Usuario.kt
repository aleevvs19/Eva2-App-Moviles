
package cl.duoc.evaluacion2.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(

    @PrimaryKey
    val correo: String,

    val nombre: String,

    val perfil: String,

    val passwordHash: String,

    val salt: String
)
