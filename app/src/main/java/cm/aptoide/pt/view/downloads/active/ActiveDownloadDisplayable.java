package cm.aptoide.pt.view.downloads.active;

import cm.aptoide.pt.Install;
import cm.aptoide.pt.InstallManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import rx.Observable;

/**
 * Created by trinkes on 7/18/16.
 */
public class ActiveDownloadDisplayable extends Displayable {

  private final InstallManager installManager;
  private final Install installation;

  public ActiveDownloadDisplayable() {
    this.installManager = null;
    this.installation = null;
  }

  public ActiveDownloadDisplayable(Install installation, InstallManager installManager) {
    this.installation = installation;
    this.installManager = installManager;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.active_download_row_layout;
  }

  public void pauseInstall() {
    installManager.stopInstallation(installation.getMd5());
  }

  public Observable<Install> getInstallationObservable() {
    return installManager.getInstall(installation.getMd5(), installation.getPackageName(),
        installation.getVersionCode());
  }
}
