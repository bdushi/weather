package al.bruno.domain.weather.usecase

import al.bruno.domain.weather.model.CacheSearch
import al.bruno.domain.weather.repository.CacheSearchRepository

class InsertCacheSearchUseCase(private val cacheSearchRepository: CacheSearchRepository) {
    suspend operator fun invoke(cacheSearch: CacheSearch) =
        cacheSearchRepository.insertCacheSearch(cacheSearch = cacheSearch)
}