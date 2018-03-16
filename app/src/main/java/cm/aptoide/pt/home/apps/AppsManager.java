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
  private InstallToDownloadAppMapper installToDownloadAppMapper;
  private InstalledToInstalledAppMapper installedToInstalledAppMapper;

  public AppsManager(UpdatesManager updatesManager, InstallManager installManager,
      InstallToDownloadAppMapper downloadsManager,
      InstalledToInstalledAppMapper installedToInstalledAppMapper) {
    this.updatesManager = updatesManager;
    this.installManager = installManager;
    this.installToDownloadAppMapper = downloadsManager;
    this.installedToInstalledAppMapper = installedToInstalledAppMapper;
  }

  public Observable<List<App>> getUpdatesList() {
    //return updatesManager.getUpdatesList()
    //  .map(updatesList ->);
    // TODO: 3/7/18 map Displayables to updateApp
    return null;
  }

  public Observable<List<App>> getInstalledApps() {
    return installManager.fetchInstalled()
        .flatMapIterable(list -> list)
        .flatMap(item -> updatesManager.filterUpdates(item))
        .toList()
        .map(installeds -> installedToInstalledAppMapper.getInstalledApps(installeds));
  }

  public Observable<List<App>> getDownloadApps() {
    return installManager.getInstallations()
        .distinctUntilChanged()
        .flatMap(installations -> {
          if (installations == null || installations.isEmpty()) {
            return Observable.empty();
          }
          return Observable.just(installations)
              .map(installedApps -> installToDownloadAppMapper.getDownloadApps(installedApps));
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

  public void retryUpdate(App app) {

  }

  public void resumeUpdate(App app) {

  }

  public void cancelUpdate(App app) {

  }

  public void pauseUpdate(App app) {

  }

  public void updateApp(App app) {

  }
}
