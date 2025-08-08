package al.bruno.weather.data.local.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["query"], unique = true)]
)
data class CacheSearchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val query: String
)