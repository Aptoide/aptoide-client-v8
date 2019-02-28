package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.install.Install;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipegoncalves on 3/22/18.
 */

public class AppMapper {

  public List<App> getDownloadApps(List<Install> installations) {
    List<App> downloadsList = new ArrayList<>();

    for (int i = 0; i < installations.size(); i++) {
      Install install = installations.get(i);
      downloadsList.add(
          new DownloadApp(install.getAppName(), install.getMd5(), install.getPackageName(),
              install.getIcon(), install.getProgress(), install.isIndeterminate(),
              install.getVersionCode(), mapDownloadStatus(install.getState())));
    }
    return downloadsList;
  }

  private StateApp.Status mapDownloadStatus(Install.InstallationStatus installationStatus) {
    StateApp.Status status;
    switch (installationStatus) {
      case GENERIC_ERROR:
      case INSTALLATION_TIMEOUT:
      case NOT_ENOUGH_SPACE_ERROR:
        status = StateApp.Status.ERROR;
        break;
      case PAUSED:
      case IN_QUEUE:
      case INITIAL_STATE:
        status = StateApp.Status.STANDBY;
        break;
      case DOWNLOADING:
        status = StateApp.Status.ACTIVE;
        break;
      case INSTALLED:
      case UNINSTALLED:
        status = StateApp.Status.COMPLETED;
        break;
      default:
        throw new IllegalStateException("Invalid installation status");
    }
    return status;
  }

  public List<App> mapInstalledToInstalledApps(List<Installed> installeds) {
    List<App> installedAppsList = new ArrayList<>();

    for (Installed installed : installeds) {
      installedAppsList.add(new InstalledApp(installed.getName(), installed.getPackageName(),
          installed.getVersionName(), installed.getIcon()));
    }
    return installedAppsList;
  }

  public List<App> mapUpdateToUpdateAppList(List<Update> updates) {
    List<App> updatesList = new ArrayList<>();
    for (Update update : updates) {
      updatesList.add(new UpdateApp(update.getLabel(), update.getMd5(), update.getIcon(),
          update.getPackageName(), 0, false, update.getUpdateVersionName(), update.getVersionCode(),
          StateApp.Status.UPDATE, update.getAppId()));
    }
    return updatesList;
  }

  public List<App> getUpdatesList(List<Install> installs) {
    List<App> updatesList = new ArrayList<>();
    for (Install install : installs) {
      updatesList.add(new UpdateApp(install.getAppName(), install.getMd5(), install.getIcon(),
          install.getPackageName(), install.getProgress(), install.isIndeterminate(),
          install.getVersionName(), install.getVersionCode(), mapUpdateStatus(install.getState()),
          -1)); //Updates in progress (downloads) dont have app id.
    }
    return updatesList;
  }

  private StateApp.Status mapUpdateStatus(Install.InstallationStatus state) {
    StateApp.Status status;

    switch (state) {
      case GENERIC_ERROR:
      case INSTALLATION_TIMEOUT:
      case NOT_ENOUGH_SPACE_ERROR:
        status = StateApp.Status.ERROR;
        break;
      case PAUSED:
      case IN_QUEUE:
      case INITIAL_STATE:
        status = StateApp.Status.STANDBY;
        break;
      case DOWNLOADING:
        status = StateApp.Status.UPDATING;
        break;
      case INSTALLED:
      case UNINSTALLED:
        status = StateApp.Status.UPDATE;
        break;
      default:
        status = StateApp.Status.UPDATE;
        break;
    }
    return status;
  }
}
