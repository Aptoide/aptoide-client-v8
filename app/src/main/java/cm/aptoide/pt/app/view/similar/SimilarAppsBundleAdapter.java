package cm.aptoide.pt.app.view.similar;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.view.similar.bundles.SimilarAppcAppsViewHolder;
import cm.aptoide.pt.app.view.similar.bundles.SimilarAppsViewHolder;
import java.text.DecimalFormat;
import java.util.List;
import rx.subjects.PublishSubject;

public class SimilarAppsBundleAdapter extends RecyclerView.Adapter<SimilarBundleViewHolder> {
  private static final int APPS = R.layout.appview_similar_layout;
  private static final int APPC_APPS = R.layout.appview_similar_appc_layout;

  private final DecimalFormat decimalFormat;
  private final PublishSubject<SimilarAppClickEvent> similarAppClick;
  private List<SimilarAppsBundle> bundles;

  public SimilarAppsBundleAdapter(List<SimilarAppsBundle> bundles, DecimalFormat decimalFormat,
      PublishSubject<SimilarAppClickEvent> similarAppClick) {
    this.bundles = bundles;
    this.decimalFormat = decimalFormat;
    this.similarAppClick = similarAppClick;
  }

  @Override public SimilarBundleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case APPS:
        return new SimilarAppsViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(APPS, parent, false), decimalFormat, similarAppClick);
      case APPC_APPS:
        return new SimilarAppcAppsViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(APPC_APPS, parent, false), decimalFormat, similarAppClick);
      default:
        throw new IllegalStateException("Invalid bundle view type");
    }
  }

  @Override public void onBindViewHolder(SimilarBundleViewHolder holder, int position) {
    holder.setBundle(bundles.get(position), position);
  }

  @Override public int getItemViewType(int position) {
    switch (bundles.get(position)
        .getType()) {
      case APPS:
        return APPS;
      case APPC_APPS:
        return APPC_APPS;
      default:
        throw new IllegalStateException(
            "Bundle type not supported by the adapter: " + bundles.get(position)
                .getType()
                .name());
    }
  }

  @Override public int getItemCount() {
    return bundles.size();
  }

  public void update(List<SimilarAppsBundle> bundles) {
    this.bundles = bundles;
    notifyDataSetChanged();
  }

  public void add(SimilarAppsBundle bundles) {
    this.bundles.add(bundles);
    notifyDataSetChanged();
  }

  public void add(List<SimilarAppsBundle> bundles) {
    this.bundles.addAll(bundles);
    notifyItemInserted(bundles.size() - 1);
  }
}
