package cm.aptoide.pt.campaigns.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PaEAppsDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(apps: List<PaEAppEntity>)

  @Query("SELECT packageName FROM pae_apps")
  fun observeAvailablePaEPackageNames(): Flow<List<String>>

  @Query("SELECT packageName FROM pae_apps")
  suspend fun getAvailablePaEPackageNames(): List<String>

  @Query("DELETE FROM pae_apps")
  suspend fun clearAll()

  @Transaction
  suspend fun replaceAll(apps: List<PaEAppEntity>) {
    clearAll()
    insertAll(apps)
  }
}
