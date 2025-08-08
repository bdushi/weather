package al.bruno.weather.data.local.dao

import al.bruno.weather.data.local.model.CacheSearchEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Insert(entity = CacheSearchEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheSearch(cacheSearchEntity: CacheSearchEntity): Long
    @Delete
    suspend fun deleteCacheSearch(cacheSearchEntity: CacheSearchEntity): Int
    @Query("SELECT * FROM CacheSearchEntity")
    fun getCacheSearch(): Flow<List<CacheSearchEntity>>
}