package cm.aptoide.pt.home.bundles.apps;

import android.graphics.Rect;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.home.bundles.base.AppBundle;
import cm.aptoide.pt.home.bundles.base.AppBundleViewHolder;
import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.Translator;
import cm.aptoide.pt.view.app.Application;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class AppsBundleViewHolder extends AppBundleViewHolder {
  private final TextView bundleTitle;
  private final Button moreButton;
  private final AppsInBundleAdapter appsInBundleAdapter;
  private final PublishSubject<HomeEvent> uiEventsListener;
  private final RecyclerView appsList;
  private final String marketName;

  public AppsBundleViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener,
      DecimalFormat oneDecimalFormatter, String marketName) {
    super(view);
    this.marketName = marketName;
    this.uiEventsListener = uiEventsListener;
    bundleTitle = (TextView) view.findViewById(R.id.bundle_title);
    moreButton = (Button) view.findViewById(R.id.bundle_more);
    appsList = (RecyclerView) view.findViewById(R.id.apps_list);
    appsInBundleAdapter =
        new AppsInBundleAdapter(new ArrayList<>(), oneDecimalFormatter, uiEventsListener);
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
    appsList.setNestedScrollingEnabled(false);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    if (!(homeBundle instanceof AppBundle)) {
      throw new IllegalStateException(this.getClass()
          .getName() + " is getting non AppBundle instance!");
    }
    bundleTitle.setText(
        Translator.translate(homeBundle.getTitle(), itemView.getContext(), marketName));
    appsInBundleAdapter.updateBundle(homeBundle, position);
    appsInBundleAdapter.update((List<Application>) homeBundle.getContent());
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
  }
}
