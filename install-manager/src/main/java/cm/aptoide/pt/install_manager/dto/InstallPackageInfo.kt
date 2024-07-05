package cm.aptoide.pt.install_manager.dto

/**
 * This class represents an app package info.
 */
data class InstallPackageInfo(
  val versionCode: Long,
  val installationFiles: Set<InstallationFile> = emptySet(),
  val payload: String? = null,
) {
  val filesSize = installationFiles.sumOf { it.fileSize }
}
