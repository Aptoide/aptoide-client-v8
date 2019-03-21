package cm.aptoide.pt.home.apps;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsAdapter extends RecyclerView.Adapter<AppsViewHolder> {

  protected static final int INSTALLED = 7;
  protected static final int UPDATE = 8;
  static final int HEADER_DOWNLOADS = 0;
  static final int HEADER_INSTALLED = 1;
  static final int HEADER_UPDATES = 2;
  static final int ACTIVE_DOWNLOAD = 3;
  static final int STANDBY_DOWNLOAD = 4;
  static final int COMPLETED_DOWNLOAD = 5;
  static final int ERROR_DOWNLOAD = 6;
  static final int UPDATING = 9;
  static final int STANDBY_UPDATE = 10;
  static final int ERROR_UPDATE = 11;
  static final int PAUSING_UPDATE = 12;
  static final int PAUSING_DOWNLOAD = 13;
  static final int INSTALLING = 14;

  private List<App> listOfApps;
  private AppsCardViewHolderFactory appsCardViewHolderFactory;

  public AppsAdapter(List<App> listOfApps, AppsCardViewHolderFactory appsCardViewHolderFactory) {
    this.listOfApps = listOfApps;
    this.appsCardViewHolderFactory = appsCardViewHolderFactory;
  }

  @Override public AppsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return appsCardViewHolderFactory.createViewHolder(viewType, parent);
  }

  @Override public void onBindViewHolder(AppsViewHolder appsViewHolder, int position) {
    appsViewHolder.setApp(listOfApps.get(position));
  }

  @Override public int getItemViewType(int position) {
    App item = listOfApps.get(position);
    int type;
    switch (item.getType()) {
      case HEADER_DOWNLOADS:
        type = HEADER_DOWNLOADS;
        break;
      case HEADER_INSTALLED:
        type = HEADER_INSTALLED;
        break;
      case HEADER_UPDATES:
        type = HEADER_UPDATES;
        break;
      case DOWNLOAD:
        type = getDownloadType(((DownloadApp) item).getStatus());
        break;
      case UPDATE:
        type = getUpdateType(((UpdateApp) item).getStatus());
        break;
      case INSTALLED:
        type = INSTALLED;
        break;
      default:
        throw new IllegalArgumentException("Invalid type of App");
    }
    return type;
  }

  @Override public int getItemCount() {
    return listOfApps.size();
  }

  private int getUpdateType(StateApp.Status updateStatus) {
    int type;
    switch (updateStatus) {
      case UPDATE:
        type = UPDATE;
        break;
      case UPDATING:
        type = UPDATING;
        break;
      case STANDBY:
        type = STANDBY_UPDATE;
        break;
      case ERROR:
        type = ERROR_UPDATE;
        break;
      case PAUSING:
        type = PAUSING_UPDATE;
        break;
      case INSTALLING:
        type = INSTALLING;
        break;
      default:
        throw new IllegalArgumentException("Wrong download status : " + updateStatus.name());
    }
    return type;
  }

  private int getDownloadType(StateApp.Status downloadStatus) {
    int type;
    switch (downloadStatus) {

      case ACTIVE:
        type = ACTIVE_DOWNLOAD;
        break;
      case STANDBY:
        type = STANDBY_DOWNLOAD;
        break;
      case COMPLETED:
        type = COMPLETED_DOWNLOAD;
        break;
      case ERROR:
        type = ERROR_DOWNLOAD;
        break;
      case PAUSING:
        type = PAUSING_DOWNLOAD;
        break;
      case INSTALLING:
        type = INSTALLING;
        break;
      default:
        throw new IllegalArgumentException("Wrong download status : " + downloadStatus.name());
    }
    return type;
  }

  private void addApps(List<App> list, int offset) {
    for (int i = 0; i < list.size(); i++) {
      if (isValid(list.get(i))) {
        if (listOfApps.contains(list.get(i))) {
          //update
          int itemIndex = listOfApps.indexOf(list.get(i));
          App actualApp = listOfApps.get(itemIndex);
          App newApp = list.get(i);

          if (actualApp instanceof StateApp && newApp instanceof StateApp) {
            if (shouldUpdateStateApp(((StateApp) actualApp), ((StateApp) newApp))) {

              if (((StateApp) actualApp).getStatus() == StateApp.Status.PAUSING) {
                if (shouldUpdatePausingApp(((StateApp) newApp))) {
                  updateApp(list, i, itemIndex);
                }
              } else {
                updateApp(list, i, itemIndex);
              }
            }
          } else {
            if (list.get(i) != listOfApps.get(itemIndex)) {
              updateApp(list, i, itemIndex);
            }
          }
        } else {
          //add new element
          listOfApps.add(offset + 1, list.get(i));
          notifyItemInserted(offset + 1);
        }
      } else {
        if (listOfApps.contains(list.get(i))) {
          int itemIndex = listOfApps.indexOf(list.get(i));
          listOfApps.remove(itemIndex);
          notifyItemRemoved(itemIndex);
        }
      }
    }
  }

  private boolean shouldUpdateStateApp(StateApp actualApp, StateApp newApp) {
    boolean hasSameStatus = actualApp.getStatus() == newApp.getStatus();
    boolean hasSameProgress = actualApp.getProgress() == newApp.getProgress();
    boolean hasSameIndeterminateStatus = (actualApp.isIndeterminate() == newApp.isIndeterminate());
    return !hasSameStatus || !hasSameProgress || !hasSameIndeterminateStatus;
  }

  private void updateApp(List<App> list, int i, int itemIndex) {
    listOfApps.set(itemIndex, list.get(i));//stores the same item with the new emitted changes
    notifyItemChanged(itemIndex);
  }

  private boolean shouldUpdatePausingApp(StateApp app) {
    return app.getStatus() == StateApp.Status.STANDBY || app.getStatus() == StateApp.Status.ERROR;
  }

  public void addUpdateAppsList(List<App> updatesList) {
    int headerPosition = findHeaderPosition(App.Type.HEADER_UPDATES);
    if (headerPosition == -1) {//there is no updates header
      headerPosition = findLastDownloadPosition();
      listOfApps.add(headerPosition + 1, new Header(App.Type.HEADER_UPDATES));
      notifyItemInserted(headerPosition + 1);
      headerPosition++;
    }
    addApps(updatesList, headerPosition);
  }

  private int findLastDownloadPosition() {
    int lastDownloadPosition = -1;
    for (int i = 0; i < listOfApps.size(); i++) {
      if (listOfApps.get(i)
          .getType() == App.Type.DOWNLOAD) {
        lastDownloadPosition = i;
      }
    }
    return lastDownloadPosition;
  }

  public void addInstalledAppsList(List<App> installedApps) {
    int headerPosition = findHeaderPosition(App.Type.HEADER_INSTALLED);
    if (headerPosition == -1) {
      headerPosition = findLastUpdatePosition();
      if (headerPosition == -1) {//there are no updates
        headerPosition = findLastDownloadPosition();
      }
      listOfApps.add(headerPosition + 1, new Header(App.Type.HEADER_INSTALLED));
      notifyItemInserted(headerPosition + 1);
      headerPosition++;
    }
    listOfApps.addAll(headerPosition + 1, installedApps);
  }

  private int findLastUpdatePosition() {
    int lastUpdatePosition = -1;
    for (int i = 0; i < listOfApps.size(); i++) {
      if (listOfApps.get(i)
          .getType() == App.Type.UPDATE) {
        lastUpdatePosition = i;
      }
    }
    return lastUpdatePosition;
  }

  public void addDownloadAppsList(List<App> downloadsList) {
    int headerPosition = findHeaderPosition(App.Type.HEADER_DOWNLOADS);
    if (headerPosition == -1) {//no downloads header
      listOfApps.add(headerPosition + 1, new Header(App.Type.HEADER_DOWNLOADS));
      headerPosition++;
      notifyItemInserted(headerPosition + 1);
    }
    addApps(downloadsList, headerPosition);
  }

  private int findHeaderPosition(App.Type headerToFind) {
    if (headerToFind != App.Type.HEADER_DOWNLOADS
        && headerToFind != App.Type.HEADER_INSTALLED
        && headerToFind != App.Type.HEADER_UPDATES) {
      throw new IllegalArgumentException("The argument must be a type of header ");
    }

    for (int i = 0; i < listOfApps.size(); i++) {
      if (listOfApps.get(i)
          .getType() == headerToFind) {
        return i;
      }
    }
    return -1;
  }

  public void removeUpdatesList(List<App> updatesToRemove) {
    for (App app : updatesToRemove) {
      if (app instanceof UpdateApp) {
        if (listOfApps.contains(((UpdateApp) app))) {
          int indexOfExcludedApp = listOfApps.indexOf(((UpdateApp) app));
          listOfApps.remove(indexOfExcludedApp);
          notifyItemRemoved(indexOfExcludedApp);
        }
      }
    }
  }

  public void removeInstalledDownloads(List<App> installedDownloadsList) {
    for (App app : installedDownloadsList) {
      if (listOfApps.contains(((DownloadApp) app))) {
        int indexOfInstalledApp = listOfApps.indexOf(((DownloadApp) app));
        listOfApps.remove(((DownloadApp) app));
        notifyItemRemoved(indexOfInstalledApp);
      }
    }
    removeDownloadsHeader();
  }

  private void removeDownloadsHeader() {
    if (countNumberOfAppsByType(App.Type.DOWNLOAD) == 0) {
      int downloadsHeaderPosition = findHeaderPosition(App.Type.HEADER_DOWNLOADS);
      if (downloadsHeaderPosition > -1) {
        listOfApps.remove(downloadsHeaderPosition);
        notifyItemRemoved(downloadsHeaderPosition);
      }
    }
  }

  private int countNumberOfAppsByType(App.Type type) {
    int appsByType = 0;
    for (App app : listOfApps) {
      if (app.getType() == type) {
        appsByType++;
      }
    }
    return appsByType;
  }

  public void removeCanceledAppDownload(App app) {
    if (listOfApps.contains(app)) {
      int indexOfCanceledDownload = listOfApps.indexOf(app);
      listOfApps.remove(app);
      notifyItemRemoved(indexOfCanceledDownload);
    }
  }

  private boolean isValid(App app) {
    boolean isValid;
    switch (app.getType()) {
      case HEADER_DOWNLOADS:
      case HEADER_INSTALLED:
      case HEADER_UPDATES:
        isValid = true;
        break;
      case DOWNLOAD:
        isValid = !TextUtils.isEmpty(((DownloadApp) app).getName());
        break;
      case UPDATE:
        isValid = !TextUtils.isEmpty(((UpdateApp) app).getName());
        break;
      case INSTALLED:
        isValid = !TextUtils.isEmpty(((InstalledApp) app).getAppName());
        break;
      default:
        isValid = false;
        break;
    }
    return isValid;
  }

  public List<App> getUpdateApps() {
    List<App> updateApps = new ArrayList<>();
    for (App app : listOfApps) {
      if (app.getType() == App.Type.UPDATE) {
        updateApps.add(app);
      }
    }
    return updateApps;
  }

  public void setAvailableUpdatesList(List<App> availableUpdates) {
    listOfApps.removeAll(getUpdatesToRemove(availableUpdates));
    notifyDataSetChanged();
    addUpdateAppsList(availableUpdates);
    Collections.sort(listOfApps, (app1, app2) -> {
      if (app1.getType() == App.Type.UPDATE && app2.getType() == App.Type.UPDATE) {
        return ((UpdateApp) app1).getName()
            .compareTo(((UpdateApp) app2).getName());
      } else {
        return 0;
      }
    });
  }

  private List<App> getUpdatesToRemove(List<App> updatesList) {
    List<App> updatesToRemove = getUpdateApps();
    updatesToRemove.removeAll(updatesList);
    return updatesToRemove;
  }

  public void setAppStandby(App app) {
    int indexOfApp = listOfApps.indexOf(app);
    if (indexOfApp != -1) {
      App application = listOfApps.get(indexOfApp);
      if (application instanceof StateApp) {
        setIndeterminate(indexOfApp, (StateApp) application);
      }
    }
  }

  private void setIndeterminate(int indexOfApp, StateApp application) {
    application.setIndeterminate(true);
    application.setStatus(StateApp.Status.STANDBY);
    notifyItemChanged(indexOfApp);
  }

  public void setAllUpdatesIndeterminate() {
    List<App> updatesList = getUpdateApps();
    for (App app : updatesList) {
      setAppStandby(((UpdateApp) app));
    }
  }

  public void setAppOnPausing(App app) {
    int indexOfApp = listOfApps.indexOf(app);
    if (indexOfApp != -1) {
      App application = listOfApps.get(indexOfApp);
      if (application instanceof StateApp) {
        setAppPausing(indexOfApp, ((StateApp) application));
      }
    }
  }

  private void setAppPausing(int indexOfApp, StateApp application) {
    application.setStatus(StateApp.Status.PAUSING);
    application.setIndeterminate(true);
    notifyItemChanged(indexOfApp);
  }
}
