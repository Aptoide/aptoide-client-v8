package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.database.room.RoomInstalled;
import cm.aptoide.pt.home.apps.model.AppcUpdateApp;
import cm.aptoide.pt.home.apps.model.DownloadApp;
import cm.aptoide.pt.home.apps.model.InstalledApp;
import cm.aptoide.pt.home.apps.model.StateApp;
import cm.aptoide.pt.home.apps.model.UpdateApp;
import cm.aptoide.pt.install.Install;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by filipegoncalves on 3/22/18.
 */

public class AppMapper {

  public List<DownloadApp> getDownloadApps(List<Install> installations) {
    List<DownloadApp> downloadsList = new ArrayList<>();

    for (int i = 0; i < installations.size(); i++) {
      Install install = installations.get(i);
      downloadsList.add(new DownloadApp(install.getAppName(), install.getMd5(), install.getIcon(),
          install.getPackageName(), install.getProgress(), install.getVersionName(),
          install.getVersionCode(), mapDownloadStatus(install.getState()), -1));
    }
    Collections.sort(downloadsList, (app1, app2) -> app1.getName()
        .compareToIgnoreCase(app2.getName()));
    return downloadsList;
  }

  public List<InstalledApp> mapInstalledToInstalledApps(List<RoomInstalled> installeds) {
    List<InstalledApp> installedAppsList = new ArrayList<>();

    for (RoomInstalled installed : installeds) {
      installedAppsList.add(new InstalledApp(installed.getName(), installed.getPackageName(),
          installed.getVersionName(), installed.getIcon()));
    }
    return installedAppsList;
  }

  public UpdateApp mapUpdateToUpdateApp(Update update, boolean isInstalledWithAptoide) {
    return new UpdateApp(update.getLabel(), update.getMd5(), update.getIcon(),
        update.getPackageName(), 0, update.getUpdateVersionName(), update.getVersionCode(),
        StateApp.Status.STANDBY, update.getAppId(), isInstalledWithAptoide);
  }

  public List<AppcUpdateApp> mapUpdateToUpdateAppcAppList(List<Update> updates,
      boolean hasPromotion, float appcValue) {
    List<AppcUpdateApp> updatesList = new ArrayList<>();
    for (Update update : updates) {
      updatesList.add(new AppcUpdateApp(update.getLabel(), update.getMd5(), update.getIcon(),
          update.getPackageName(), 0, update.getUpdateVersionName(), update.getVersionCode(),
          StateApp.Status.STANDBY, update.getAppId(), hasPromotion, appcValue));
    }
    return updatesList;
  }

  public UpdateApp mapInstallToUpdateApp(Install install, boolean isInstalledWithAptoide) {
    return new UpdateApp(install.getAppName(), install.getMd5(), install.getIcon(),
        install.getPackageName(), install.getProgress(), install.getVersionName(),
        install.getVersionCode(), mapDownloadStatus(install.getState()), -1,
        isInstalledWithAptoide);
  }

  private StateApp.Status mapDownloadStatus(Install.InstallationStatus state) {
    StateApp.Status status;
    switch (state) {
      case GENERIC_ERROR:
      case INSTALLATION_TIMEOUT:
      case NOT_ENOUGH_SPACE_ERROR:
        status = StateApp.Status.ERROR;
        break;
      case IN_QUEUE:
        status = StateApp.Status.IN_QUEUE;
        break;
      case PAUSED:
        status = StateApp.Status.PAUSE;
        break;
      case DOWNLOADING:
        status = StateApp.Status.ACTIVE;
        break;
      case INSTALLING:
        status = StateApp.Status.INSTALLING;
        break;
      case UNINSTALLED:
      case INITIAL_STATE:
      default:
        status = StateApp.Status.STANDBY;
        break;
    }
    return status;
  }
}
