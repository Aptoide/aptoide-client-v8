package cm.aptoide.pt.install_manager.dto

/**
 * This class represents an app package info.
 */
data class InstallPackageInfo(
  val version: Version,
  val downloadSize: Long = Long.MIN_VALUE,
  val installationFiles: Set<InstallationFile> = emptySet(),
)

