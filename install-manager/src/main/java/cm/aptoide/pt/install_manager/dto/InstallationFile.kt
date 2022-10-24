package cm.aptoide.pt.install_manager.dto

data class InstallationFile(
  val name: String,
  val type: Type,
  val md5: String,
  val url: String,
  val altUrl: String,
  val localPath: String
) {

  /**
   * Types of package installation files
   * Sorted in order of installation priority
   */
  @Suppress("unused")
  enum class Type {
    BASE,
    OBB_MAIN,
    OBB_PATCH,
    PFD_INSTALL_TIME,
    PFD_CONDITIONAL,
    PAD_INSTALL_TIME,
    PAD_FAST_FOLLOW,
    PFD_INSTANT,
    PFD_ON_DEMAND,
    PAD_ON_DEMAND,
  }
}