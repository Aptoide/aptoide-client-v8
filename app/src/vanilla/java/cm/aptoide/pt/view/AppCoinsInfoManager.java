package cm.aptoide.pt.view;

import cm.aptoide.pt.app.view.AppCoinsInfoFragment;
import cm.aptoide.pt.install.InstallManager;
import rx.Observable;

/**
 * Created by D01 on 03/08/2018.
 */

public class AppCoinsInfoManager {

  private final InstallManager installManager;

  public AppCoinsInfoManager(InstallManager installManager) {

    this.installManager = installManager;
  }

  public Observable<Boolean> loadButtonState() {
    return installManager.isInstalled(AppCoinsInfoFragment.APPCWALLETPACKAGENAME);
  }
}
