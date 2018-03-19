package cm.aptoide.pt.home.apps;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import java.util.List;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsAdapter extends RecyclerView.Adapter<AppsViewHolder> {

  protected static final int HEADER = 0;
  protected static final int HEADER_UPDATES = 1;
  protected static final int ACTIVE_DOWNLOAD = 2;
  protected static final int STANDBY_DOWNLOAD = 3;
  protected static final int COMPLETED_DOWNLOAD = 4;
  protected static final int ERROR_DOWNLOAD = 5;
  protected static final int INSTALLED = 6;
  protected static final int UPDATE = 7;
  protected static final int UPDATING = 8;
  protected static final int STANDBY_UPDATE = 9;
  protected static final int ERROR_UPDATE = 10;

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
      case HEADER:
        type = HEADER;
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

  public void addApps(List<App> list) {
    for (int i = 0; i < list.size(); i++) {
      if (listOfApps.contains(list.get(i))) {
        //update
        int itemIndex = listOfApps.indexOf(list.get(i));
        listOfApps.set(itemIndex, list.get(i));//stores the same item with the new emitted changes
        notifyItemChanged(itemIndex);
      } else {
        //add new element
        listOfApps.add(list.get(i));
        notifyItemInserted(listOfApps.size() - 1);
      }
    }
  }
}
