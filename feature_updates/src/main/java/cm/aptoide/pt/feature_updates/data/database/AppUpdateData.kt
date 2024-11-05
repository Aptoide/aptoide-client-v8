package cm.aptoide.pt.feature_updates.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AppUpdate")
data class AppUpdateData(
  @PrimaryKey val packageName: String,
  val versionCode: Int,
  val data: String,
)
