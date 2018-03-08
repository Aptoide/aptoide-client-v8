package cm.aptoide.pt.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class BundlesAdapter extends RecyclerView.Adapter<AppBundleViewHolder> {
  private static final int EDITORS = R.layout.editors_choice_bundle_item;
  private static final int APPS = R.layout.apps_bundle_item;
  private static final int STORE = R.layout.store_bundle_item;
  private List<AppBundle> bundles;
  private PublishSubject<AppBundle> uiEventsListener;

  public BundlesAdapter(List<AppBundle> bundles, PublishSubject<AppBundle> uiEventsListener) {
    this.bundles = bundles;
    this.uiEventsListener = uiEventsListener;
  }

  @Override public AppBundleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case EDITORS:
        return new EditorsBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(EDITORS, parent, false), uiEventsListener);
      case APPS:
        return new AppsBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(APPS, parent, false), uiEventsListener);
      case STORE:
        return new StoreBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(STORE, parent, false));
      default:
        throw new IllegalStateException("Invalid bundle view type");
    }
  }

  @Override public void onBindViewHolder(AppBundleViewHolder appBundleViewHolder, int position) {
    appBundleViewHolder.setBundle(bundles.get(position), position);
  }

  @Override public int getItemViewType(int position) {
    switch (bundles.get(position)
        .getType()) {
      case APPS:
        return APPS;
      case EDITORS:
        return EDITORS;
      case STORE:
        return STORE;
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

  public void add(List<AppBundle> bundles) {
    this.bundles.addAll(bundles);
    notifyDataSetChanged();
  }
}
