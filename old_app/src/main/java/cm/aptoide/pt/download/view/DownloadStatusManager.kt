package cm.aptoide.pt.download.view

import cm.aptoide.pt.app.migration.AppcMigrationManager
import cm.aptoide.pt.install.Install.InstallationStatus
import cm.aptoide.pt.install.Install.InstallationType
import cm.aptoide.pt.install.InstallManager
import rx.Observable
import java.util.concurrent.TimeUnit

class DownloadStatusManager(private val installManager: InstallManager,
                            private val appcMigrationManager: AppcMigrationManager) {


  fun loadDownloadModel(md5: String, packageName: String,
                        versionCode: Int, signature: String?,
                        storeId: Long,
                        hasAppc: Boolean): Observable<DownloadStatusModel>? {
    return Observable.combineLatest(installManager.getInstall(md5, packageName, versionCode),
        appcMigrationManager.isMigrationApp(packageName, signature, versionCode, storeId,
            hasAppc)) { install, isMigration ->
      DownloadStatusModel(parseStatusDownloadType(install.type, isMigration),
          install.progress,
          parseStatusDownloadState(install.state, install.isIndeterminate))
    }.throttleLast(200, TimeUnit.MILLISECONDS)
  }

  private fun parseStatusDownloadType(type: InstallationType?,
                                      isMigration: Boolean): DownloadStatusModel.Action {
    return if (isMigration) {
      DownloadStatusModel.Action.MIGRATE
    } else {
      when (type) {
        InstallationType.INSTALLED -> DownloadStatusModel.Action.OPEN
        InstallationType.DOWNGRADE -> DownloadStatusModel.Action.DOWNGRADE
        InstallationType.UPDATE -> DownloadStatusModel.Action.UPDATE
        InstallationType.INSTALL -> DownloadStatusModel.Action.INSTALL
        else -> DownloadStatusModel.Action.INSTALL
      }
    }
  }

  private fun parseStatusDownloadState(
      state: InstallationStatus?,
      isIndeterminate: Boolean): DownloadStatusModel.DownloadState {
    return if (isIndeterminate) {
      DownloadStatusModel.DownloadState.IN_QUEUE
    } else {
      when (state) {
        InstallationStatus.INSTALLATION_TIMEOUT, InstallationStatus.GENERIC_ERROR -> DownloadStatusModel.DownloadState.GENERIC_ERROR
        InstallationStatus.NOT_ENOUGH_SPACE_ERROR -> DownloadStatusModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR
        InstallationStatus.IN_QUEUE -> DownloadStatusModel.DownloadState.IN_QUEUE
        InstallationStatus.PAUSED -> DownloadStatusModel.DownloadState.PAUSE
        InstallationStatus.DOWNLOADING -> DownloadStatusModel.DownloadState.ACTIVE
        InstallationStatus.INITIAL_STATE, InstallationStatus.INSTALLED, InstallationStatus.UNINSTALLED -> DownloadStatusModel.DownloadState.STANDBY
        InstallationStatus.INSTALLING -> DownloadStatusModel.DownloadState.INSTALLING
        else -> throw IllegalStateException("Wrong type of download state")
      }
    }
  }

}