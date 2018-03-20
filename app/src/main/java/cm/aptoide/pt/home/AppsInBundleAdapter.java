package cm.aptoide.pt.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.app.Application;
import java.text.DecimalFormat;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 07/03/2018.
 */

class AppsInBundleAdapter extends RecyclerView.Adapter<AppInBundleViewHolder> {

  private final DecimalFormat oneDecimalFormatter;
  private final PublishSubject<Application> appClickedEvents;
  private List<Application> apps;

  AppsInBundleAdapter(List<Application> apps, DecimalFormat oneDecimalFormatter,
      PublishSubject<Application> appClickedEvents) {
    this.apps = apps;
    this.oneDecimalFormatter = oneDecimalFormatter;
    this.appClickedEvents = appClickedEvents;
  }

  @Override public AppInBundleViewHolder onCreateViewHolder(ViewGroup parent, int position) {
    return new AppInBundleViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.app_home_item, parent, false), appClickedEvents, oneDecimalFormatter);
  }

  @Override public void onBindViewHolder(AppInBundleViewHolder viewHolder, int position) {
    viewHolder.setApp(apps.get(position));
  }

  @Override public int getItemCount() {
    return apps.size();
  }

  public void update(List<Application> apps) {
    this.apps = apps;
    notifyDataSetChanged();
  }
}
