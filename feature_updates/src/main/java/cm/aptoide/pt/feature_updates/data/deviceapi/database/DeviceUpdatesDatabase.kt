package cm.aptoide.pt.feature_updates.data.deviceapi.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [DeviceAppUpdate::class])
abstract class DeviceUpdatesDatabase : RoomDatabase() {
  abstract fun deviceAppUpdateDao(): DeviceAppUpdateDao
}
