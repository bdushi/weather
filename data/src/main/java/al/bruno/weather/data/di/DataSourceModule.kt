package al.bruno.weather.data.di

import al.bruno.weather.data.CacheSearchDataSource
import al.bruno.weather.data.local.CacheSearchLocalDataSource
import al.bruno.weather.data.local.dao.SearchDao
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single


@Module
@ComponentScan
@Configuration
class DataSourceModule {
    @Single(createdAtStart = false)
    fun provideSearchDataSource(searchDao: SearchDao): CacheSearchLocalDataSource =
        CacheSearchDataSource(searchDao)
}