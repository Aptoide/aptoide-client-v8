package cm.aptoide.pt.installer

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller.PACKAGE_SOURCE_STORE
import android.content.pm.PackageInstaller.Session
import android.os.Build
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import cm.aptoide.pt.installer.di.DownloadsPath
import cm.aptoide.pt.installer.platform.INSTALL_SESSION_API_COMPLETE_ACTION
import cm.aptoide.pt.installer.platform.InstallEvents
import cm.aptoide.pt.installer.platform.InstallResult
import cm.aptoide.pt.installer.platform.checkMd5
import cm.aptoide.pt.installer.platform.copyWithProgressTo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideInstaller @Inject constructor(
  @ApplicationContext private val context: Context,
  @DownloadsPath private val downloadsPath: File,
  private val installEvents: InstallEvents,
) : PackageInstaller {

  init {
    // Clean up all old sessions on app start
    context.packageManager.packageInstaller.mySessions.forEach {
      context.packageManager.packageInstaller.abandonSession(it.sessionId)
    }
  }

  override suspend fun install(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ): Flow<Int> = flow {
    emit(0)
    context.packageManager.packageInstaller.run {
      val sessionId = createSession(
        android.content.pm.PackageInstaller
          .SessionParams(android.content.pm.PackageInstaller.SessionParams.MODE_FULL_INSTALL)
          .apply {
            setAppPackageName(packageName)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              setPackageSource(PACKAGE_SOURCE_STORE)
            }
          }
      )
      openSession(sessionId).run {
        runCatching {
          val apkFilesMap = installPackageInfo.apkFiles
          apkFilesMap.checkMd5 {
            emit((it * 49.0 / apkFilesMap.size).toInt())
          }
          val apkFiles = apkFilesMap.values
          val totalSize = apkFiles.totalLength
          loadFiles(apkFiles) {
            emit(49 + (it * 49.0 / totalSize).toInt())
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
            is InstallResult.Cancel -> throw CancellationException(result.message)
            is InstallResult.Success -> emit(100)
          }
        }
          .onFailure { abandon() }
          .getOrThrow()
      }
    }
  }
    .distinctUntilChanged()

  override suspend fun uninstall(packageName: String): Flow<Int> =
    throw NotImplementedError("An operation is not implemented: Not supported")

  override fun cancel(packageName: String) = true

  private val InstallPackageInfo.apkFiles: Map<String, File>
    get() = installationFiles
      .filter {
        it.type !in listOf(InstallationFile.Type.OBB_MAIN, InstallationFile.Type.OBB_MAIN)
      }
      .associate { it.md5 to File(downloadsPath, it.name) }

  private suspend fun Map<String, File>.checkMd5(progress: suspend (Int) -> Unit) =
    entries.forEachIndexed { index, (md5, file) ->
      if (file.checkMd5(md5)) {
        progress(index + 1)
      } else {
        throw IllegalStateException("MD5 check failed: File ${file.name} is corrupt")
      }
    }

  private val Collection<File>.totalLength get() = map { it.length() }.reduce { acc, l -> acc + l }

  private suspend fun Session.loadFiles(
    files: Collection<File>,
    progress: suspend (Long) -> Unit
  ) {
    var processedSize: Long = 0
    files.forEach { file ->
      val size = file.length()
      FileInputStream(file)
        .use { apkStream ->
          openWrite(file.name, 0, size)
            .use { sessionStream ->
              apkStream
                .copyWithProgressTo(sessionStream)
                .collect {
                  progress(processedSize + it)
                }
              processedSize += size
              fsync(sessionStream)
            }
        }
    }
  }

  companion object {
    private const val SESSION_INSTALL_REQUEST_CODE = 18
  }
}
