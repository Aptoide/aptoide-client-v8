package cm.aptoide.pt.install_manager.dto

data class InstallationFile(
  val name: String,
  val type: Type,
  val md5: String,
  val fileSize: Long,
  val url: String,
  val altUrl: String,
  val localPath: String,
) {

  /**
   * Types of package installation files
   * Sorted in order of installation priority
   */
  @Suppress("unused")
  enum class Type(val extension: String) {
    BASE(".apk"),
    OBB_MAIN(".obb"),
    OBB_PATCH(".obb"),
    PFD_INSTALL_TIME(".apk"),
    PFD_ON_DEMAND(".apk"),
    PFD_CONDITIONAL(".apk"),
    PFD_INSTANT(".apk"),
    PAD_INSTALL_TIME(".apk"),
    PAD_FAST_FOLLOW(".apk"),
    PAD_ON_DEMAND(".apk"),
  }
}
