package cm.aptoide.pt.installer

import android.content.Context
import android.os.Build
import cm.aptoide.pt.extensions.checkMd5
import cm.aptoide.pt.extensions.hasPackageInstallsPermission
import cm.aptoide.pt.extensions.hasWriteExternalStoragePermission
import cm.aptoide.pt.install_manager.dto.InstallationFile
import java.io.File

internal fun InstallationFile.toCheckedFile(parentDir: File): File =
  File(parentDir, name)
    .takeIf { it.checkMd5(md5) }
    ?: throw IllegalStateException("MD5 check failed: File $name is corrupt")

internal val Collection<File>.totalLength
  get() = map(File::length).reduceOrNull { acc, l -> acc + l } ?: 0

internal fun Collection<File>.deleteFromCache() = forEach(File::delete)

internal fun Context.getPermissionsState() =
  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
    hasPackageInstallsPermission() && hasWriteExternalStoragePermission()
  } else {
    hasPackageInstallsPermission()
  }
