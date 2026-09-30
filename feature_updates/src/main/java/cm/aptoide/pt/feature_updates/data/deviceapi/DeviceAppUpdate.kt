package cm.aptoide.pt.feature_updates.data.deviceapi

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

/**
 * An update kept as the device API described it. Its own table, apart from the v7 one, so
 * neither format is ever read as the other and the other builds keep their data untouched.
 */
@Entity(tableName = "DeviceAppUpdate")
data class DeviceAppUpdate(
  @PrimaryKey val packageName: String,
  val versionCode: Int,
  val data: String,
)

@Dao
interface DeviceAppUpdateDao {

  @Query("SELECT * FROM DeviceAppUpdate")
  fun getAll(): Flow<List<DeviceAppUpdate>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun save(updates: List<DeviceAppUpdate>)

  @Query("DELETE FROM DeviceAppUpdate WHERE packageName IN (:packageNames)")
  suspend fun remove(packageNames: List<String>)
}

@Database(version = 1, entities = [DeviceAppUpdate::class])
abstract class DeviceUpdatesDatabase : RoomDatabase() {
  abstract fun deviceAppUpdateDao(): DeviceAppUpdateDao
}
