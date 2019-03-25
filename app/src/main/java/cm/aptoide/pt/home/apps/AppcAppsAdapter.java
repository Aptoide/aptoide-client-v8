package cm.aptoide.pt.home.apps;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.home.apps.AppsAdapter.INSTALLED;

public class AppcAppsAdapter extends RecyclerView.Adapter<AppsViewHolder> {

  protected static final int UPDATE = 1;
  static final int UPDATING = 2;
  static final int STANDBY_UPDATE = 3;
  static final int ERROR_UPDATE = 4;
  static final int PAUSING_UPDATE = 5;
  private final PublishSubject<AppClick> appItemClicks;
  private List<App> listOfApps;

  public AppcAppsAdapter(List<App> listOfApps, PublishSubject<AppClick> appItemClicks) {
    this.listOfApps = listOfApps;
    this.appItemClicks = appItemClicks;
  }

  @Override public AppsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    AppsViewHolder appViewHolder;
    switch (viewType) {
      case UPDATE:
        appViewHolder = new AppcAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_appc_upgrade_app_item, parent, false), appItemClicks);
        break;
      case UPDATING:
        appViewHolder = new UpdatingAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_updating_app_item, parent, false), appItemClicks, true);
        break;
      case STANDBY_UPDATE:
        appViewHolder = new StandByUpdateAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_standby_update_app_item, parent, false), appItemClicks, true);
        break;
      case ERROR_UPDATE:
        appViewHolder = new ErrorUpgradeAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_error_update_app_item, parent, false), appItemClicks);
        break;
      case PAUSING_UPDATE:
        appViewHolder = new StandByUpdateAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_standby_update_app_item, parent, false), appItemClicks, true);
        break;
      default:
        throw new IllegalStateException("Wrong cardType" + viewType);
    }
    return appViewHolder;
  }

  @Override public void onBindViewHolder(AppsViewHolder holder, int position) {
    holder.setApp(listOfApps.get(position));
  }

  @Override public int getItemViewType(int position) {
    App item = listOfApps.get(position);
    int type;
    switch (item.getType()) {
      case UPDATE:
        type = getUpdateType(((UpdateApp) item).getStatus());
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

  private int getUpdateType(StateApp.Status updateStatus) {
    int type;
    switch (updateStatus) {
      case APPC_UPGRADE:
        type = UPDATE;
        break;
      case APPC_UPGRADING:
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
      default:
        throw new IllegalArgumentException("Wrong download status : " + updateStatus.name());
    }
    return type;
  }

  public void setAvailableUpgradesList(List<App> list) {
    listOfApps.removeAll(getUpdatesToRemove(list));
    notifyDataSetChanged();
    addApps(list);
    Collections.sort(listOfApps, (app1, app2) -> {
      if (app1.getType() == App.Type.UPDATE && app2.getType() == App.Type.UPDATE) {
        return ((UpdateApp) app1).getName()
            .compareTo(((UpdateApp) app2).getName());
      } else {
        return 0;
      }
    });
  }

  public void addApps(List<App> list) {
    for (int i = 0; i < list.size(); i++) {
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
        listOfApps.add(list.get(i));
        notifyItemInserted(i);
      }
    }
  }

  private void updateApp(List<App> list, int i, int itemIndex) {
    listOfApps.set(itemIndex, list.get(i));
    notifyItemChanged(itemIndex);
  }

  private boolean shouldUpdatePausingApp(StateApp app) {
    return app.getStatus() == StateApp.Status.STANDBY || app.getStatus() == StateApp.Status.ERROR;
  }

  private boolean shouldUpdateStateApp(StateApp actualApp, StateApp newApp) {
    boolean hasSameStatus = actualApp.getStatus() == newApp.getStatus();
    boolean hasSameProgress = actualApp.getProgress() == newApp.getProgress();
    boolean hasSameIndeterminateStatus = (actualApp.isIndeterminate() == newApp.isIndeterminate());
    return !hasSameStatus || !hasSameProgress || !hasSameIndeterminateStatus;
  }

  private List<App> getUpdatesToRemove(List<App> updatesList) {
    List<App> updatesToRemove = getUpdateApps();
    updatesToRemove.removeAll(updatesList);
    return updatesToRemove;
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
}
