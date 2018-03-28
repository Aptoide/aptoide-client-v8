package cm.aptoide.pt.view.app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by trinkes on 17/10/2017.
 */

class ListStoreAppsAdapter extends RecyclerView.Adapter<AppViewHolder> {
  private final PublishSubject<Application> appClicks;
  private List<Application> list;

  public ListStoreAppsAdapter(List<Application> list, PublishSubject<Application> appClicks) {
    this.list = list;
    this.appClicks = appClicks;
  }

  @Override public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == R.layout.search_ad_loading_list_item) {
      return new AppLoadingViewHolder(LayoutInflater.from(parent.getContext())
          .inflate(R.layout.search_ad_loading_list_item, parent, false));
    } else {
      return new ApplicationViewHolder(LayoutInflater.from(parent.getContext())
          .inflate(R.layout.app_home_item, parent, false), appClicks);
    }
  }

  @Override public void onBindViewHolder(AppViewHolder holder, int position) {
    holder.setApp(list.get(position));
  }

  @Override public int getItemViewType(int position) {
    Application application = list.get(position);
    if (application instanceof AppLoading) {
      return R.layout.search_ad_loading_list_item;
    } else {
      return R.layout.app_home_item;
    }
  }

  @Override public int getItemCount() {
    return list.size();
  }

  public void addApps(List<Application> applicationList) {
    int loadingPosition = getLoadingPosition();
    int firstInsertedIndex;
    if (loadingPosition >= 0) {
      this.list.addAll(loadingPosition, applicationList);
      firstInsertedIndex = loadingPosition;
    } else {
      firstInsertedIndex = this.list.size();
      this.list.addAll(applicationList);
    }
    notifyItemRangeInserted(firstInsertedIndex, applicationList.size());
  }

  public void showLoading() {
    if (getLoadingPosition() < 0) {
      list.add(new AppLoading());
      notifyItemInserted(list.size() - 1);
    }
  }

  public void hideLoading() {
    int loadingPosition = getLoadingPosition();
    if (loadingPosition >= 0) {
      list.remove(loadingPosition);
      notifyItemRemoved(loadingPosition);
    }
  }

  public int getLoadingPosition() {
    for (int i = list.size() - 1; i >= 0; i--) {
      Application application = list.get(i);
      if (application instanceof AppLoading) {
        return i;
      }
    }
    return -1;
  }

  public Application getItem(int position) {
    return list.get(position);
  }

  public void setApps(List<Application> apps) {
    this.list = apps;
    notifyDataSetChanged();
  }
}
