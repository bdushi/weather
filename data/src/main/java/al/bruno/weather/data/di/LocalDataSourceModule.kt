package al.bruno.weather.data.di

import al.bruno.weather.data.local.AppDatabase
import al.bruno.weather.data.local.dao.SearchDao
import android.content.Context
import androidx.room.Room
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan
class LocalDataSourceModule {
    @Single(createdAtStart = false)
    fun appDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "weather"
        ).build()
    }

    @Single(createdAtStart = false)
    fun engagementDao(database: AppDatabase): SearchDao {
        return database.searchDao()
    }
}
