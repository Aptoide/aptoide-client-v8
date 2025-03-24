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

fun InstallPackageInfo.hasObb() =
  installationFiles.find { it.type == InstallationFile.Type.OBB_MAIN } != null

fun InstallPackageInfo.hasSplitApks() =
  installationFiles.count { it.type == InstallationFile.Type.BASE } > 1 ||
    installationFiles.any { it.type.name.contains("PAD") || it.type.name.contains("PFD") }
