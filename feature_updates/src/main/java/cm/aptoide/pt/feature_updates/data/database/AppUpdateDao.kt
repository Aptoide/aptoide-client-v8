package cm.aptoide.pt.feature_updates.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppUpdateDao {

  @Query("SELECT * from AppUpdate")
  suspend fun getAll(): List<AppUpdateData>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun save(data: List<AppUpdateData>)

  @Query("DELETE FROM AppUpdate")
  suspend fun clear()
}
