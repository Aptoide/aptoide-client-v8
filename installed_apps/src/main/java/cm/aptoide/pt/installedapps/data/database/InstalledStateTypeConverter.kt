package cm.aptoide.pt.installedapps.data.database

import androidx.room.TypeConverter
import cm.aptoide.pt.installedapps.data.database.model.InstalledState

class InstalledStateTypeConverter {

  @TypeConverter
  fun toInstalledState(value: Int) = enumValues<InstalledState>()[value]

  @TypeConverter
  fun fromInstalledState(value: InstalledState) = value.state
}