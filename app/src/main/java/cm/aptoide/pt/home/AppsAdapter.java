package cm.aptoide.pt.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.app.Application;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 07/03/2018.
 */

class AppsAdapter extends RecyclerView.Adapter<AppInBundleViewHolder> {

  private List<Application> apps;

  AppsAdapter(List<Application> apps) {
    this.apps = apps;
  }

  @Override public AppInBundleViewHolder onCreateViewHolder(ViewGroup parent, int position) {
    return new AppInBundleViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.app_home_item, parent, false), PublishSubject.create());
  }

  @Override public void onBindViewHolder(AppInBundleViewHolder viewHolder, int position) {
    viewHolder.setApp(apps.get(position));
  }

  @Override public int getItemCount() {
    return apps.size();
  }

  public void add(List<Application> apps) {
    this.apps.addAll(apps);
    notifyDataSetChanged();
  }
}
