package cm.aptoide.pt.installedapps.domain.model

data class InstalledApp(
  val appName: String,
  val packageName: String,
  val versionName: String,
  val versionCode: Int,
  val appIcon: String,
  val installedAppState: InstalledAppState
)

enum class InstalledAppState {
  DOWNLOADING, INSTALLED, INSTALLING, NOT_INSTALLED
}