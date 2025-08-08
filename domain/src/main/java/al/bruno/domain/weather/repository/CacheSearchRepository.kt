package al.bruno.domain.weather.repository

import al.bruno.domain.weather.model.CacheSearch
import kotlinx.coroutines.flow.Flow

interface CacheSearchRepository {
    suspend fun insertCacheSearch(cacheSearch: CacheSearch): Long
    suspend fun deleteCacheSearch(cacheSearch: CacheSearch): Int
    fun getCacheSearch(): Flow<List<CacheSearch>>
}