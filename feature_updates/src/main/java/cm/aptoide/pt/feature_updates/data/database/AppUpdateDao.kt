package cm.aptoide.pt.feature_updates.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUpdateDao {

  @Query("SELECT * from AppUpdate")
  fun getAll(): Flow<List<AppUpdateData>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun save(data: List<AppUpdateData>)

  @Delete
  suspend fun remove(vararg data: AppUpdateData)

  @Query("DELETE FROM AppUpdate")
  suspend fun clear()
}
