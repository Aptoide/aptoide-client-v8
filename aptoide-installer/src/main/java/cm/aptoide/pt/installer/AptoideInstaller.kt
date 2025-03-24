package cm.aptoide.pt.installer

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller.PACKAGE_SOURCE_STORE
import android.content.pm.PackageInstaller.Session
import android.content.pm.PackageInstaller.SessionParams.USER_ACTION_NOT_REQUIRED
import android.os.Build
import cm.aptoide.pt.install_manager.AbortException
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile
import cm.aptoide.pt.install_manager.dto.hasObb
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import cm.aptoide.pt.installer.di.DownloadsPath
import cm.aptoide.pt.installer.obb.ObbService
import cm.aptoide.pt.installer.obb.installOBBs
import cm.aptoide.pt.installer.obb.removeObbFromStore
import cm.aptoide.pt.installer.platform.INSTALL_SESSION_API_COMPLETE_ACTION
import cm.aptoide.pt.installer.platform.InstallEvents
import cm.aptoide.pt.installer.platform.InstallPermissions
import cm.aptoide.pt.installer.platform.InstallResult
import cm.aptoide.pt.installer.platform.UNINSTALL_API_COMPLETE_ACTION
import cm.aptoide.pt.installer.platform.UninstallEvents
import cm.aptoide.pt.installer.platform.UninstallResult
import cm.aptoide.pt.installer.platform.copyWithProgressTo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class AptoideInstaller @Inject constructor(
  @ApplicationContext private val context: Context,
  @DownloadsPath private val downloadsPath: File,
  private val installEvents: InstallEvents,
  private val uninstallEvents: UninstallEvents,
  private val installPermissions: InstallPermissions,
) : PackageInstaller {
  private val initialPermissionsAllowed = context.getPermissionsState()

  init {
    // Clean up all old sessions on app start
    context.packageManager.packageInstaller.mySessions.forEach {
      try {
        // Some times it leads to crashes that are hard to reproduce.
        context.packageManager.packageInstaller.abandonSession(it.sessionId)
      } catch (t: Throwable) {
        Timber.e(t)
      }
    }
  }

  override fun install(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> = flow {
    emit(0)
    if (installPackageInfo.hasObb()) {
      installPermissions.checkIfCanWriteExternal()
    }
    installPermissions.checkIfCanInstall()

    val filesDir = File(downloadsPath, packageName)

    if (!filesDir.exists()) throw IllegalStateException("Necessary files do not exist for app $packageName")

    val checkFraction = 49
    val (apkFiles, obbFiles) = installPackageInfo.getCheckedFiles(filesDir) {
      emit((it * checkFraction).toInt())
    }
    val totalApkSize = apkFiles.totalLength
    val totalObbSize = obbFiles.totalLength
    val apkFraction = 49.0 * totalApkSize / (totalApkSize + totalObbSize)
    val obbFraction = 49.0 * totalObbSize / (totalApkSize + totalObbSize)
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

    context.packageManager.packageInstaller.run {
      val sessionId = createSession(
        android.content.pm.PackageInstaller
          .SessionParams(android.content.pm.PackageInstaller.SessionParams.MODE_FULL_INSTALL)
          .apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
              setRequestUpdateOwnership(true)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
              setRequireUserAction(USER_ACTION_NOT_REQUIRED)
            }
            setAppPackageName(packageName)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              setPackageSource(PACKAGE_SOURCE_STORE)
            }
          }
      )
      openSession(sessionId).run {
        runCatching {
          loadFiles(apkFiles) {
            emit(checkFraction + obbFraction.toInt() + (apkFraction * it / totalApkSize).toInt())
          }
          commit(
            PendingIntent
              .getBroadcast(
                context,
                SESSION_INSTALL_REQUEST_CODE,
                Intent(INSTALL_SESSION_API_COMPLETE_ACTION)
                  .setPackage(context.packageName)
                  .putExtra("${context.packageName}.pn", packageName)
                  .putExtra("${context.packageName}.ap", installPackageInfo.payload),
                // This is essential to be like that for having extras in the intent
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                  PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                } else {
                  PendingIntent.FLAG_UPDATE_CURRENT
                }
              )
              .intentSender
          )
          emit(99)
          when (val result = installEvents.events.filter { it.sessionId == sessionId }.first()) {
            is InstallResult.Fail -> throw Exception(result.message)
            is InstallResult.Abort -> throw AbortException(result.message)
            is InstallResult.Success -> {
              emit(100)
              filesDir.deleteRecursively()
              obbFiles.deleteFromCache()
            }
          }
        }
          .onFailure {
            abandon()
            if (totalObbSize > 0) removeObbFromStore(packageName)
          }
          .getOrThrow()
      }
    }
  }
    .distinctUntilChanged()

  override fun uninstall(packageName: String): Flow<Int> = flow {
    emit(0)
    val id = Random.nextInt()

    val intentSender = PendingIntent
      .getBroadcast(
        context,
        UNINSTALL_REQUEST_CODE,
        Intent(UNINSTALL_API_COMPLETE_ACTION)
          .putExtra("${context.packageName}.uninstall_id", id)
          .setPackage(context.packageName),
        // This is essential to be like that for having extras in the intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
          PendingIntent.FLAG_UPDATE_CURRENT
        }
      )
      .intentSender

    context.packageManager.packageInstaller.uninstall(packageName, intentSender)
    emit(99)

    when (val result = uninstallEvents.events.filter { it.id == id }.first()) {
      is UninstallResult.Fail -> throw Exception(result.message)
      is UninstallResult.Abort -> throw AbortException(result.message)
      is UninstallResult.Success -> emit(100)
    }
  }

  private suspend fun InstallPackageInfo.getCheckedFiles(
    downloadsDir: File,
    progress: suspend (Double) -> Unit,
  ): Pair<List<File>, List<File>> = installationFiles.run {
    val apks = mutableListOf<File>()
    val obbs = mutableListOf<File>()
    forEachIndexed { index, value ->
      when (value.type) {
        InstallationFile.Type.BASE,
        InstallationFile.Type.PFD_INSTALL_TIME,
        InstallationFile.Type.PAD_INSTALL_TIME,
          -> apks.add(value.toCheckedFile(downloadsDir))

        InstallationFile.Type.OBB_MAIN,
        InstallationFile.Type.OBB_PATCH,
          -> obbs.add(value.toCheckedFile(downloadsDir))

        else -> {}
      }

      progress(index + 1.0 / size)
    }
    apks to obbs
  }

  private suspend fun Session.loadFiles(
    files: Collection<File>,
    progress: suspend (Long) -> Unit,
  ) {
    var processedSize: Long = 0
    files.forEach { file ->
      val size = file.length()
      file.inputStream()
        .use { apkStream ->
          openWrite(file.name, 0, size)
            .use { sessionStream ->
              apkStream
                .copyWithProgressTo(sessionStream)
                .collect {
                  progress(processedSize + it)
                }
              fsync(sessionStream)
            }
        }
      processedSize += size
    }
  }

  companion object {
    private const val SESSION_INSTALL_REQUEST_CODE = 18
    private const val UNINSTALL_REQUEST_CODE = 19
  }
}
