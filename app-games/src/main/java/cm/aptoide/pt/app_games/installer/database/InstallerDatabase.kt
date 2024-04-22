package cm.aptoide.pt.app_games.installer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cm.aptoide.pt.app_games.installer.database.model.AppDetailsData

@Database(
  version = 1,
  entities = [AppDetailsData::class]
)

abstract class InstallerDatabase : RoomDatabase() {
  abstract fun appInfoDao(): AppDetailsDao

}
