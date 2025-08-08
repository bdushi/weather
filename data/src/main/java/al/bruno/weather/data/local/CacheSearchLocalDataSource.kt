package al.bruno.weather.data.local

import al.bruno.weather.data.local.model.CacheSearchEntity
import kotlinx.coroutines.flow.Flow

interface CacheSearchLocalDataSource {
    suspend fun insertCacheSearch(cacheSearchEntity: CacheSearchEntity): Long
    suspend fun deleteCacheSearch(cacheSearchEntity: CacheSearchEntity): Int
    fun getCacheSearch(): Flow<List<CacheSearchEntity>>
}
