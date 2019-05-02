package cm.aptoide.pt.install;

import cm.aptoide.pt.packageinstaller.InstallStatus;
import rx.Observable;
import rx.subjects.PublishSubject;

public class AppInstallerStatusReceiver {

  private PublishSubject<InstallStatus> installStatusPublishSubject;

  public AppInstallerStatusReceiver(PublishSubject<InstallStatus> installStatusPublishSubject) {
    this.installStatusPublishSubject = installStatusPublishSubject;
  }

  public void onStatusReceived(InstallStatus installStatus) {
    installStatusPublishSubject.onNext(installStatus);
  }

  public Observable<InstallStatus> getInstallerInstallStatus() {
    return installStatusPublishSubject;
  }
}
