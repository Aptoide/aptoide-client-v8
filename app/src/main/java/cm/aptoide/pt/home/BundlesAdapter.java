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

public class BundlesAdapter extends RecyclerView.Adapter<AppBundleViewHolder> {
  private static final int SOCIAL = R.layout.social_recommends_bundle_item;
  private static final int EDITORS = R.layout.editors_choice_bundle_item;
  private static final int APPS = R.layout.apps_bundle_item;
  private static final int STORE = R.layout.store_bundle_item;
  private static final int ADS = R.layout.ads_bundle_item;
  private static final int LOADING = R.layout.progress_item;
  private final ProgressBundle progressBundle;
  private final DecimalFormat oneDecimalFormatter;
  private final PublishSubject<Application> appClickedEvents;
  private final PublishSubject<HomeMoreClick> uiEventsListener;
  private final PublishSubject<AppClick> recommendsClickedEvents;
  private List<HomeBundle> bundles;
  private PublishSubject<AdClick> adClickedEvents;

  public BundlesAdapter(List<HomeBundle> bundles, ProgressBundle homeBundle,
      PublishSubject<HomeMoreClick> uiEventsListener, DecimalFormat oneDecimalFormatter,
      PublishSubject<Application> appClickedEvents, PublishSubject<AdClick> adPublishSubject,
      PublishSubject<AppClick> recommendsClickedEvents) {
    this.bundles = bundles;
    this.progressBundle = homeBundle;
    this.uiEventsListener = uiEventsListener;
    this.oneDecimalFormatter = oneDecimalFormatter;
    this.appClickedEvents = appClickedEvents;
    this.adClickedEvents = adPublishSubject;
    this.recommendsClickedEvents = recommendsClickedEvents;
  }

  @Override public AppBundleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case EDITORS:
        return new EditorsBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(EDITORS, parent, false), uiEventsListener, oneDecimalFormatter,
            appClickedEvents);
      case SOCIAL:
        return new SocialBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(SOCIAL, parent, false), recommendsClickedEvents);
      case APPS:
        return new AppsBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(APPS, parent, false), uiEventsListener, oneDecimalFormatter, appClickedEvents);
      case STORE:
        return new StoreBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(STORE, parent, false));
      case ADS:
        return new AdsBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(ADS, parent, false), uiEventsListener, oneDecimalFormatter, adClickedEvents);
      case LOADING:
        return new LoadingBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(LOADING, parent, false));
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
      case SOCIAL:
        return SOCIAL;
      case APPS:
        return APPS;
      case EDITORS:
        return EDITORS;
      case STORE:
        return STORE;
      case ADS:
        return ADS;
      case LOADING:
        return LOADING;
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

  public void update(List<HomeBundle> bundles) {
    this.bundles = bundles;
    notifyDataSetChanged();
  }

  public void add(List<HomeBundle> bundles) {
    this.bundles.addAll(bundles);
    notifyDataSetChanged();
  }

  public void addLoadMore() {
    if (getLoadingPosition() < 0) {
      bundles.add(progressBundle);
      notifyItemInserted(bundles.size() - 1);
    }
  }

  public void removeLoadMore() {
    int loadingPosition = getLoadingPosition();
    if (loadingPosition >= 0) {
      bundles.remove(loadingPosition);
      notifyItemRemoved(loadingPosition);
    }
  }

  public synchronized int getLoadingPosition() {
    for (int i = bundles.size() - 1; i >= 0; i--) {
      HomeBundle homeBundle = bundles.get(i);
      if (homeBundle instanceof ProgressBundle) {
        return i;
      }
    }
    return -1;
  }
}
