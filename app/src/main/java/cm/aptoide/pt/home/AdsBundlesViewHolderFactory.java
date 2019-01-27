package cm.aptoide.pt.home;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import java.text.DecimalFormat;
import rx.subjects.PublishSubject;

public class AdsBundlesViewHolderFactory {

  private static final int ADS = R.layout.ads_bundle_item;
  private final PublishSubject<HomeEvent> uiEventsListener;
  private final PublishSubject<AdHomeEvent> adClickedEvents;
  private final DecimalFormat oneDecimalFormatter;
  private final String marketName;
  private final boolean showNatives;

  public AdsBundlesViewHolderFactory(PublishSubject<HomeEvent> uiEventsListener,
      PublishSubject<AdHomeEvent> adClickedEvents, DecimalFormat oneDecimalFormatter,
      String marketName, boolean showNatives) {
    this.uiEventsListener = uiEventsListener;
    this.adClickedEvents = adClickedEvents;
    this.oneDecimalFormatter = oneDecimalFormatter;
    this.marketName = marketName;
    this.showNatives = showNatives;
  }

  public AppBundleViewHolder createViewHolder(ViewGroup parent) {
    if (Build.VERSION.SDK_INT >= 21 && showNatives) {
      return new AdsWithMoPubBundleViewHolder(LayoutInflater.from(parent.getContext())
          .inflate(ADS, parent, false), uiEventsListener, oneDecimalFormatter, adClickedEvents,
          marketName);
    } else {
      return new AdsBundleViewHolder(LayoutInflater.from(parent.getContext())
          .inflate(ADS, parent, false), uiEventsListener, oneDecimalFormatter, adClickedEvents,
          marketName);
    }
  }
}
