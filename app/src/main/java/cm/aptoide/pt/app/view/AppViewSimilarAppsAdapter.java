package cm.aptoide.pt.app.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.AppViewSimilarApp;
import cm.aptoide.pt.app.view.similar.SimilarAppClickEvent;
import java.text.DecimalFormat;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 11/05/18.
 */

public class AppViewSimilarAppsAdapter extends RecyclerView.Adapter<AppViewSimilarAppViewHolder> {

  private List<AppViewSimilarApp> similarApps;
  private DecimalFormat oneDecimalFormater;
  private PublishSubject<SimilarAppClickEvent> appClicked;
  private SimilarAppType type;

  public AppViewSimilarAppsAdapter(List<AppViewSimilarApp> similarApps,
      DecimalFormat oneDecimalFormater, PublishSubject<SimilarAppClickEvent> appClicked,
      SimilarAppType type) {
    this.similarApps = similarApps;
    this.oneDecimalFormater = oneDecimalFormater;
    this.appClicked = appClicked;
    this.type = type;
  }

  @Override
  public AppViewSimilarAppViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    return new AppViewSimilarAppViewHolder(LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.displayable_grid_ad, viewGroup, false), oneDecimalFormater, appClicked);
  }

  @Override public void onBindViewHolder(AppViewSimilarAppViewHolder appViewSimilarAppViewHolder,
      int position) {
    if (similarApps.get(position) != null) {
      appViewSimilarAppViewHolder.setSimilarApp(similarApps.get(position), type);
    }
  }

  @Override public int getItemViewType(int position) {
    return similarApps.get(position)
        .getNetworkAdType();
  }

  @Override public int getItemCount() {
    return similarApps.size();
  }

  public void update(List<AppViewSimilarApp> apps) {
    similarApps = apps;
    notifyDataSetChanged();
  }

  public enum SimilarAppType {

    APPC_SIMILAR_APPS("appc_similar_apps"), SIMILAR_APPS("similar_apps");

    private final String description;

    SimilarAppType(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }
}
