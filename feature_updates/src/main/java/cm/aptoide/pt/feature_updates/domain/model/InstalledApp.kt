package cm.aptoide.pt.feature_updates.domain.model

data class InstalledApp(
  val appName: String,
  val packageName: String,
  val versionCode: String,
  val imagePath: String
)