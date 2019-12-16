package cm.aptoide.pt.app.view.similar.bundles;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ads.MoPubNativeAdsListener;
import cm.aptoide.pt.app.view.AppViewSimilarAppsAdapter;
import cm.aptoide.pt.app.view.similar.SimilarAppClickEvent;
import cm.aptoide.pt.app.view.similar.SimilarAppsBundle;
import cm.aptoide.pt.app.view.similar.SimilarBundleViewHolder;
import cm.aptoide.pt.home.SnapToStartHelper;
import cm.aptoide.pt.utils.AptoideUtils;
import com.mopub.nativeads.InMobiNativeAdRenderer;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;
import java.text.DecimalFormat;
import java.util.Collections;
import rx.subjects.PublishSubject;

public class SimilarAppsViewHolder extends SimilarBundleViewHolder {

  private final RecyclerView similarApps;

  private final DecimalFormat oneDecimalFormat;
  private final PublishSubject<SimilarAppClickEvent> similarAppClick;

  private MoPubRecyclerAdapter moPubSimilarAppsRecyclerAdapter;
  private AppViewSimilarAppsAdapter adapter;

  public SimilarAppsViewHolder(View view, DecimalFormat oneDecimalFormat,
      PublishSubject<SimilarAppClickEvent> similarAppClick) {
    super(view);
    this.oneDecimalFormat = oneDecimalFormat;
    this.similarAppClick = similarAppClick;

    similarApps = view.findViewById(R.id.similar_list);
    similarApps.setNestedScrollingEnabled(false);
    similarApps.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        int margin = AptoideUtils.ScreenU.getPixelsForDip(5, view.getResources());
        outRect.set(margin, margin, 0, margin);
      }
    });

    LinearLayoutManager similarLayout =
        new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false);

    similarApps.setLayoutManager(similarLayout);
    SnapHelper similarSnap = new SnapToStartHelper();
    similarSnap.attachToRecyclerView(similarApps);
  }

  private void setSimilarAdapter(boolean loadMoPubAdapter, boolean isFromMatureApp) {
    this.adapter =
        new AppViewSimilarAppsAdapter(Collections.emptyList(), oneDecimalFormat, similarAppClick,
            AppViewSimilarAppsAdapter.SimilarAppType.SIMILAR_APPS);
    if (loadMoPubAdapter) {
      moPubSimilarAppsRecyclerAdapter =
          new MoPubRecyclerAdapter((Activity) similarApps.getContext(), adapter);
      configureAdRenderers();
      moPubSimilarAppsRecyclerAdapter.setAdLoadedListener(new MoPubNativeAdsListener());

      if (Build.VERSION.SDK_INT >= 21) {
        similarApps.setAdapter(moPubSimilarAppsRecyclerAdapter);
        loadAds(isFromMatureApp);
      } else {
        similarApps.setAdapter(adapter);
      }
    } else {
      similarApps.setAdapter(adapter);
    }
  }

  private void loadAds(boolean isFromMatureApp) {
    if (isFromMatureApp) {
      moPubSimilarAppsRecyclerAdapter.loadAds(BuildConfig.MOPUB_NATIVE_EXCLUSIVE_PLACEMENT_ID);
    } else {
      moPubSimilarAppsRecyclerAdapter.loadAds(BuildConfig.MOPUB_NATIVE_APPVIEW_PLACEMENT_ID);
    }
  }

  @NonNull private ViewBinder getMoPubAdViewBinder() {
    return new ViewBinder.Builder(R.layout.displayable_grid_ad).titleId(R.id.name)
        .iconImageId(R.id.icon)
        .mainImageId(R.id.native_main_image)
        .addExtra("primary_ad_view_layout", R.id.primary_ad_view_layout)
        .build();
  }

  private void configureAdRenderers() {
    ViewBinder viewBinder = getMoPubAdViewBinder();
    moPubSimilarAppsRecyclerAdapter.registerAdRenderer(new MoPubStaticNativeAdRenderer(viewBinder));
    moPubSimilarAppsRecyclerAdapter.registerAdRenderer(new InMobiNativeAdRenderer(viewBinder));
  }

  @Override public void setBundle(SimilarAppsBundle bundle, int position) {
    if (adapter == null) {
      setSimilarAdapter(bundle.getContent()
          .shouldLoadNativeAds(), bundle.getContent()
          .isFromMatureApp());
    }
    adapter.update(mapToSimilar(bundle.getContent(), bundle.getContent()
        .hasAd()));
  }
}
