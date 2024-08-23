package cm.aptoide.pt.installer

import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.hasObb
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.installer.network.DownloaderRepository
import cm.aptoide.pt.installer.platform.InstallPermissions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class AptoideDownloader @Inject constructor(
  private val downloaderRepository: DownloaderRepository,
  private val installPermissions: InstallPermissions,
) : PackageDownloader {
  private val downloadsInProgress = mutableSetOf<String>()

  @Suppress("OPT_IN_USAGE")
  override fun download(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> = installPackageInfo.run {
    var totalProgress = 0.0
    installationFiles.asFlow()
      .onStart {
        if(installPackageInfo.hasObb()) {
          installPermissions.checkIfCanWriteExternal()
        }
        installPermissions.checkIfCanInstall()
      }
      .flatMapMerge(concurrency = 3) { item ->
        var progress = 0.0
        downloaderRepository.download(packageName, versionCode, item)
          .map {
            val diff = it - progress
            progress = it
            diff * item.fileSize / filesSize
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
