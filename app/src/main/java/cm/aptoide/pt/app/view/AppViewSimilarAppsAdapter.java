package cm.aptoide.pt.app.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.AppViewSimilarApp;
import java.text.DecimalFormat;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 11/05/18.
 */

public class AppViewSimilarAppsAdapter extends RecyclerView.Adapter<AppViewSimilarAppViewHolder> {

  List<AppViewSimilarApp> similarApps;
  DecimalFormat oneDecimalFormater;
  PublishSubject<SimilarAppClickEvent> appClicked;

  public AppViewSimilarAppsAdapter(List<AppViewSimilarApp> similarApps,
      DecimalFormat oneDecimalFormater, PublishSubject<SimilarAppClickEvent> appClicked) {
    this.similarApps = similarApps;
    this.oneDecimalFormater = oneDecimalFormater;
    this.appClicked = appClicked;
  }

  @Override public AppViewSimilarAppViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    return new AppViewSimilarAppViewHolder(LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.displayable_grid_ad, viewGroup, false), oneDecimalFormater, appClicked);
  }

  @Override public void onBindViewHolder(AppViewSimilarAppViewHolder appViewSimilarAppViewHolder,
      int position) {
    appViewSimilarAppViewHolder.setSimilarApp(similarApps.get(position), position);
  }

  @Override public int getItemCount() {
    return similarApps.size();
  }

  public void update(List<AppViewSimilarApp> apps) {
    similarApps = apps;
    notifyDataSetChanged();
  }
}
