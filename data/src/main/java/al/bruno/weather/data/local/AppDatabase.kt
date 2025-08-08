package al.bruno.weather.data.local

import al.bruno.weather.data.local.dao.SearchDao
import androidx.room.Database
import androidx.room.RoomDatabase
import al.bruno.weather.data.local.model.CacheSearchEntity

@Database(
    entities = [CacheSearchEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchDao(): SearchDao
}