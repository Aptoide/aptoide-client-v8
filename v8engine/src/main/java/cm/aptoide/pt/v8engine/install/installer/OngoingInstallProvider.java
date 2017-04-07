package cm.aptoide.pt.v8engine.install.installer;

import cm.aptoide.pt.v8engine.repository.InstalledRepository;
import rx.Observable;

/**
 * Created by trinkes on 07/04/2017.
 */

public class OngoingInstallProvider {
  private InstalledRepository installedRepository;

  public OngoingInstallProvider(InstalledRepository installedRepository) {
    this.installedRepository = installedRepository;
  }

  public Observable<OngoingInstall> getInstallation(String packageName) {
    return installedRepository.get(packageName)
        .map(installed -> new OngoingInstall(installed.getPackageName(), installed.getVersionCode(),
            installed.getVersionName(), installed.getType(), installed.getStatus()));
  }
}
