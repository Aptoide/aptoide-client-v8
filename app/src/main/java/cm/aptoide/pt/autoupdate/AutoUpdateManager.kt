package cm.aptoide.pt.autoupdate

import android.content.SharedPreferences
import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.pt.actions.PermissionManager
import cm.aptoide.pt.actions.PermissionService
import cm.aptoide.pt.database.realm.Download
import cm.aptoide.pt.download.DownloadAnalytics
import cm.aptoide.pt.download.DownloadFactory
import cm.aptoide.pt.install.Install
import cm.aptoide.pt.install.InstallManager
import rx.Observable
import rx.Single

open class AutoUpdateManager(private val downloadFactory: DownloadFactory,
                             private val permissionManager: PermissionManager,
                             private val installManager: InstallManager,
                             private val downloadAnalytics: DownloadAnalytics,
                             private val localVersionCode: Int,
                             private val autoUpdateRepository: AutoUpdateRepository,
                             private val localVersionSdk: Int,
                             private val sharedPreferences: SharedPreferences) {

  private val AUTO_UPDATE_SHOW = "showAutoUpdate"

  fun shouldUpdate(): Observable<Boolean> {
    return loadAutoUpdateModel().toObservable().map { it.shouldUpdate }
  }

  fun requestPermissions(permissionService: PermissionService): Observable<Void> {
    return permissionManager.requestDownloadAccess(permissionService)
        .flatMap { permissionManager.requestExternalStoragePermission(permissionService) }
  }

  fun startUpdate(shouldInstall: Boolean): Observable<Install> {
    return getAutoUpdateModel().flatMap {
      Observable.just(downloadFactory.create(it.md5, it.versionCode, it.packageName, it.uri, false))
          .flatMapCompletable { download ->
            installManager.install(download, shouldInstall)
                .doOnSubscribe {
                  downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK
                      , DownloadAnalytics.AppContext.AUTO_UPDATE, false)
                }
          }
          .toCompletable()
          .andThen(getInstall())
    }
  }

  fun shouldShowAutoUpdateDialog(): Observable<Boolean> {
    val result = sharedPreferences.getInt(AUTO_UPDATE_SHOW, 0)
    return Observable.just(result % 5 == 0 || result == 1)
  }

  fun incrementAutoUpdateShow() {
    val autoUpdateShow = sharedPreferences.getInt(AUTO_UPDATE_SHOW, 0)
    val result = autoUpdateShow + 1
    sharedPreferences.edit().putInt(AUTO_UPDATE_SHOW, result).apply()
  }

  fun clearAutoUpdateShow() {
    sharedPreferences.edit().putInt(AUTO_UPDATE_SHOW, 0).apply()
  }

  fun isDownloadComplete(): Single<Boolean> {
    return loadAutoUpdateModel().toObservable()
        .flatMapSingle { model -> installManager.getDownload(model.md5) }
        .map { download -> download != null && download.overallDownloadStatus == Download.COMPLETED }
        .toSingle()
  }

  private fun loadAutoUpdateModel(): Single<AutoUpdateModel> {
    return autoUpdateRepository.loadFreshAutoUpdateModel()
        .flatMap {
          var autoUpdateModel = it
          if (!it.wasSuccess()) Single.error<Throwable>(Throwable(it.status.toString()))
          if (shouldUpdate(it))
            autoUpdateModel = it.copy(shouldUpdate = true)
          Single.just(autoUpdateModel)
        }
  }

  private fun getAutoUpdateModel(): Observable<AutoUpdateModel> {
    return autoUpdateRepository.loadAutoUpdateModel().toObservable()

  }

  private fun getInstall(): Observable<Install> {
    return getAutoUpdateModel().flatMap {
      installManager.getInstall(it.md5,
          it.packageName, it.versionCode)
          .first { install -> install.hasDownloadStarted() }
    }
  }

  private fun shouldUpdate(autoUpdateModel: AutoUpdateModel): Boolean {
    return autoUpdateModel.versionCode > localVersionCode && localVersionSdk >= Integer.parseInt(
        autoUpdateModel.minSdk)
  }

  fun hasDownloadPermissions(permissionService: PermissionService): Observable<Boolean> {
    return permissionManager.hasDownloadAccess(permissionService)
  }

}
