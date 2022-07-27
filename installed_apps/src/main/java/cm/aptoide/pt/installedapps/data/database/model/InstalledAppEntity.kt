package cm.aptoide.pt.installedapps.data.database.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "InstalledApps")
data class InstalledAppEntity(
  @PrimaryKey @NonNull val packageName: String,
  @NonNull val appName: String,
  @NonNull val appVersion: String,
  @NonNull val versionCode: Int,
  @NonNull val appIcon: String,
  @NonNull val installedState: InstalledState
)

enum class InstalledState {
  DOWNLOADING, INSTALLED, INSTALLING, NOT_INSTALLED
}
