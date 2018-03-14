package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.install.Install;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipegoncalves on 3/8/18.
 */

public class DownloadAppToInstallMapper {

  public List<App> getDownloadApps(List<Install> installations) {
    List<App> downloadsList = new ArrayList<>();

    for (int i = 0; i < installations.size(); i++) {
      Install install = installations.get(i);
      downloadsList.add(new DownloadApp(install.getAppName(), install.getMd5(), install.getIcon(),
          install.getProgress(), install.isIndeterminate(), install.getVersionCode(),
          mapDownloadStatus(install.getState())));
    }
    return downloadsList;
  }

  private DownloadApp.Status mapDownloadStatus(Install.InstallationStatus installationStatus) {
    DownloadApp.Status status;
    switch (installationStatus) {
      case GENERIC_ERROR:
      case INSTALLATION_TIMEOUT:
      case NOT_ENOUGH_SPACE_ERROR:
        status = DownloadApp.Status.ERROR;
        break;
      case PAUSED:
      case IN_QUEUE:
        status = DownloadApp.Status.STANDBY;
        break;
      case INSTALLING:
        status = DownloadApp.Status.ACTIVE;
        break;
      case INSTALLED:
      case UNINSTALLED:
        status = DownloadApp.Status.COMPLETED;
        break;
      default:
        status = DownloadApp.Status.COMPLETED;
        break;
    }
    return status;
  }
}
