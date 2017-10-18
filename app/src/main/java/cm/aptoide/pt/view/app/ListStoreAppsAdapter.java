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
    return new AppViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.displayable_grid_app, parent, false), appClicks);
  }

  @Override public void onBindViewHolder(AppViewHolder holder, int position) {
    holder.setApp(list.get(position));
  }

  @Override public int getItemCount() {
    return list.size();
  }

  public void setApps(List<Application> list) {
    this.list = list;
    notifyDataSetChanged();
  }
}
