package cm.aptoide.pt.app;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.download.InstallType;
import cm.aptoide.pt.download.Origin;
import cm.aptoide.pt.install.Install;

/**
 * Created by filipegoncalves on 5/9/18.
 */

public class DownloadStateParser {

  public DownloadStateParser() {
  }

  public DownloadModel.DownloadState parseDownloadState(Install.InstallationStatus state) {
    DownloadModel.DownloadState downloadState;
    switch (state) {
      case DOWNLOADING:
        downloadState = DownloadModel.DownloadState.ACTIVE;
        break;
      case PAUSED:
        downloadState = DownloadModel.DownloadState.PAUSE;
        break;
      case IN_QUEUE:
        downloadState = DownloadModel.DownloadState.INDETERMINATE;
        break;
      case INSTALLED:
        downloadState = DownloadModel.DownloadState.COMPLETE;
        break;
      case UNINSTALLED:
        downloadState = DownloadModel.DownloadState.COMPLETE;
        break;
      case INSTALLATION_TIMEOUT:
      case GENERIC_ERROR:
        downloadState = DownloadModel.DownloadState.ERROR;
        break;
      case NOT_ENOUGH_SPACE_ERROR:
        downloadState = DownloadModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR;
        break;
      case INSTALLING:
        downloadState = DownloadModel.DownloadState.INSTALLING;
        break;
      default:
        downloadState = DownloadModel.DownloadState.COMPLETE;
        break;
    }
    return downloadState;
  }

  public DownloadModel.Action parseDownloadType(Install.InstallationType type, boolean paidApp,
      boolean wasPaid) {
    DownloadModel.Action action;
    if (paidApp && !wasPaid) {
      action = DownloadModel.Action.PAY;
    } else {
      switch (type) {
        case INSTALLED:
          action = DownloadModel.Action.OPEN;
          break;
        case INSTALL:
          action = DownloadModel.Action.INSTALL;
          break;
        case DOWNGRADE:
          action = DownloadModel.Action.DOWNGRADE;
          break;
        case UPDATE:
          action = DownloadModel.Action.UPDATE;
          break;
        default:
          action = DownloadModel.Action.INSTALL;
          break;
      }
    }
    return action;
  }

  public int parseDownloadAction(DownloadModel.Action action) {
    int downloadAction;
    switch (action) {
      case INSTALL:
        downloadAction = Download.ACTION_INSTALL;
        break;
      case UPDATE:
        downloadAction = Download.ACTION_UPDATE;
        break;
      case DOWNGRADE:
        downloadAction = Download.ACTION_DOWNGRADE;
        break;
      default:
        throw new IllegalArgumentException("Invalid action");
    }
    return downloadAction;
  }

  public Origin getOrigin(int action) {
    switch (action) {
      default:
      case Download.ACTION_INSTALL:
        return Origin.INSTALL;
      case Download.ACTION_UPDATE:
        return Origin.UPDATE;
      case Download.ACTION_DOWNGRADE:
        return Origin.DOWNGRADE;
    }
  }

  public InstallType getInstallType(int action) {
    switch (action) {
      default:
      case Download.ACTION_INSTALL:
        return InstallType.INSTALL;
      case Download.ACTION_UPDATE:
        return InstallType.UPDATE;
      case Download.ACTION_DOWNGRADE:
        return InstallType.DOWNGRADE;
    }
  }
}
