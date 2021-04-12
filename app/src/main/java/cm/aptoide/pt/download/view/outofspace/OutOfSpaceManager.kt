package cm.aptoide.pt.download.view.outofspace

import cm.aptoide.pt.file.FileManager
import cm.aptoide.pt.install.InstallAppSizeValidator
import cm.aptoide.pt.install.InstallManager
import rx.Observable
import rx.Single


class OutOfSpaceManager(
    private val installManager: InstallManager,
    private val requiredSpace: Long,
    private val fileManager: FileManager,
    private val installAppSizeValidator: InstallAppSizeValidator) {


  fun getInstalledApps(): Observable<List<InstalledApp>> {
    return installManager.fetchInstalledExceptSystem()
        .flatMap {
          Observable.from(it).map { InstalledApp(it.name, it.packageName, it.icon, it.appSize) }
              .toList()
        }
  }

  fun uninstallApp(packageName: String?): Single<Long> {
    return getInstalledAppSize(packageName).flatMap { appSize ->
      installManager.uninstallApp(packageName).andThen(Single.just(appSize))
    }
  }

  fun clearSpaceFromCache(): Observable<Boolean> {
    return fileManager.deleteCache(false)
        .map { requiredSpace < installAppSizeValidator.getAvailableSpace() }
  }

  private fun getInstalledAppSize(packageName: String?): Single<Long> {
    return installManager.getInstalledAppSize(packageName)
  }
}
