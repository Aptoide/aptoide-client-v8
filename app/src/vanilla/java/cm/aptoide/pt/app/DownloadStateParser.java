package cm.aptoide.pt.app;

import cm.aptoide.pt.install.Install;

/**
 * Created by filipegoncalves on 5/9/18.
 */

public class DownloadStateParser {

  public DownloadStateParser() {
  }

  public DownloadAppViewModel.DownloadState parseDownloadState(Install.InstallationStatus state) {
    DownloadAppViewModel.DownloadState downloadState;
    switch (state) {
      case INSTALLING:
        downloadState = DownloadAppViewModel.DownloadState.ACTIVE;
        break;
      case PAUSED:
        downloadState = DownloadAppViewModel.DownloadState.PAUSE;
        break;
      case IN_QUEUE:
        downloadState = DownloadAppViewModel.DownloadState.INDETERMINATE;
        break;
      case INSTALLED:
        downloadState = DownloadAppViewModel.DownloadState.COMPLETE;
        break;
      case UNINSTALLED:
        downloadState = DownloadAppViewModel.DownloadState.COMPLETE;
        break;
      case INSTALLATION_TIMEOUT:
      case GENERIC_ERROR:
      case NOT_ENOUGH_SPACE_ERROR:
        downloadState = DownloadAppViewModel.DownloadState.ERROR;
        break;
      default:
        downloadState = DownloadAppViewModel.DownloadState.COMPLETE;
    }
    return downloadState;
  }

  public DownloadAppViewModel.Action parseDownloadAction(Install.InstallationType type) {
    DownloadAppViewModel.Action action;
    switch (type) {
      case INSTALLED:
        action = DownloadAppViewModel.Action.OPEN;
        break;
      case INSTALL:
        action = DownloadAppViewModel.Action.INSTALL;
        break;
      case DOWNGRADE:
        action = DownloadAppViewModel.Action.DOWNGRADE;
        break;
      case UPDATE:
        action = DownloadAppViewModel.Action.UPDATE;
        break;
      default:
        action = DownloadAppViewModel.Action.INSTALL;
    }
    return action;
  }
}
