package cm.aptoide.pt.aptoide_installer

import cm.aptoide.pt.aptoide_installer.model.Download
import cm.aptoide.pt.aptoide_installer.model.DownloadAction
import cm.aptoide.pt.aptoide_installer.model.DownloadFileMapper
import cm.aptoide.pt.aptoide_installer.model.DownloadStateMapper
import cm.aptoide.pt.downloadmanager.DownloadManager
import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.installedapps.data.InstalledAppsRepository
import cm.aptoide.pt.installedapps.domain.model.InstalledApp
import cm.aptoide.pt.packageinstaller.AppInstaller
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.rx2.asFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideInstallManager @Inject constructor(
  var downloadManager: DownloadManager,
  var downloadStateMapper: DownloadStateMapper,
  val downloadFileMapper: DownloadFileMapper,
  var installedAppsRepository: InstalledAppsRepository,
) : InstallManager {

  override fun start() {
    downloadManager.start()
    dispatchInstalls()
  }

  override fun download(download: Download) {
    //downloadManager.getDownloadAsSingle()
    //downloadManager.startDownload()
    //installer.install()
  }

  override fun install(packageName: String) {
    //todo call installer to install the app
  }

  override fun getDownload(app: App): Flow<Download> {
    // TODO: in case the download does not exist, create one
    return downloadManager.getDownloadAsObservable(app.packageName).asFlow().combine(
      installedAppsRepository.getInstalledApp(123, app.packageName)
    ) { downloadEntity: DownloadEntity?, installedApp: InstalledApp ->
      Download(
        app.name,
        app.packageName,
        "md5",
        app.icon,
        app.versionName,
        123,
        app.isAppCoins,
        app.appSize,
        downloadStateMapper.mapDownloadState(
          downloadEntity!!.overallDownloadStatus,
          installedApp.installedAppState
        ),
        downloadEntity.overallProgress,
        downloadFileMapper.mapDownloadFileList(downloadEntity.filesToDownload),
        DownloadAction.INSTALL
      )
    }.flowOn(Dispatchers.IO)
  }


  override fun cancelDownload(packageName: String) {
    downloadManager.removeDownload(packageName)
  }

  override fun getActiveDownloads(packageName: String) {
    downloadManager.currentActiveDownloads
  }

  private fun dispatchInstalls() {

  }
}