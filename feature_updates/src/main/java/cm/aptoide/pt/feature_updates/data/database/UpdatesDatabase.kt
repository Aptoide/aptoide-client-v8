package cm.aptoide.pt.feature_updates.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [AppUpdateData::class])
abstract class UpdatesDatabase : RoomDatabase() {
  abstract fun appUpdateDao(): AppUpdateDao
}
