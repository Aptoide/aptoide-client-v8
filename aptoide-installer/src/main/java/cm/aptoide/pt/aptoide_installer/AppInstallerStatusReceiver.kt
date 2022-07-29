package cm.aptoide.pt.aptoide_installer

import cm.aptoide.pt.packageinstaller.InstallStatus
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class AppInstallerStatusReceiver(private val installStatusPublishSubject: PublishSubject<InstallStatus>) {

  fun onStatusReceived(installStatus: InstallStatus) {
    installStatusPublishSubject.onNext(installStatus)
  }

  fun getInstallerInstallStatus(): Observable<InstallStatus> {
    return installStatusPublishSubject
  }

}