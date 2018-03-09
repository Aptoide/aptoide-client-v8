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
  private List<Application> apps;

  public EditorsAppsAdapter(List<Application> apps, DecimalFormat oneDecimalFormatter) {
    this.apps = apps;
    this.oneDecimalFormatter = oneDecimalFormatter;
  }

  @Override
  public FeatureGraphicInBundleViewHolder onCreateViewHolder(ViewGroup parent, int position) {
    return new FeatureGraphicInBundleViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.feature_graphic_home_item, parent, false), PublishSubject.create(),
        oneDecimalFormatter);
  }

  @Override
  public void onBindViewHolder(FeatureGraphicInBundleViewHolder viewHolder, int position) {
    viewHolder.setFeatureGraphicApplication((FeatureGraphicApplication) apps.get(position));
  }

  @Override public int getItemCount() {
    return apps.size();
  }

  public void update(List<Application> apps) {
    this.apps = apps;
    notifyDataSetChanged();
  }
}
