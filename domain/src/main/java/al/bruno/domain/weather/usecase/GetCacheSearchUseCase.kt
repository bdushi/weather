package al.bruno.domain.weather.usecase

import al.bruno.domain.weather.repository.CacheSearchRepository

class GetCacheSearchUseCase(private val cacheSearchRepository: CacheSearchRepository) {
    operator fun invoke() =
        cacheSearchRepository.getCacheSearch()
}