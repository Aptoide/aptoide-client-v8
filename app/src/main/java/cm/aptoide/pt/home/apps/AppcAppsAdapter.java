package cm.aptoide.pt.home.apps;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.subjects.PublishSubject;

public class AppcAppsAdapter extends RecyclerView.Adapter<AppsViewHolder> {

  private final PublishSubject<AppClick> appItemClicks;
  private List<App> listOfApps;

  public AppcAppsAdapter(List<App> listOfApps, PublishSubject<AppClick> appItemClicks) {
    this.listOfApps = listOfApps;
    this.appItemClicks = appItemClicks;
  }

  @Override public AppsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new AppcAppViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.apps_appc_upgrade_app_item, parent, false), appItemClicks);
  }

  @Override public void onBindViewHolder(AppsViewHolder holder, int position) {
    holder.setApp(listOfApps.get(position));
  }

  @Override public int getItemCount() {
    return listOfApps.size();
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

  private void addApps(List<App> list) {
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
}
