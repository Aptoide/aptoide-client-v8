package cm.aptoide.pt.aptoide_installer.model

data class DownloadFile(
  val md5: String,
  val path: String,
  val altPath: String,
  val packageName: String,
  val versionCode: Int,
  val versionName: String,
  val fileName: String,
  val fileType: FileType,
  val subFileType: SubFileType,
  val cachePath: String
)

enum class FileType {
  APK, OBB, SPLIT
}

enum class SubFileType {
  MAIN, PATCH, ASSET, FEATURE, BASE, SUBTYPE_APK
}