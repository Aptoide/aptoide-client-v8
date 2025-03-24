package cm.aptoide.pt.installer

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile
import cm.aptoide.pt.install_manager.dto.hasObb
import cm.aptoide.pt.install_manager.dto.hasSplitApks
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import cm.aptoide.pt.installer.di.DownloadsPath
import cm.aptoide.pt.installer.obb.ObbService
import cm.aptoide.pt.installer.obb.installOBBs
import cm.aptoide.pt.installer.platform.InstallPermissions
import cm.aptoide.pt.installer.platform.UserActionLauncher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("DEPRECATION")
@Singleton
class LegacyInstaller @Inject constructor(
  @ApplicationContext private val context: Context,
  @DownloadsPath private val downloadsPath: File,
  private val installPermissions: InstallPermissions,
  private val userActionLauncher: UserActionLauncher,
) : PackageInstaller {
  private val initialPermissionsAllowed = context.getPermissionsState()

  override fun install(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ): Flow<Int> = flow {
    emit(0)
    if (installPackageInfo.hasObb()) {
      installPermissions.checkIfCanWriteExternal()
    }
    installPermissions.checkIfCanInstall()

    if (installPackageInfo.hasSplitApks()) {
      throw IllegalStateException("Can't install split apks with the legacy installer")
    }

    val filesDir = File(downloadsPath, packageName)
    if (!filesDir.exists()) throw IllegalStateException("Necessary file does not exist for app $packageName")

    val checkFraction = 49
    val (apk, obbFiles) = installPackageInfo.getCheckedFiles(filesDir) {
      emit((it * checkFraction).toInt())
    }

    if (apk == null) {
      throw IllegalStateException("Missing base apk")
    }

    val apkSize = apk.length()
    val totalObbSize = obbFiles.totalLength
    val obbFraction = 49.0 * totalObbSize / (apkSize + totalObbSize)

    if (totalObbSize > 0) {
      if (initialPermissionsAllowed) {
        obbFiles.installOBBs(packageName) {
          emit(checkFraction + (obbFraction * it / totalObbSize).toInt())
        }
      } else {
        ObbService.bindServiceAndWaitForResult(
          context = context,
          packageName = packageName,
          obbFilePaths = obbFiles.map { it.absolutePath }
        ).let { movedOBBFiles ->
          if (movedOBBFiles) {
            //TODO: improve installation progress when using OBBService
            emit(checkFraction + (obbFraction / totalObbSize).toInt())
          } else {
            throw IllegalStateException("Error moving OBB files")
          }
        }
      }
    }

    val installIntent = Intent(Intent.ACTION_INSTALL_PACKAGE)
      .putExtra(Intent.EXTRA_RETURN_RESULT, true)
      .putExtra(
        Intent.EXTRA_INSTALLER_PACKAGE_NAME,
        context.applicationContext.packageName
      ).putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
      .putExtra("${context.packageName}.pn", packageName)
      .putExtra("${context.packageName}.ap", installPackageInfo.payload)

    installIntent.data = FileProvider.getUriForFile(
      context,
      "${context.applicationContext.packageName}.fileProvider",
      apk
    )

    installIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

    emit(99)
    val installed = userActionLauncher.launchIntent(installIntent)

    if (installed) {
      emit(100)
      filesDir.deleteRecursively()
      obbFiles.deleteFromCache()
    } else {
      throw Exception("Failed to install with legacy installer")
    }
  }

  override fun uninstall(packageName: String): Flow<Int> = flow {
    emit(0)
    val intent = Intent(Intent.ACTION_DELETE)
      .putExtra(Intent.EXTRA_RETURN_RESULT, true)
    intent.data = "package:$packageName".toUri()

    emit(99)
    val uninstalled = userActionLauncher.launchIntent(intent)

    if (uninstalled) {
      emit(100)
    } else {
      throw Exception("Failed to uninstall with legacy installer")
    }
  }

  private suspend fun InstallPackageInfo.getCheckedFiles(
    downloadsDir: File,
    progress: suspend (Double) -> Unit,
  ): Pair<File?, List<File>> = installationFiles.run {
    var apk: File? = null
    val obbs = mutableListOf<File>()
    forEachIndexed { index, value ->
      when (value.type) {
        InstallationFile.Type.BASE,
          -> apk = value.toCheckedFile(downloadsDir)

        InstallationFile.Type.OBB_MAIN,
        InstallationFile.Type.OBB_PATCH,
          -> obbs.add(value.toCheckedFile(downloadsDir))

        else -> {}
      }

      progress(index + 1.0 / size)
    }
    apk to obbs
  }
}
