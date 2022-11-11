package cm.aptoide.pt.aptoide_installer

import cm.aptoide.pt.aptoide_installer.model.*
import cm.aptoide.pt.downloadmanager.DownloadManager
import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity
import cm.aptoide.pt.feature_apps.data.DetailedApp
import cm.aptoide.pt.installedapps.data.InstalledAppsRepository
import cm.aptoide.pt.installedapps.data.database.model.InstalledAppEntity
import cm.aptoide.pt.installedapps.data.database.model.InstalledState
import cm.aptoide.pt.installedapps.domain.model.InstalledApp
import cm.aptoide.pt.packageinstaller.AppInstall
import cm.aptoide.pt.packageinstaller.AppInstaller
import cm.aptoide.pt.packageinstaller.InstallStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideInstallManager @Inject constructor(
  private val downloadManager: DownloadManager,
  private val downloadStateMapper: DownloadStateMapper,
  private val downloadFileMapper: DownloadFileMapper,
  private val installedAppsRepository: InstalledAppsRepository,
  private val downloadFactory: DownloadFactory,
  private val installer: AppInstaller,
  private val appInstallerStatusReceiver: AppInstallerStatusReceiver
) : InstallManager {

  override suspend fun start() {
    downloadManager.start()
    dispatchInstalls()
  }

  override suspend fun download(download: Download) = withContext(Dispatchers.IO) {
    try {
      val downloadEntity = downloadManager.getDownloadAsSingle(download.md5)
        .onErrorReturn { throwable ->
          throwable.printStackTrace()
          downloadFactory.createDownload(download)
        }
        .doOnError { throwable -> throwable.printStackTrace() }
        .await()
      setupInstallationProcess(downloadEntity)
      downloadManager.startDownload(downloadEntity).await()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun setupInstallationProcess(downloadEntity: DownloadEntity) =
    installedAppsRepository.addInstalledApp(
      InstalledAppEntity(
        packageName = downloadEntity.packageName,
        appName = downloadEntity.appName,
        appVersion = downloadEntity.versionName,
        versionCode = downloadEntity.versionCode,
        appIcon = downloadEntity.icon,
        installedState = InstalledState.DOWNLOADING
      )
    )

  override fun install(packageName: String) {
    // TODO: delete not necessary
  }

  override fun getDownload(app: DetailedApp): Flow<Download> =
    downloadManager.getDownloadAsObservable(app.md5)
      .doOnError { throwable -> throwable.printStackTrace() }
      .asFlow()
      .catch { throwable -> throwable.printStackTrace() }
      .combine(
        installedAppsRepository.getInstalledApp(app.versionCode, app.packageName)
      ) { downloadEntity: DownloadEntity, installedApp: InstalledApp ->
        Download(
          appName = app.name,
          packageName = app.packageName,
          md5 = app.md5,
          icon = app.icon,
          versionName = app.versionName,
          versionCode = app.versionCode,
          hasAppc = app.isAppCoins,
          appSize = app.appSize,
          downloadState = downloadStateMapper.mapDownloadState(
            downloadStatus = downloadEntity.overallDownloadStatus,
            installedAppState = installedApp.installedAppState
          ),
          progress = downloadEntity.overallProgress,
          downloadFileList = downloadFileMapper.mapDownloadFileList(downloadEntity.filesToDownload),
          action = DownloadAction.INSTALL,
          trustedBadge = app.malware!!,
          storeName = app.store.storeName
        )
      }
      .flowOn(Dispatchers.IO)
      .catch { throwable -> throwable.printStackTrace() }


  override suspend fun cancelDownload(md5: String) {
    downloadManager.removeDownload(md5)
  }

  override fun getActiveDownloads(packageName: String) {
    downloadManager.currentActiveDownloads
  }

  private suspend fun dispatchInstalls() =
    installedAppsRepository
      .getDownloadInstallApps()
      .flatMapMerge { it.asFlow() }
      .flatMapLatest { installedApp ->
        downloadManager.getCompletedDownload(installedApp.packageName).doOnNext {
        }.asFlow()
          .map { downloadEntity ->
            installedAppsRepository.addInstalledApp(
              InstalledAppEntity(
                packageName = installedApp.packageName,
                appName = installedApp.appName,
                appVersion = downloadEntity.versionName,
                versionCode = installedApp.versionCode,
                appIcon = installedApp.appIcon,
                installedState = InstalledState.INSTALLING
              )
            )
            installer.install(createAppInstall(downloadEntity))
            installedApp
          }
      }
      .flatMapLatest { installedApp ->
        appInstallerStatusReceiver.getInstallerInstallStatus()
          .filter { installedApp.packageName == it.packageName }.asFlow().map { installStatus ->
            installedAppsRepository.addInstalledApp(
              InstalledAppEntity(
                packageName = installedApp.packageName,
                appName = installedApp.appName,
                appVersion = installedApp.versionName,
                versionCode = installedApp.versionCode,
                appIcon = installedApp.appIcon,
                installedState = mapInstallStatus(installStatus)
              )
            )
            installedApp
          }
      }
      .catch { cause: Throwable -> cause.printStackTrace() }
      .collect()

  private fun mapInstallStatus(installStatus: InstallStatus): InstalledState =
    when (installStatus.status!!) {
      InstallStatus.Status.SUCCESS -> InstalledState.INSTALLED
      InstallStatus.Status.INSTALLING -> InstalledState.INSTALLING
      InstallStatus.Status.FAIL -> InstalledState.NOT_INSTALLED
      InstallStatus.Status.CANCELED -> InstalledState.NOT_INSTALLED
      InstallStatus.Status.UNKNOWN_ERROR -> InstalledState.NOT_INSTALLED
      InstallStatus.Status.WAITING_INSTALL_FEEDBACK -> InstalledState.NOT_INSTALLED
    }

  private fun createAppInstall(download: DownloadEntity): AppInstall =
    AppInstall.builder().setPackageName(download.packageName)
      .setBaseApk(File(download.filesToDownload[0].filePath))
      .build()
}
