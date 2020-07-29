package cm.aptoide.pt.app;

import cm.aptoide.pt.database.room.RoomDownload;
import cm.aptoide.pt.download.InstallType;
import cm.aptoide.pt.download.Origin;
import cm.aptoide.pt.download.view.DownloadStatusModel;
import cm.aptoide.pt.install.Install;

/**
 * Created by filipegoncalves on 5/9/18.
 */

public class DownloadStateParser {

  public DownloadStateParser() {
  }

  public DownloadModel.DownloadState parseDownloadState(Install.InstallationStatus state,
      boolean isIndeterminate) {
    DownloadModel.DownloadState downloadState;
    if (isIndeterminate) {
      downloadState = DownloadModel.DownloadState.INDETERMINATE;
    } else {

      switch (state) {
        case DOWNLOADING:
          downloadState = DownloadModel.DownloadState.ACTIVE;
          break;
        case PAUSED:
          downloadState = DownloadModel.DownloadState.PAUSE;
          break;
        case IN_QUEUE:
        case INITIAL_STATE:
          downloadState = DownloadModel.DownloadState.INDETERMINATE;
          break;
        case INSTALLED:
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
          throw new IllegalStateException("Wrong type of download state");
      }
    }
    return downloadState;
  }

  public DownloadModel.Action parseDownloadType(Install.InstallationType type,
      boolean isMigration) {
    DownloadModel.Action action;
    if (isMigration) {
      action = DownloadModel.Action.MIGRATE;
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

  public DownloadStatusModel.Action parseStatusDownloadType(Install.InstallationType type,
      boolean isMigration) {
    DownloadStatusModel.Action action;
    if (isMigration) {
      action = DownloadStatusModel.Action.MIGRATE;
    } else {
      switch (type) {
        case INSTALLED:
          action = DownloadStatusModel.Action.OPEN;
          break;
        case DOWNGRADE:
          action = DownloadStatusModel.Action.DOWNGRADE;
          break;
        case UPDATE:
          action = DownloadStatusModel.Action.UPDATE;
          break;
        case INSTALL:
        default:
          action = DownloadStatusModel.Action.INSTALL;
          break;
      }
    }
    return action;
  }

  public DownloadStatusModel.DownloadState parseStatusDownloadState(
      Install.InstallationStatus state, boolean isIndeterminate) {
    DownloadStatusModel.DownloadState downloadState;
    if (isIndeterminate) {
      downloadState = DownloadStatusModel.DownloadState.IN_QUEUE;
    } else {
      switch (state) {
        case INSTALLATION_TIMEOUT:
        case GENERIC_ERROR:
          downloadState = DownloadStatusModel.DownloadState.GENERIC_ERROR;
          break;
        case NOT_ENOUGH_SPACE_ERROR:
          downloadState = DownloadStatusModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR;
          break;
        case IN_QUEUE:
          downloadState = DownloadStatusModel.DownloadState.IN_QUEUE;
          break;
        case PAUSED:
          downloadState = DownloadStatusModel.DownloadState.PAUSE;
          break;
        case DOWNLOADING:
          downloadState = DownloadStatusModel.DownloadState.ACTIVE;
          break;
        case INITIAL_STATE:
        case INSTALLED:
        case UNINSTALLED:
          downloadState = DownloadStatusModel.DownloadState.STANDBY;
          break;
        case INSTALLING:
          downloadState = DownloadStatusModel.DownloadState.INSTALLING;
          break;
        default:
          throw new IllegalStateException("Wrong type of download state");
      }
    }
    return downloadState;
  }

  public int parseDownloadAction(DownloadModel.Action action) {
    int downloadAction;
    switch (action) {
      case INSTALL:
        downloadAction = RoomDownload.ACTION_INSTALL;
        break;
      case UPDATE:
        downloadAction = RoomDownload.ACTION_UPDATE;
        break;
      case DOWNGRADE:
        downloadAction = RoomDownload.ACTION_DOWNGRADE;
        break;
      case MIGRATE:
        downloadAction = RoomDownload.ACTION_DOWNGRADE;
        break;
      default:
        throw new IllegalArgumentException("Invalid action " + action.toString());
    }
    return downloadAction;
  }

  public Origin getOrigin(int action) {
    switch (action) {
      default:
      case RoomDownload.ACTION_INSTALL:
        return Origin.INSTALL;
      case RoomDownload.ACTION_UPDATE:
        return Origin.UPDATE;
      case RoomDownload.ACTION_DOWNGRADE:
        return Origin.DOWNGRADE;
    }
  }

  public InstallType getInstallType(int action) {
    switch (action) {
      default:
      case RoomDownload.ACTION_INSTALL:
        return InstallType.INSTALL;
      case RoomDownload.ACTION_UPDATE:
        return InstallType.UPDATE;
      case RoomDownload.ACTION_DOWNGRADE:
        return InstallType.DOWNGRADE;
    }
  }
}
