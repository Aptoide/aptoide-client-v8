package cm.aptoide.pt.home.bundles.apps;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.view.app.AppViewHolder;
import cm.aptoide.pt.view.app.Application;
import java.text.DecimalFormat;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 07/03/2018.
 */

class AppsInBundleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final int REWARD_APP = R.layout.reward_app_home_item;
  private static final int APP = R.layout.app_home_item;
  private static final int ESKILLS_APP = R.layout.eskills_app_home_item;
  private final DecimalFormat oneDecimalFormatter;
  private final PublishSubject<HomeEvent> appClickedEvents;
  private HomeBundle homeBundle;
  private int bundlePosition;
  private List<Application> apps;
  private ExperimentClicked experimentClickedEvent;

  AppsInBundleAdapter(List<Application> apps, DecimalFormat oneDecimalFormatter,
      PublishSubject<HomeEvent> appClickedEvents, ExperimentClicked experimentClickedEvent) {
    this.apps = apps;
    this.oneDecimalFormatter = oneDecimalFormatter;
    this.appClickedEvents = appClickedEvents;
    this.homeBundle = null;
    this.bundlePosition = -1;
    this.experimentClickedEvent = experimentClickedEvent;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case REWARD_APP:
        return new RewardAppInBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(REWARD_APP, parent, false), appClickedEvents);
      case APP:
        return new AppInBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(APP, parent, false), appClickedEvents, oneDecimalFormatter);
      case ESKILLS_APP:
        return new EskillsAppInBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(ESKILLS_APP, parent, false), appClickedEvents, oneDecimalFormatter);
      default:
        throw new IllegalArgumentException("Wrong type of App");
    }
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    ((AppViewHolder) viewHolder).setApp(apps.get(position), homeBundle, bundlePosition);
  }

  @Override public int getItemViewType(int position) {
    if (apps.get(position) instanceof RewardApp) {
      return REWARD_APP;
    } else if (apps.get(position) instanceof EskillsApp) {
      return ESKILLS_APP;
    } else {
      return APP;
    }
  }

  @Override public int getItemCount() {
    return apps.size();
  }

  public void update(List<Application> apps) {
    this.apps = apps;
    notifyDataSetChanged();
  }

  public void updateBundle(HomeBundle homeBundle, int position) {
    this.homeBundle = homeBundle;
    this.bundlePosition = position;
  }

}
