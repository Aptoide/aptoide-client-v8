package cm.aptoide.pt.aptoide_installer.model

data class Download(
  val appName: String,
  val packageName: String,
  val md5: String,
  val icon: String,
  val versionName: String,
  val versionCode: Int,
  val hasAppc: Boolean,
  val appSize: Long,
  val downloadState: DownloadState,
  val progress: Int,
  val downloadFileList: List<DownloadFile>,
  val action: DownloadAction,
  val trustedBadge: String,
  val storeName: String
)

enum class DownloadState {
  INSTALL, PROCESSING, DOWNLOADING, INSTALLING, INSTALLED, ERROR, READY_TO_INSTALL
}

enum class DownloadAction {
  INSTALL, UPDATE, DOWNGRADE
}