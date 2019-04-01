package cm.aptoide.pt.app.view.similar.bundles;

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
import cm.aptoide.pt.view.custom.HorizontalHeaderItemDecoration;
import java.text.DecimalFormat;
import java.util.Collections;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.app.view.AppViewSimilarAppsAdapter.SimilarAppType.APPC_SIMILAR_APPS;

public class SimilarAppcAppsViewHolder extends SimilarBundleViewHolder {
  private final RecyclerView similarAppcApps;

  private final DecimalFormat oneDecimalFormat;
  private final PublishSubject<SimilarAppClickEvent> similarAppClick;

  private AppViewSimilarAppsAdapter adapter;

  public SimilarAppcAppsViewHolder(View view, DecimalFormat oneDecimalFormat,
      PublishSubject<SimilarAppClickEvent> similarAppClick) {
    super(view);
    this.oneDecimalFormat = oneDecimalFormat;
    this.similarAppClick = similarAppClick;

    similarAppcApps = view.findViewById(R.id.similar_appc_list);
    similarAppcApps.setNestedScrollingEnabled(false);
    HorizontalHeaderItemDecoration similarAppcHeaderItemDecoration =
        new HorizontalHeaderItemDecoration(view.getContext(), similarAppcApps,
            R.layout.appview_appc_similar_header,
            AptoideUtils.ScreenU.getPixelsForDip(112, view.getResources()),
            AptoideUtils.ScreenU.getPixelsForDip(5, view.getResources()));
    similarAppcApps.addItemDecoration(similarAppcHeaderItemDecoration);

    LinearLayoutManager similarAppcLayout =
        new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);

    similarAppcApps.setLayoutManager(similarAppcLayout);
    SnapHelper similarSnap = new SnapToStartHelper();
    similarSnap.attachToRecyclerView(similarAppcApps);

    similarAppcApps.setAdapter(getSimilarAdapter());
  }

  private RecyclerView.Adapter getSimilarAdapter() {
    this.adapter =
        new AppViewSimilarAppsAdapter(Collections.emptyList(), oneDecimalFormat, similarAppClick,
            APPC_SIMILAR_APPS);
    return adapter;
  }

  @Override public void setBundle(SimilarAppsBundle bundle, int position) {
    adapter.update(mapToSimilar(bundle.getContent(), false));
  }
}
