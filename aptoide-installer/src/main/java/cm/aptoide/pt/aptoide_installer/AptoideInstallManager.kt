package cm.aptoide.pt.aptoide_installer

import cm.aptoide.pt.aptoide_installer.model.*
import cm.aptoide.pt.downloadmanager.DownloadManager
import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity
import cm.aptoide.pt.feature_apps.data.App
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

  override suspend fun download(download: Download) {
    withContext(Dispatchers.IO) {
      try {

        val downloadEntity = downloadManager.getDownloadAsSingle(download.md5)
          .onErrorReturn { throwable ->
            throwable.printStackTrace()
            downloadFactory.createDownload(download)
          }
          .doOnError { throwable ->
            throwable.printStackTrace()
          }
          .await()
        setupInstallationProcess(downloadEntity)
        downloadManager.startDownload(downloadEntity).await()
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  private fun setupInstallationProcess(downloadEntity: DownloadEntity) {
    val installedApp = InstalledAppEntity(
      downloadEntity.packageName,
      downloadEntity.appName,
      downloadEntity.versionName,
      downloadEntity.versionCode, downloadEntity.icon, InstalledState.DOWNLOADING
    )
    installedAppsRepository.addInstalledApp(installedApp)
  }

  override fun install(packageName: String) {
    // TODO: delete not necessary
  }

  override fun getDownload(app: App): Flow<Download> {
    return downloadManager.getDownloadAsObservable(app.md5).doOnError { throwable ->
      throwable.printStackTrace()
    }.asFlow()
      .catch { throwable ->
        throwable.printStackTrace()
      }.combine(
        installedAppsRepository.getInstalledApp(app.versionCode, app.packageName)
      ) { downloadEntity: DownloadEntity, installedApp: InstalledApp ->
        Download(
          app.name,
          app.packageName,
          "md5",
          app.icon,
          app.versionName,
          app.versionCode,
          app.isAppCoins,
          app.appSize,
          downloadStateMapper.mapDownloadState(
            downloadEntity.overallDownloadStatus,
            installedApp.installedAppState
          ),
          downloadEntity.overallProgress,
          downloadFileMapper.mapDownloadFileList(
            downloadEntity.filesToDownload
          ),
          DownloadAction.INSTALL, app.malware!!, app.store.storeName
        )
      }.flowOn(Dispatchers.IO).catch { throwable -> throwable.printStackTrace() }
  }


  override suspend fun cancelDownload(md5: String) {
    downloadManager.removeDownload(md5)
  }

  override fun getActiveDownloads(packageName: String) {
    downloadManager.currentActiveDownloads
  }

  private suspend fun dispatchInstalls() {
    installedAppsRepository.getDownloadInstallApps()
      .flatMapMerge { it.asFlow() }
      .flatMapLatest { installedApp ->
        downloadManager.getCompletedDownload(installedApp.packageName).doOnNext {
        }.asFlow()
          .map { downloadEntity ->
            installedAppsRepository.addInstalledApp(
              InstalledAppEntity(
                installedApp.packageName,
                installedApp.appName,
                "",
                installedApp.versionCode,
                installedApp.appIcon,
                InstalledState.INSTALLING
              )
            )
            installer.install(createAppInstall(downloadEntity))
            installedApp
          }
      }.flatMapLatest { installedApp ->
        appInstallerStatusReceiver.getInstallerInstallStatus()
          .filter { installedApp.packageName == it.packageName }.asFlow().map { installStatus ->
            installedAppsRepository.addInstalledApp(
              InstalledAppEntity(
                installedApp.packageName, installedApp.appName,
                "",
                installedApp.versionCode,
                installedApp.appIcon,
                mapInstallStatus(installStatus)
              )
            )
            installedApp
          }
      }
      .catch { cause: Throwable ->
        cause.printStackTrace()
      }.collect()

  }

  private fun mapInstallStatus(installStatus: InstallStatus): InstalledState {
    return when (installStatus.status) {
      InstallStatus.Status.SUCCESS -> {
        InstalledState.INSTALLED
      }
      InstallStatus.Status.INSTALLING -> {
        InstalledState.INSTALLING
      }
      InstallStatus.Status.FAIL -> {
        InstalledState.NOT_INSTALLED
      }
      InstallStatus.Status.CANCELED -> {
        InstalledState.NOT_INSTALLED
      }
      InstallStatus.Status.UNKNOWN_ERROR -> {
        InstalledState.NOT_INSTALLED
      }
      InstallStatus.Status.WAITING_INSTALL_FEEDBACK -> {
        InstalledState.NOT_INSTALLED
      }
    }
  }

  private fun createAppInstall(download: DownloadEntity): AppInstall {
    val appInstall =
      AppInstall.builder().setPackageName(download.packageName)
        .setBaseApk(File(download.filesToDownload[0].filePath))
    return appInstall.build()
  }
}