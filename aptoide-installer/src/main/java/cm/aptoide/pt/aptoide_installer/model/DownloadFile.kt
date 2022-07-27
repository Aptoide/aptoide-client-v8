package cm.aptoide.pt.aptoide_installer.model

data class DownloadFile(
  val md5: String,
  val path: String,
  val altPath: String,
  val packageName: String
)