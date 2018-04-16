package cm.aptoide.pt.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.app.FeatureGraphicApplication;
import java.text.DecimalFormat;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 08/03/2018.
 */

class EditorsAppsAdapter extends RecyclerView.Adapter<FeatureGraphicInBundleViewHolder> {
  private final DecimalFormat oneDecimalFormatter;
  private final PublishSubject<HomeClick> appClickedEvents;
  private List<Application> apps;
  private HomeBundle homeBundle;
  private int bundlePosition;

  public EditorsAppsAdapter(List<Application> apps, DecimalFormat oneDecimalFormatter,
      PublishSubject<HomeClick> appClickedEvents) {
    this.apps = apps;
    this.oneDecimalFormatter = oneDecimalFormatter;
    this.appClickedEvents = appClickedEvents;
    this.homeBundle = null;
    this.bundlePosition = -1;
  }

  @Override
  public FeatureGraphicInBundleViewHolder onCreateViewHolder(ViewGroup parent, int position) {
    return new FeatureGraphicInBundleViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.feature_graphic_home_item, parent, false), oneDecimalFormatter,
        appClickedEvents);
  }

  @Override
  public void onBindViewHolder(FeatureGraphicInBundleViewHolder viewHolder, int position) {
    viewHolder.setFeatureGraphicApplication((FeatureGraphicApplication) apps.get(position),
        homeBundle, bundlePosition, position);
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
