package cm.aptoide.pt.installer

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller.PACKAGE_SOURCE_STORE
import android.content.pm.PackageInstaller.Session
import android.content.pm.PackageInstaller.SessionParams.USER_ACTION_NOT_REQUIRED
import android.net.Uri
import android.os.Build
import android.os.Environment
import cm.aptoide.pt.extensions.checkMd5
import cm.aptoide.pt.install_manager.AbortException
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import cm.aptoide.pt.installer.di.DownloadsPath
import cm.aptoide.pt.installer.platform.INSTALL_SESSION_API_COMPLETE_ACTION
import cm.aptoide.pt.installer.platform.InstallEvents
import cm.aptoide.pt.installer.platform.InstallPermissions
import cm.aptoide.pt.installer.platform.InstallResult
import cm.aptoide.pt.installer.platform.copyWithProgressTo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideInstaller @Inject constructor(
  @ApplicationContext private val context: Context,
  @DownloadsPath private val downloadsPath: File,
  private val installEvents: InstallEvents,
  private val installPermissions: InstallPermissions,
) : PackageInstaller {

  init {
    // Clean up all old sessions on app start
    context.packageManager.packageInstaller.mySessions.forEach {
      context.packageManager.packageInstaller.abandonSession(it.sessionId)
    }
  }

  override fun install(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> = flow {
    emit(0)
    installPermissions.checkIfCanWriteExternal()
    installPermissions.checkIfCanInstall()
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
          val checkFraction = 49
          val (apkFiles, obbFiles) = installPackageInfo.getCheckedFiles {
            emit((it * checkFraction).toInt())
          }
          val totalApkSize = apkFiles.totalLength
          val totalObbSize = obbFiles.totalLength
          val apkFraction = 49.0 * totalApkSize / (totalApkSize + totalObbSize)
          val obbFraction = 49.0 * totalObbSize / (totalApkSize + totalObbSize)
          obbFiles.moveToObbStore(packageName) {
            emit(checkFraction + (obbFraction * it / totalObbSize).toInt())
          }
          loadFiles(apkFiles) {
            emit(checkFraction + obbFraction.toInt() + (apkFraction * it / totalApkSize).toInt())
          }
          commit(
            PendingIntent
              .getBroadcast(
                context,
                SESSION_INSTALL_REQUEST_CODE,
                Intent(INSTALL_SESSION_API_COMPLETE_ACTION),
                // This is essential to be like that for having extras in the intent
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
              )
              .intentSender
          )
          emit(99)
          when (val result = installEvents.events.filter { it.sessionId == sessionId }.first()) {
            is InstallResult.Fail -> throw Exception(result.message)
            is InstallResult.Abort -> throw AbortException(result.message)
            is InstallResult.Success -> emit(100)
          }
        }
          .onFailure { abandon() }
          .getOrThrow()
      }
    }
  }
    .distinctUntilChanged()

  override fun uninstall(packageName: String): Flow<Int> = flow {
    emit(0)
    val intent = Intent(Intent.ACTION_DELETE)
    intent.data = Uri.parse("package:$packageName")
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
    emit(99)
  }

  override fun cancel(packageName: String) = true

  private suspend fun InstallPackageInfo.getCheckedFiles(
    progress: suspend (Double) -> Unit,
  ): Pair<List<File>, List<File>> = installationFiles.run {
    val apks = mutableListOf<File>()
    val obbs = mutableListOf<File>()
    forEachIndexed { index, value ->
      if (value.type in listOf(InstallationFile.Type.OBB_MAIN, InstallationFile.Type.OBB_PATCH)) {
        obbs.add(value.toCheckedFile())
      } else {
        apks.add(value.toCheckedFile())
      }
      progress(index + 1.0 / size)
    }
    apks to obbs
  }

  private fun InstallationFile.toCheckedFile(): File =
    File(downloadsPath, name)
      .takeIf { it.checkMd5(md5) }
      ?: throw IllegalStateException("MD5 check failed: File $name is corrupt")

  private val Collection<File>.totalLength
    get() = map(File::length).reduceOrNull { acc, l -> acc + l } ?: 0

  private suspend fun Collection<File>.moveToObbStore(
    packageName: String,
    progress: suspend (Long) -> Unit,
  ) {
    val outputPath = "$OBB_FOLDER$packageName/"
    val prepared = File(outputPath).run {
      deleteRecursively()
      mkdirs()
    }
    if (!prepared) throw IllegalStateException("Can't create OBB folder: $outputPath")

    var processedSize: Long = 0
    forEach { file ->
      val size = file.length()
      val destinationFile = File(outputPath + file.name)
      // Try to move first
      file.renameTo(destinationFile)
        .takeUnless { it }
        ?.also {
          file.inputStream().use { inputStream ->
            destinationFile.createNewFile()
            destinationFile.outputStream().use { outputStream ->
              inputStream
                .copyWithProgressTo(outputStream)
                .collect {
                  progress(processedSize + it)
                }
            }
          }
        }
      processedSize += size
    }
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
    private val OBB_FOLDER =
      Environment.getExternalStorageDirectory().absolutePath + "/Android/obb/"
  }
}
