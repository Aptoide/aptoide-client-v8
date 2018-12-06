package cm.aptoide.pt.home;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ads.data.ApplicationAd;
import cm.aptoide.pt.ads.data.AppodealNativeAd;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.Translator;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeCallbacks;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 13/03/2018.
 */

class AdsBundleViewHolder extends AppBundleViewHolder {
  private final TextView bundleTitle;
  private final Button moreButton;
  private final AdsInBundleAdapter appsInBundleAdapter;
  private final PublishSubject<HomeEvent> uiEventsListener;
  private final RecyclerView appsList;
  //private final MoPubRecyclerAdapter moPubRecyclerAdapter;
  private final HomeAnalytics homeAnalytics;

  private boolean hasAdLoaded;

  public AdsBundleViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener,
      DecimalFormat oneDecimalFormatter, PublishSubject<AdHomeEvent> adClickedEvents,
      HomeAnalytics homeAnalytics) {
    super(view);
    this.homeAnalytics = homeAnalytics;
    this.uiEventsListener = uiEventsListener;
    this.hasAdLoaded = false;
    bundleTitle = (TextView) view.findViewById(R.id.bundle_title);
    moreButton = (Button) view.findViewById(R.id.bundle_more);
    appsList = (RecyclerView) view.findViewById(R.id.apps_list);
    appsInBundleAdapter =
        new AdsInBundleAdapter(new ArrayList<>(), oneDecimalFormatter, adClickedEvents);
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
    appsList.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        int margin = AptoideUtils.ScreenU.getPixelsForDip(5, view.getResources());
        outRect.set(margin, margin, 0, margin);
      }
    });
    appsList.setLayoutManager(layoutManager);
    appsList.setAdapter(appsInBundleAdapter);

    Appodeal.setNativeCallbacks(new NativeCallbacks() {
      @Override public void onNativeLoaded() {
        if (appsInBundleAdapter.getAppodealCount() < 3) {
          List<NativeAd> nativeAds = Appodeal.getNativeAds(3);
          for (NativeAd nativeAd : nativeAds) {
            if (appsInBundleAdapter.getAppodealCount() < 3) {
              appsInBundleAdapter.add(
                  new AdClick(new AppodealNativeAd(nativeAd), "ads-highlighted"));
            }
          }
        }
      }

      @Override public void onNativeFailedToLoad() {

      }

      @Override public void onNativeShown(NativeAd nativeAd) {
        homeAnalytics.sendAdImpressionEvent(0, "Unknown", 2, "ads-highlighted", HomeEvent.Type.AD,
            ApplicationAd.Network.APPODEAL);
      }

      @Override public void onNativeClicked(NativeAd nativeAd) {
        homeAnalytics.sendAdClickEvent(0, "Unknown", 2, "ads-highlighted", HomeEvent.Type.AD,
            ApplicationAd.Network.APPODEAL);
      }

      @Override public void onNativeExpired() {

      }
    });
    //moPubRecyclerAdapter =
    //    new MoPubRecyclerAdapter((Activity) view.getContext(), appsInBundleAdapter);
    //ViewBinder moPubViewBinder =
    //    new ViewBinder.Builder(R.layout.displayable_grid_ad).titleId(R.id.name)
    //        .iconImageId(R.id.icon)
    //        .build();
    //MoPubStaticNativeAdRenderer moPubRenderer = new MoPubStaticNativeAdRenderer(moPubViewBinder);
    //moPubRecyclerAdapter.registerAdRenderer(moPubRenderer);
    //moPubRecyclerAdapter.setAdLoadedListener(new MoPubNativeAdLoadedListener() {
    //  @Override public void onAdLoaded(int position) {
    //    homeAnalytics.sendAdImpressionEvent(0, "Ad", position, "ads-highlighted", HomeEvent.Type.AD,
    //        ApplicationAd.Network.MOPUB);
    //  }
    //
    //  @Override public void onAdRemoved(int position) {
    //
    //  }
    //});
    //appsList.setAdapter(moPubRecyclerAdapter);
  }


  @Override public void setBundle(HomeBundle homeBundle, int position) {
    if (!(homeBundle instanceof AdBundle)) {
      throw new IllegalStateException(this.getClass()
          .getName() + " is getting non AdBundle instance!");
    }
    bundleTitle.setText(Translator.translate(homeBundle.getTitle(), itemView.getContext(),
        ((AptoideApplication) itemView.getContext()
            .getApplicationContext()).getMarketName()));

    appsInBundleAdapter.updateBundle(homeBundle, position);
    appsInBundleAdapter.update((List<AdClick>) homeBundle.getContent());

    appsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dx > 0) {
          uiEventsListener.onNext(new HomeEvent(homeBundle, position, HomeEvent.Type.SCROLL_RIGHT));
        }
      }
    });

    moreButton.setOnClickListener(
        v -> uiEventsListener.onNext(new HomeEvent(homeBundle, position, HomeEvent.Type.MORE)));

    List<NativeAd> nativeAds = Appodeal.getNativeAds(3);
    if (nativeAds != null && appsInBundleAdapter.getAppodealCount() < 3) {
      for (NativeAd nativeAd : nativeAds) {
        appsInBundleAdapter.add(new AdClick(new AppodealNativeAd(nativeAd), "ads-highlighted"));
      }
    }

      //moPubRecyclerAdapter.loadAds(BuildConfig.MOPUB_HIGHLIGHTED_PLACEMENT_ID);
  }
}
