package cm.aptoide.pt.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
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
  private static final int EDITORIAL = R.layout.editorial_action_item;
  private static final int INFO = R.layout.info_action_item;
  private static final int SMALL_BANNER = R.layout.ads_small_banner;
  private final ProgressBundle progressBundle;
  private final DecimalFormat oneDecimalFormatter;
  private final PublishSubject<HomeEvent> uiEventsListener;
  private final String marketName;
  private final AdsBundlesViewHolderFactory adsBundlesViewHolderFactory;
  private List<HomeBundle> bundles;
  private PublishSubject<AdHomeEvent> adClickedEvents;

  public BundlesAdapter(List<HomeBundle> bundles, ProgressBundle homeBundle,
      PublishSubject<HomeEvent> uiEventsListener, DecimalFormat oneDecimalFormatter,
      PublishSubject<AdHomeEvent> adPublishSubject, String marketName,
      AdsBundlesViewHolderFactory adsBundlesViewHolderFactory) {
    this.bundles = bundles;
    this.progressBundle = homeBundle;
    this.uiEventsListener = uiEventsListener;
    this.oneDecimalFormatter = oneDecimalFormatter;
    this.adClickedEvents = adPublishSubject;
    this.marketName = marketName;
    this.adsBundlesViewHolderFactory = adsBundlesViewHolderFactory;
  }

  @Override public AppBundleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case EDITORS:
        return new EditorsBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(EDITORS, parent, false), uiEventsListener, oneDecimalFormatter, marketName);
      case SOCIAL:
        return new SocialBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(SOCIAL, parent, false), uiEventsListener, oneDecimalFormatter);
      case APPS:
        return new AppsBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(APPS, parent, false), uiEventsListener, oneDecimalFormatter, marketName);
      case STORE:
        return new StoreBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(STORE, parent, false));
      case ADS:
        return adsBundlesViewHolderFactory.createViewHolder(parent);
      case INFO:
        return new InfoBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(INFO, parent, false), uiEventsListener);
      case EDITORIAL:
        return new EditorialBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(EDITORIAL, parent, false), uiEventsListener);
      case LOADING:
        return new LoadingBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(LOADING, parent, false));
      case SMALL_BANNER:
        return new SmallBannerAdBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(SMALL_BANNER, parent, false));
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
      case APPCOINS_ADS:
        return APPS;
      case EDITORS:
        return EDITORS;
      case STORE:
        return STORE;
      case ADS:
        return ADS;
      case INFO_BUNDLE:
        return INFO;
      case LOADING:
        return LOADING;
      case EDITORIAL:
        return EDITORIAL;
      case SMALL_BANNER:
        return SMALL_BANNER;
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

  public void addHighlightedAd(AdClick adClick) {
    for (HomeBundle bundle : bundles) {
      if (bundle.getType() == HomeBundle.BundleType.ADS) {
        List<AdClick> content = (List<AdClick>) bundle.getContent();
        if (content == null) return;
        for (int i = 0; i < content.size(); i++) {
          if (content.get(i)
              .getAd()
              .getPackageName()
              .equals(adClick.getAd()
                  .getPackageName())) {
            content.remove(i);
            break;
          }
        }
        content.add(0, adClick);
      }
    }
    notifyDataSetChanged();
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

  public void remove(int bundlePosition) {
    bundles.remove(bundlePosition);
    notifyItemRemoved(bundlePosition);
  }

  public HomeBundle getBundle(int visibleItem) {
    return bundles.get(visibleItem);
  }
}
