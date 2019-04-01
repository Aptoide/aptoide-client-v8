package cm.aptoide.pt.app.view.similar.bundles;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ads.MoPubNativeAdsListener;
import cm.aptoide.pt.app.view.AppViewSimilarAppsAdapter;
import cm.aptoide.pt.app.view.similar.SimilarAppClickEvent;
import cm.aptoide.pt.app.view.similar.SimilarAppsBundle;
import cm.aptoide.pt.app.view.similar.SimilarBundleViewHolder;
import cm.aptoide.pt.home.SnapToStartHelper;
import cm.aptoide.pt.utils.AptoideUtils;
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
        new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);

    similarApps.setLayoutManager(similarLayout);
    SnapHelper similarSnap = new SnapToStartHelper();
    similarSnap.attachToRecyclerView(similarApps);
  }

  private void setSimilarAdapter(boolean mopubAdapter) {
    this.adapter =
        new AppViewSimilarAppsAdapter(Collections.emptyList(), oneDecimalFormat, similarAppClick,
            AppViewSimilarAppsAdapter.SimilarAppType.SIMILAR_APPS);
    if (mopubAdapter) {
      moPubSimilarAppsRecyclerAdapter =
          new MoPubRecyclerAdapter((Activity) similarApps.getContext(), adapter);
      moPubSimilarAppsRecyclerAdapter.registerAdRenderer(
          new MoPubStaticNativeAdRenderer(getMoPubAdViewBinder()));
      moPubSimilarAppsRecyclerAdapter.setAdLoadedListener(new MoPubNativeAdsListener());

      if (Build.VERSION.SDK_INT >= 21) {
        similarApps.setAdapter(moPubSimilarAppsRecyclerAdapter);
        moPubSimilarAppsRecyclerAdapter.loadAds(BuildConfig.MOPUB_NATIVE_APPVIEW_PLACEMENT_ID);
      } else {
        similarApps.setAdapter(adapter);
      }
    } else {
      similarApps.setAdapter(adapter);
    }
  }

  @NonNull private ViewBinder getMoPubAdViewBinder() {
    return new ViewBinder.Builder(R.layout.displayable_grid_ad).titleId(R.id.name)
        .iconImageId(R.id.icon)
        .build();
  }

  @Override public void setBundle(SimilarAppsBundle bundle, int position) {
    if (adapter == null) {
      setSimilarAdapter(bundle.getContent()
          .shouldLoadNativeAds());
    }
    adapter.update(mapToSimilar(bundle.getContent(), bundle.getContent()
        .hasAd()));
  }
}
