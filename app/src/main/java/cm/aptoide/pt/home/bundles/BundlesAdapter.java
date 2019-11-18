package cm.aptoide.pt.home.bundles;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.editorial.CaptionBackgroundPainter;
import cm.aptoide.pt.home.bundles.ads.AdClick;
import cm.aptoide.pt.home.bundles.ads.AdsBundlesViewHolderFactory;
import cm.aptoide.pt.home.bundles.ads.WalletAdsOfferViewHolder;
import cm.aptoide.pt.home.bundles.ads.banner.SmallBannerAdBundleViewHolder;
import cm.aptoide.pt.home.bundles.appcoins.EarnAppCoinsViewHolder;
import cm.aptoide.pt.home.bundles.apps.AppsBundleViewHolder;
import cm.aptoide.pt.home.bundles.base.ActionBundle;
import cm.aptoide.pt.home.bundles.base.AppBundleViewHolder;
import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.home.bundles.editorial.EditorialBundleViewHolder;
import cm.aptoide.pt.home.bundles.editorschoice.EditorsBundleViewHolder;
import cm.aptoide.pt.home.bundles.info.InfoBundleViewHolder;
import cm.aptoide.pt.home.bundles.misc.ErrorHomeBundle;
import cm.aptoide.pt.home.bundles.misc.LoadingBundleViewHolder;
import cm.aptoide.pt.home.bundles.misc.LoadingMoreErrorViewHolder;
import cm.aptoide.pt.home.bundles.misc.ProgressBundle;
import cm.aptoide.pt.home.bundles.top.TopBundleViewHolder;
import java.text.DecimalFormat;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class BundlesAdapter extends RecyclerView.Adapter<AppBundleViewHolder> {
  private static final int EDITORS = 1;
  private static final int APPS = 2;
  private static final int ADS = 3;
  private static final int LOADING = 4;
  private static final int EDITORIAL = 5;
  private static final int INFO = 6;
  private static final int SMALL_BANNER = 7;
  private static final int WALLET_ADS_OFFER = 8;
  private static final int TOP = 9;
  private static final int LOAD_MORE_ERROR = 10;
  private static final int EARN_APPCOINS = 11;
  private final ProgressBundle progressBundle;
  private final DecimalFormat oneDecimalFormatter;
  private final PublishSubject<HomeEvent> uiEventsListener;
  private final String marketName;
  private final AdsBundlesViewHolderFactory adsBundlesViewHolderFactory;
  private final CaptionBackgroundPainter captionBackgroundPainter;
  private List<HomeBundle> bundles;
  private ErrorHomeBundle errorBundle;

  public BundlesAdapter(List<HomeBundle> bundles, ProgressBundle homeBundle,
      ErrorHomeBundle errorBundle, DecimalFormat oneDecimalFormatter,
      PublishSubject<HomeEvent> uiEventsListener,
      AdsBundlesViewHolderFactory adsBundlesViewHolderFactory,
      CaptionBackgroundPainter captionBackgroundPainter, String marketName) {
    this.bundles = bundles;
    this.progressBundle = homeBundle;
    this.errorBundle = errorBundle;
    this.uiEventsListener = uiEventsListener;
    this.oneDecimalFormatter = oneDecimalFormatter;
    this.marketName = marketName;
    this.adsBundlesViewHolderFactory = adsBundlesViewHolderFactory;
    this.captionBackgroundPainter = captionBackgroundPainter;
  }

  @Override public AppBundleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case EDITORS:
        return new EditorsBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.editors_choice_bundle_item, parent, false), uiEventsListener,
            oneDecimalFormatter, marketName);
      case APPS:
        return new AppsBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_bundle_item, parent, false), uiEventsListener,
            oneDecimalFormatter, marketName);
      case ADS:
        return adsBundlesViewHolderFactory.createViewHolder(parent);
      case INFO:
        return new InfoBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.info_action_item_card, parent, false), uiEventsListener);
      case EDITORIAL:
        return new EditorialBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.editorial_action_item, parent, false), uiEventsListener,
            captionBackgroundPainter);
      case LOADING:
        return new LoadingBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.progress_item, parent, false));
      case SMALL_BANNER:
        return new SmallBannerAdBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.ads_small_banner, parent, false));
      case WALLET_ADS_OFFER:
        return new WalletAdsOfferViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.info_action_item_card, parent, false), uiEventsListener);
      case TOP:
        return new TopBundleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.top_bundle_item, parent, false), uiEventsListener,
            oneDecimalFormatter, marketName);
      case LOAD_MORE_ERROR:
        return new LoadingMoreErrorViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.load_more_error, parent, false), uiEventsListener);
      case EARN_APPCOINS:
        return new EarnAppCoinsViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.bundle_earn_appcoins, parent, false), new DecimalFormat("0.00"),
            uiEventsListener);
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
      case APPCOINS_ADS:
        return EARN_APPCOINS;
      case EDITORS:
        return EDITORS;
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
      case WALLET_ADS_OFFER:
        return WALLET_ADS_OFFER;
      case TOP:
        return TOP;
      case LOAD_MORE_ERROR:
        return LOAD_MORE_ERROR;
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
    int initialPosition = this.bundles.size();
    this.bundles.addAll(bundles);
    notifyItemRangeInserted(initialPosition, bundles.size());
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

  public synchronized void updateEditorials() {
    for (int i = 0; i < bundles.size(); i++) {
      if (bundles.get(i) instanceof ActionBundle) {
        notifyItemChanged(i);
      }
    }
  }

  public void showLoadMoreError() {
    bundles.add(errorBundle);
    notifyItemInserted(bundles.indexOf(errorBundle));
  }

  public void removeLoadMoreError() {
    remove(bundles.size() - 1);
  }

  /**
   * @return true if the bundles are fully loaded (i.e. no skeleton layout placeholder)
   */
  public boolean isLoaded() {
    if (bundles == null || bundles.isEmpty()) return false;
    for (HomeBundle bundle : bundles) {
      if (bundle.getContent() == null) {
        return false;
      }
    }
    return true;
  }
}
