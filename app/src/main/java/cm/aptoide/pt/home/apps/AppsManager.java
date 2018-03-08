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

  public AppsManager(UpdatesManager updatesManager, InstallManager installManager) {
    this.updatesManager = updatesManager;
    this.installManager = installManager;
  }

  public Observable<List<UpdateApp>> getUpdatesList() {
    //return updatesManager.getUpdatesList()
    //  .map(updatesList ->);
    // TODO: 3/7/18 map Displayables to updateApp
    return null;
  }

  public Observable<List<InstalledApp>> getInstalledApps() {
    return null;
  }
}
