package cm.aptoide.pt.aptoide_installer

import android.util.Log
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
            Log.d("lol", "download: error after getting single ")
            throwable.printStackTrace()
            downloadFactory.createDownload(download)
          }
          .doOnError { throwable ->
            Log.d("lol", "download: download got error")
            throwable.printStackTrace()
          }
          .await()
        setupInstallationProcess(downloadEntity)
        downloadManager.startDownload(downloadEntity).await()
      } catch (e: Exception) {
        Log.d("lol", "download: download exception try catch")
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
    Log.d(
      "lol",
      "setupInstallationProcess: saving downloading installed app " + installedApp.packageName
    )
    installedAppsRepository.addInstalledApp(installedApp)
  }

  override fun install(packageName: String) {
    // TODO: delete not necessary
  }

  override fun getDownload(app: App): Flow<Download> {
    return downloadManager.getDownloadAsObservable(app.md5).doOnSubscribe {
      Log.d(
        "lol",
        "getDownload: subscribed got download"
      )
    }.doOnError { throwable ->
      Log.d("lol", "getDownload: error while getting download")
      throwable.printStackTrace()
    }.doOnNext {
      Log.d("lol", "emitted download : " + it.packageName + " and " + it.overallDownloadStatus)
    }.asFlow()
      .catch { throwable ->
        throwable.printStackTrace()
      }.combine(
        installedAppsRepository.getInstalledApp(app.versionCode, app.packageName)
      ) { downloadEntity: DownloadEntity, installedApp: InstalledApp ->
        Log.d("lol", "getting download ")
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


  override fun cancelDownload(packageName: String) {
    downloadManager.removeDownload(packageName)
  }

  override fun getActiveDownloads(packageName: String) {
    downloadManager.currentActiveDownloads
  }

  private suspend fun dispatchInstalls() {
    installedAppsRepository.getDownloadInstallApps()
      .map {
        Log.d("lol", "dispatchInstalls: got a list of download install apps size of " + it.size)
        it
      }
      .flatMapMerge { it.asFlow() }
      .flatMapLatest { installedApp ->
        downloadManager.getCompletedDownload(installedApp.packageName).doOnNext {
          Log.d(
            "lol",
            "dispatchInstalls: emitted download as observable COMPLETED " + it.packageName
          )
        }.asFlow()
          .map { downloadEntity ->
            Log.d("lol", "dispatchInstalls: got a completed download")
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
            downloadEntity
          }
      }.map { downloadEntity ->
        installer.install(createAppInstall(downloadEntity))
        downloadEntity
      }
      .catch { cause: Throwable ->
        Log.d("lol", "dispatchInstalls: error here")
        cause.printStackTrace()
      }.collect()
    /*it.map { installedApp ->
      Log.d("lol", "dispatchInstalls: getting download for downloading installed app")
      downloadManager.getDownloadAsObservable(installedApp.packageName).asFlow()
        .filter { download -> download.overallDownloadStatus == DownloadEntity.COMPLETED }
        .map { downloadEntity ->
          Log.d("lol", "dispatchInstalls: got a completed download")
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
          downloadEntity
        }
        .map { downloadEntity -> installer.install(createAppInstall(downloadEntity)) }
    }*/


    //create another chain - installed installed app - remove install files

  }

  private fun createAppInstall(download: DownloadEntity): AppInstall {
    val appInstall =
      AppInstall.builder().setPackageName(download.packageName)
        .setBaseApk(File(download.filesToDownload[0].filePath))
    return appInstall.build()
  }
}