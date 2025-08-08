package al.bruno.weather.data

import al.bruno.weather.data.local.AppDatabase
import al.bruno.weather.data.local.CacheSearchLocalDataSource
import al.bruno.weather.data.local.model.CacheSearchEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CacheSearchDataSource @Inject constructor(private val appDatabase: AppDatabase) :
    CacheSearchLocalDataSource {
    override suspend fun insertCacheSearch(cacheSearchEntity: CacheSearchEntity): Long {
        return appDatabase
            .searchDao()
            .insertCacheSearch(
                cacheSearchEntity
            )
    }

    override suspend fun deleteCacheSearch(cacheSearchEntity: CacheSearchEntity): Int {
        return appDatabase
            .searchDao()
            .deleteCacheSearch(
                cacheSearchEntity
            )
    }

    override fun getCacheSearch(): Flow<List<CacheSearchEntity>> {
        return appDatabase.searchDao().getCacheSearch()
    }
}
