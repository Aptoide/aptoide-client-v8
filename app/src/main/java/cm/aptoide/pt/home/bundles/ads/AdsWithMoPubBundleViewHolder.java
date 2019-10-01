package cm.aptoide.pt.home.bundles.ads;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.aptoideviews.skeletonV2.Skeleton;
import cm.aptoide.aptoideviews.skeletonV2.SkeletonUtils;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ads.MoPubNativeAdsListener;
import cm.aptoide.pt.home.bundles.base.AppBundleViewHolder;
import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.Translator;
import com.mopub.nativeads.InMobiNativeAdRenderer;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import rx.subjects.PublishSubject;

public class AdsWithMoPubBundleViewHolder extends AppBundleViewHolder {

  private final TextView bundleTitle;
  private final Button moreButton;
  private final AdsInBundleAdapter appsInBundleAdapter;
  private final PublishSubject<HomeEvent> uiEventsListener;
  private final RecyclerView appsList;
  private final String marketName;
  private final MoPubRecyclerAdapter moPubRecyclerAdapter;
  private boolean hasAdLoaded;

  private final Skeleton skeleton;

  public AdsWithMoPubBundleViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener,
      DecimalFormat oneDecimalFormatter, PublishSubject<AdHomeEvent> adClickedEvents,
      String marketName) {
    super(view);
    this.uiEventsListener = uiEventsListener;
    this.marketName = marketName;

    bundleTitle = view.findViewById(R.id.bundle_title);
    moreButton = view.findViewById(R.id.bundle_more);
    appsList = view.findViewById(R.id.apps_list);
    appsInBundleAdapter =
        new AdsInBundleAdapter(new ArrayList<>(), oneDecimalFormatter, adClickedEvents);
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false);
    appsList.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        int margin = AptoideUtils.ScreenU.getPixelsForDip(5, view.getResources());
        outRect.set(margin, margin, 0, margin);
      }
    });
    appsList.setLayoutManager(layoutManager);
    appsList.setAdapter(appsInBundleAdapter);

    moPubRecyclerAdapter =
        new MoPubRecyclerAdapter((Activity) view.getContext(), appsInBundleAdapter);
    ViewBinder moPubViewBinder =
        new ViewBinder.Builder(R.layout.displayable_grid_ad).titleId(R.id.name)
            .iconImageId(R.id.icon)
            .mainImageId(R.id.native_main_image)
            .addExtra("primary_ad_view_layout", R.id.primary_ad_view_layout)
            .build();

    moPubRecyclerAdapter.registerAdRenderer(new MoPubStaticNativeAdRenderer(moPubViewBinder));
    InMobiNativeAdRenderer inMobiNativeAdRenderer = new InMobiNativeAdRenderer(moPubViewBinder);
    moPubRecyclerAdapter.registerAdRenderer(inMobiNativeAdRenderer);
    moPubRecyclerAdapter.setAdLoadedListener(new MoPubNativeAdsListener());
    appsList.setAdapter(moPubRecyclerAdapter);
    appsList.setNestedScrollingEnabled(false);

    skeleton = SkeletonUtils.applySkeleton(appsList, R.layout.app_home_item_skeleton, 9);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    if (!(homeBundle instanceof AdBundle)) {
      throw new IllegalStateException(this.getClass()
          .getName() + " is getting non AdBundle instance!");
    }
    bundleTitle.setText(
        Translator.translate(homeBundle.getTitle(), itemView.getContext(), marketName));

    if (homeBundle.getContent() == null) {
      skeleton.showSkeleton();
    } else {
      skeleton.showOriginal();
      appsInBundleAdapter.updateBundle(homeBundle, position);
      appsInBundleAdapter.update((List<AdClick>) homeBundle.getContent());

      appsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          if (dx > 0) {
            uiEventsListener.onNext(
                new HomeEvent(homeBundle, getAdapterPosition(), HomeEvent.Type.SCROLL_RIGHT));
          }
        }
      });

      moreButton.setOnClickListener(v -> uiEventsListener.onNext(
          new HomeEvent(homeBundle, getAdapterPosition(), HomeEvent.Type.MORE)));

      if (!hasAdLoaded) {
        hasAdLoaded = true;
        moPubRecyclerAdapter.loadAds(BuildConfig.MOPUB_NATIVE_HOME_PLACEMENT_ID);
      }
    }
  }
}
