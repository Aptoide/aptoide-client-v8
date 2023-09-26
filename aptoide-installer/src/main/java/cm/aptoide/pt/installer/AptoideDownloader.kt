package cm.aptoide.pt.installer

import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.installer.network.DownloaderRepository
import cm.aptoide.pt.installer.platform.InstallPermissions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class AptoideDownloader @Inject constructor(
  private val downloaderRepository: DownloaderRepository,
  private val installPermissions: InstallPermissions,
) : PackageDownloader {
  private val downloadsInProgress = mutableSetOf<String>()

  @Suppress("OPT_IN_USAGE")
  override suspend fun download(
    packageName: String,
    forceDownload: Boolean,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> = installPackageInfo.run {
    installPermissions.checkIfCanWriteExternal()
    installPermissions.checkIfCanInstall()
    val totalSize = installationFiles.sumOf { it.fileSize }
    var totalProgress = 0.0
    installationFiles.asFlow()
      .flatMapMerge(concurrency = 3) { item ->
        var progress = 0.0
        downloaderRepository.download(packageName, versionCode, item)
          .map {
            val diff = it - progress
            progress = it
            diff * item.fileSize / totalSize
          }
      }.map {
        if (!downloadsInProgress.contains(packageName)) throw CancellationException()
        totalProgress += it
        (totalProgress * 100).toInt()
      }
  }.distinctUntilChanged()
    .onCompletion { downloadsInProgress.remove(packageName) }
    .also { downloadsInProgress += packageName }

  override fun cancel(packageName: String) = downloadsInProgress.remove(packageName)
}
