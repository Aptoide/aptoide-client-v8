package cm.aptoide.pt.campaigns.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
  entities = [PaEAppEntity::class],
  version = 1,
  exportSchema = true
)
internal abstract class PaECampaignsDatabase : RoomDatabase() {
  abstract fun paeAppsDao(): PaEAppsDao
}
