package cm.aptoide.pt.campaigns.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import cm.aptoide.pt.campaigns.data.database.model.PaEMissionEntity

@Dao
interface PaeMissionDao {

  @Query("SELECT * FROM pae_missions WHERE packageName = :packageName")
  suspend fun getAppMissions(packageName: String): List<PaEMissionEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(missions: List<PaEMissionEntity>)

  @Query("DELETE FROM pae_missions WHERE packageName = :packageName")
  suspend fun clearAppMissions(packageName: String)

  @Transaction
  suspend fun replaceAppMissions(packageName: String, missions: List<PaEMissionEntity>) {
    clearAppMissions(packageName)
    insertAll(missions)
  }
}
