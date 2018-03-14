package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.install.InstallManager;
import java.util.List;
import rx.Observable;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsManager {

  private UpdatesManager updatesManager;
  private InstallManager installManager;
  private DownloadAppToInstallMapper downloadAppToInstallMapper;

  public AppsManager(UpdatesManager updatesManager, InstallManager installManager,
      DownloadAppToInstallMapper downloadsManager) {
    this.updatesManager = updatesManager;
    this.installManager = installManager;
    this.downloadAppToInstallMapper = downloadsManager;
  }

  public Observable<List<App>> getUpdatesList() {
    //return updatesManager.getUpdatesList()
    //  .map(updatesList ->);
    // TODO: 3/7/18 map Displayables to updateApp
    return null;
  }

  public Observable<List<App>> getInstalledApps() {
    return null;
  }

  public Observable<List<App>> getDownloadApps() {
    return installManager.getInstallations()
        .distinctUntilChanged()
        .flatMap(installations -> {
          if (installations == null || installations.isEmpty()) {
            return Observable.empty();
          }
          return Observable.just(installations)
              .map(installedApps -> downloadAppToInstallMapper.getDownloadApps(installedApps));
        });
  }

  public void retryDownload(App app) {

  }

  public void installApp(App app) {

  }

  public void cancelDownload(App app) {

  }

  public void resumeDownload(App app) {

  }

  public void pauseDownload(App app) {

  }
}
