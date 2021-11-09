package cm.aptoide.pt.app.view.similar.bundles;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
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
        new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false);

    similarApps.setLayoutManager(similarLayout);
    SnapHelper similarSnap = new SnapToStartHelper();
    similarSnap.attachToRecyclerView(similarApps);
  }

  private void setSimilarAdapter() {
    this.adapter =
        new AppViewSimilarAppsAdapter(Collections.emptyList(), oneDecimalFormat, similarAppClick,
            AppViewSimilarAppsAdapter.SimilarAppType.SIMILAR_APPS);
    similarApps.setAdapter(adapter);
  }

  @Override public void setBundle(SimilarAppsBundle bundle, int position) {
    if (adapter == null) {
      setSimilarAdapter();
    }
    adapter.update(mapToSimilar(bundle.getContent(), bundle.getContent()
        .hasAd()));
  }
}
