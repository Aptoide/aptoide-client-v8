package cm.aptoide.pt.feature_updates.data.deviceapi.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceAppUpdateDao {

  @Query("SELECT * FROM DeviceAppUpdate")
  fun getAll(): Flow<List<DeviceAppUpdate>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun save(updates: List<DeviceAppUpdate>)

  @Query("DELETE FROM DeviceAppUpdate WHERE packageName IN (:packageNames)")
  suspend fun remove(packageNames: List<String>)
}
