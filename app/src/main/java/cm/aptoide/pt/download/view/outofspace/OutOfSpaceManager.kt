package cm.aptoide.pt.download.view.outofspace

import cm.aptoide.pt.install.InstallAppSizeValidator
import cm.aptoide.pt.install.InstallManager
import rx.Observable
import rx.Single
import rx.subjects.PublishSubject


class OutOfSpaceManager(
    private val installManager: InstallManager,
    private val appSize: Long,
    private val uninstalledEnoughApps: PublishSubject<Void>,
    private val installAppSizeValidator: InstallAppSizeValidator) {

  private var requiredSpace: Long = appSize


  fun getInstalledApps(): Observable<List<InstalledApp>> {
    return installManager.fetchInstalledExceptSystem()
        .flatMap {
          Observable.from(it).filter {
            !it.packageName.equals("cm.aptoide.pt") && !it.packageName.equals("com.appcoins.wallet")
          }.map { InstalledApp(it.name, it.packageName, it.icon, it.appSize) }
              .toList()
        }
  }

  fun uninstallApp(packageName: String?): Single<Long> {
    return getInstalledAppSize(packageName).flatMap { appSize ->
      installManager.uninstallApp(packageName).andThen(Single.just(appSize))
          .doOnSuccess {
            if (requiredSpace <= appSize) {
              uninstalledEnoughApps.onNext(null)
            } else {
              requiredSpace -= appSize
            }
          }
    }.map { requiredSpace }
  }

  private fun getInstalledAppSize(packageName: String?): Single<Long> {
    return installManager.getInstalledAppSize(packageName)
  }

  fun uninstalledEnoughApps(): Observable<Void> {
    return uninstalledEnoughApps
  }

  fun getRequiredStorageSize(): Single<Long> {
    return Single.just(appSize - installAppSizeValidator.getAvailableSpace())
        .doOnSuccess { requiredSpace = it }
  }
}
