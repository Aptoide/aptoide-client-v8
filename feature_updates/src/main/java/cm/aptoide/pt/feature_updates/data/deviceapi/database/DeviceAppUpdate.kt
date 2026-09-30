package cm.aptoide.pt.feature_updates.data.deviceapi.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * An update kept as the device API described it. Its own table, apart from the v7 one, so
 * neither format is ever read as the other and the other builds keep their data untouched.
 */
@Entity(tableName = "DeviceAppUpdate")
data class DeviceAppUpdate(
  @PrimaryKey val packageName: String,
  val versionCode: Int,
  val data: String,
)
