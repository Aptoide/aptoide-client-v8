package cm.aptoide.pt.installer

import android.content.Context
import cm.aptoide.pt.install_manager.DownloadInfo
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.hasObb
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.installer.network.DownloaderRepository
import cm.aptoide.pt.installer.platform.InstallPermissions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class AptoideDownloader @Inject constructor(
  @ApplicationContext private val context: Context,
  private val downloaderRepository: DownloaderRepository,
  private val installPermissions: InstallPermissions,
) : PackageDownloader {

  @Suppress("OPT_IN_USAGE")
  override fun download(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<DownloadInfo> = installPackageInfo.run {
    var totalProgress = 0.0
    var totalDownloadedBytes = 0L

    installationFiles.asFlow()
      .onStart {
        if (installPackageInfo.hasObb()) {
          installPermissions.checkIfCanWriteExternal()
        }
      }
      .flatMapMerge(concurrency = 5) { item ->
        var fileProgress = 0.0
        var fileDownloadedBytes = 0L
        downloaderRepository.download(packageName, versionCode, item)
          .map { (progress, downloadedBytes) ->
            val progressDiff = progress - fileProgress
            fileProgress = progress

            val bytesDiff = downloadedBytes - fileDownloadedBytes
            fileDownloadedBytes = downloadedBytes

            (progressDiff * item.fileSize / filesSize) to bytesDiff
          }
      }.map { (progress, downloadedBytes) ->
        coroutineContext.ensureActive()
        totalProgress += progress
        totalDownloadedBytes += downloadedBytes

        DownloadInfo(
          progress = (totalProgress * 100).toInt(),
          downloadedBytes = totalDownloadedBytes
        )
      }
  }.distinctUntilChanged()
    .also { InstallerWorker.enqueue(context) }
}
