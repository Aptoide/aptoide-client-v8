package cm.aptoide.pt.installedapps.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cm.aptoide.pt.installedapps.data.database.model.InstalledAppEntity

@Database(entities = [InstalledAppEntity::class], version = 1, exportSchema = false)
abstract class InstalledAppsDatabase : RoomDatabase() {

  abstract fun installedAppsDao(): InstalledAppsDao
}