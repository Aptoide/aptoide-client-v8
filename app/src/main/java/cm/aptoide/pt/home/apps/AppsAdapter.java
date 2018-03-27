package cm.aptoide.pt.home.apps;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import java.util.List;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsAdapter extends RecyclerView.Adapter<AppsViewHolder> {

  protected static final int HEADER_DOWNLOADS = 0;
  protected static final int HEADER_INSTALLED = 1;
  protected static final int HEADER_UPDATES = 2;
  protected static final int ACTIVE_DOWNLOAD = 3;
  protected static final int STANDBY_DOWNLOAD = 4;
  protected static final int COMPLETED_DOWNLOAD = 5;
  protected static final int ERROR_DOWNLOAD = 6;
  protected static final int INSTALLED = 7;
  protected static final int UPDATE = 8;
  protected static final int UPDATING = 9;
  protected static final int STANDBY_UPDATE = 10;
  protected static final int ERROR_UPDATE = 11;

  private List<App> listOfApps;
  private AppCardViewHolderFactory appCardViewHolderFactory;

  public AppsAdapter(List<App> listOfApps, AppCardViewHolderFactory appCardViewHolderFactory) {
    this.listOfApps = listOfApps;
    this.appCardViewHolderFactory = appCardViewHolderFactory;
  }

  @Override public AppsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return appCardViewHolderFactory.createViewHolder(viewType, parent);
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
        type = getDownloadType(((DownloadApp) item).getDownloadStatus());
        break;
      case UPDATE:
        type = getUpdateType(((UpdateApp) item).getUpdateStatus());
        break;
      case INSTALLED:
        type = INSTALLED;
        break;
      case INSTALLING:
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

  private int getUpdateType(UpdateApp.UpdateStatus updateStatus) {
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
      default:
        throw new IllegalArgumentException("Wrong download status : " + updateStatus.name());
    }
    return type;
  }

  private int getDownloadType(DownloadApp.Status downloadStatus) {
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
      default:
        throw new IllegalArgumentException("Wrong download status : " + downloadStatus.name());
    }
    return type;
  }

  private void addApps(List<App> list, int offset) {
    for (int i = 0; i < list.size(); i++) {
      if (listOfApps.contains(list.get(i))) {
        //update
        int itemIndex = listOfApps.indexOf(list.get(i));
        listOfApps.set(itemIndex, list.get(i));//stores the same item with the new emitted changes
        notifyItemChanged(itemIndex);
      } else {
        //add new element
        listOfApps.add(offset + 1, list.get(i));
        notifyItemInserted(offset + 1);
      }
    }
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
    addApps(installedApps, headerPosition);
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

  public void removeUpdatesList(List<App> excludedUpdatesList) {
    for (App app : excludedUpdatesList) {
      if (app instanceof UpdateApp) {
        if (listOfApps.contains(((UpdateApp) app))) {
          int indexOfExcludedApp = listOfApps.indexOf(((UpdateApp) app));
          listOfApps.remove(indexOfExcludedApp);
          notifyItemRemoved(indexOfExcludedApp);
        }
      }
    }
  }
}
