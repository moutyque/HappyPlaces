package small.app.happyplaces.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = arrayOf(HappyPlace::class),
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun happyPlaceDAO(): HappyPlaceDAO
}

lateinit var context: Context

@Volatile
private lateinit var INSTANCE: AppDatabase
fun getInstance(_context: Context): AppDatabase {
    synchronized(AppDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            context = _context
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).fallbackToDestructiveMigration()
                .build()

        }
        return INSTANCE
    }
}
