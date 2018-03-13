package cm.aptoide.pt.home.apps;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import java.util.List;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsAdapter extends RecyclerView.Adapter<AppsViewHolder> {

  protected static final int HEADER = 0;
  protected static final int ACTIVE_DOWNLOAD = 1;
  protected static final int STANDBY_DOWNLOAD = 2;
  protected static final int COMPLETED_DOWNLOAD = 3;
  protected static final int ERROR_DOWNLOAD = 4;
  protected static final int UPDATE = 5;
  protected static final int INSTALLED = 6;

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
      case DOWNLOAD:
        type = getDownloadType(((DownloadApp) item).getDownloadStatus());
        break;
      case UPDATE:
        type = UPDATE;
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
    listOfApps.addAll(list);
    notifyDataSetChanged();
  }
}
