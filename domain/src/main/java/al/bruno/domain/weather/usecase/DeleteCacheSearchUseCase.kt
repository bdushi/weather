package al.bruno.domain.weather.usecase

import al.bruno.domain.weather.model.CacheSearch
import al.bruno.domain.weather.repository.CacheSearchRepository

class DeleteCacheSearchUseCase(private val cacheSearchRepository: CacheSearchRepository) {
    suspend operator fun invoke(cacheSearch: CacheSearch) =
        cacheSearchRepository.deleteCacheSearch(cacheSearch = cacheSearch)
}