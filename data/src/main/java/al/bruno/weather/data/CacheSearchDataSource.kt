package al.bruno.weather.data

import al.bruno.weather.data.local.CacheSearchLocalDataSource
import al.bruno.weather.data.local.dao.SearchDao
import al.bruno.weather.data.local.model.CacheSearchEntity
import kotlinx.coroutines.flow.Flow

class CacheSearchDataSource(private val searchDao: SearchDao) :
    CacheSearchLocalDataSource {
    override suspend fun insertCacheSearch(cacheSearchEntity: CacheSearchEntity): Long {
        return searchDao
            .insertCacheSearch(
                cacheSearchEntity
            )
    }

    override suspend fun deleteCacheSearch(cacheSearchEntity: CacheSearchEntity): Int {
        return searchDao
            .deleteCacheSearch(
                cacheSearchEntity
            )
    }

    override fun getCacheSearch(): Flow<List<CacheSearchEntity>> {
        return searchDao.getCacheSearch()
    }
}
