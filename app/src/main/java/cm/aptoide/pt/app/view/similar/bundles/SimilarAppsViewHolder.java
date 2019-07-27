package cm.aptoide.pt.app.view.similar.bundles;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.view.AppViewSimilarAppsAdapter;
import cm.aptoide.pt.app.view.similar.SimilarAppClickEvent;
import cm.aptoide.pt.app.view.similar.SimilarAppsBundle;
import cm.aptoide.pt.app.view.similar.SimilarBundleViewHolder;
import cm.aptoide.pt.home.SnapToStartHelper;
import cm.aptoide.pt.utils.AptoideUtils;
import java.text.DecimalFormat;
import java.util.Collections;
import rx.subjects.PublishSubject;

public class SimilarAppsViewHolder extends SimilarBundleViewHolder {

  private final RecyclerView similarApps;

  private final DecimalFormat oneDecimalFormat;
  private final PublishSubject<SimilarAppClickEvent> similarAppClick;

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
    } else {
      similarApps.setAdapter(adapter);
    }
  }


  private void configureAdRenderers() {
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
