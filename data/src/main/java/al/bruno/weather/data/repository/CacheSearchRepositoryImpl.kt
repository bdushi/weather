package al.bruno.weather.data.repository

import al.bruno.domain.weather.model.CacheSearch
import al.bruno.domain.weather.repository.CacheSearchRepository
import al.bruno.weather.data.local.CacheSearchLocalDataSource
import al.bruno.weather.data.local.model.CacheSearchEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single(createdAtStart = false, binds = [CacheSearchRepository::class])
class CacheSearchRepositoryImpl(
    private val cacheSearchLocalDataSource: CacheSearchLocalDataSource
) : CacheSearchRepository {
    override suspend fun insertCacheSearch(cacheSearch: CacheSearch): Long {
        return cacheSearchLocalDataSource
            .insertCacheSearch(
                CacheSearchEntity(
                    id = cacheSearch.id,
                    query = cacheSearch.query
                )
            )
    }

    override suspend fun deleteCacheSearch(cacheSearch: CacheSearch): Int {
        return cacheSearchLocalDataSource
            .deleteCacheSearch(
                cacheSearchEntity = CacheSearchEntity(
                    id = cacheSearch.id,
                    query = cacheSearch.query
                )
            )
    }

    override fun getCacheSearch(): Flow<List<CacheSearch>> {
        return cacheSearchLocalDataSource.getCacheSearch().map {
            it.map { cacheSearch ->
                CacheSearch(
                    id = cacheSearch.id,
                    query = cacheSearch.query
                )
            }
        }
    }
}
