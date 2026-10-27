
package cl.duoc.evaluacion2.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarUsuario(usuario: Usuario): Long

    @Query(
        "SELECT * FROM usuarios WHERE correo = :correo LIMIT 1"
    )
    suspend fun buscarPorCorreo(correo: String): Usuario?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int

    @Query("SELECT * FROM usuarios")
    suspend fun obtenerUsuarios(): List<Usuario>
}
