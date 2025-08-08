package al.bruno.weather.data

import al.bruno.domain.weather.model.CacheSearch
import al.bruno.domain.weather.repository.CacheSearchRepository
import al.bruno.weather.data.local.CacheSearchLocalDataSource
import al.bruno.weather.data.local.model.CacheSearchEntity
import al.bruno.weather.data.repository.CacheSearchRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CacheSearchRepositoryTest {

    private val searchDao: CacheSearchLocalDataSource = mockk()
    private lateinit var repository: CacheSearchRepository

    @Before
    fun setUp() {
        repository = CacheSearchRepositoryImpl(searchDao)
    }

    @Test
    fun `insertCacheSearch should return inserted ID`() = runTest {
        val cacheSearch = CacheSearch(123L, "Piqeras")
        coEvery { searchDao.insertCacheSearch(any()) } returns 1L

        val result = repository.insertCacheSearch(cacheSearch)

        assertEquals(1L, result)
        coVerify { searchDao.insertCacheSearch(CacheSearchEntity(123L, "Piqeras")) }
    }

    @Test
    fun `deleteCacheSearch should return deleted count`() = runTest {
        val cacheSearch = CacheSearch(123L, "Piqeras")
        coEvery { searchDao.deleteCacheSearch(any()) } returns 1

        val result = repository.deleteCacheSearch(cacheSearch)

        assertEquals(1, result)
        coVerify { searchDao.deleteCacheSearch(CacheSearchEntity(123L, "Piqeras")) }
    }

    @Test
    fun `getCacheSearch should emit mapped domain models`() = runTest {
        val entities = listOf(CacheSearchEntity(123L, "Piqeras"))
        every { searchDao.getCacheSearch() } returns flowOf(entities)

        val result = repository.getCacheSearch().first()

        assertEquals(listOf(CacheSearch(123L, "Piqeras")), result)
    }
}