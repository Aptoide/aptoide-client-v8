package cm.aptoide.pt.installedapps.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cm.aptoide.pt.installedapps.data.database.model.InstalledAppEntity

@Database(entities = [InstalledAppEntity::class], version = 1, exportSchema = false)
@TypeConverters(
  InstalledStateTypeConverter::class
)
abstract class InstalledAppsDatabase : RoomDatabase() {

  abstract fun installedAppsDao(): InstalledAppsDao
}