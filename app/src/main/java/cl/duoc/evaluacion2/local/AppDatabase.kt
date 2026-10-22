
package cl.duoc.evaluacion2.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Usuario::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obtenerBaseDatos(
            context: Context
        ): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mpazpro_usuarios"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
