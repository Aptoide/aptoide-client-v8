package cm.aptoide.pt.home.bundles.editorschoice;

import android.graphics.Rect;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.aptoideviews.skeletonV2.Skeleton;
import cm.aptoide.aptoideviews.skeletonV2.SkeletonUtils;
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

public class EditorsBundleViewHolder extends AppBundleViewHolder {
  private final TextView bundleTitle;
  private final Button moreButton;
  private final EditorsAppsAdapter graphicAppsAdapter;
  private final PublishSubject<HomeEvent> uiEventsListener;
  private final RecyclerView graphicsList;
  private final String marketName;

  private final Skeleton skeleton;

  public EditorsBundleViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener,
      DecimalFormat oneDecimalFormatter, String marketName) {
    super(view);
    this.marketName = marketName;
    this.uiEventsListener = uiEventsListener;
    bundleTitle = (TextView) view.findViewById(R.id.bundle_title);
    moreButton = (Button) view.findViewById(R.id.bundle_more);
    graphicsList = (RecyclerView) view.findViewById(R.id.featured_graphic_list);
    graphicAppsAdapter =
        new EditorsAppsAdapter(new ArrayList<>(), oneDecimalFormatter, uiEventsListener);
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false);
    graphicsList.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        int margin = AptoideUtils.ScreenU.getPixelsForDip(2, view.getResources());
        outRect.set(margin, margin, 0, margin);
      }
    });
    graphicsList.setLayoutManager(layoutManager);
    graphicsList.setAdapter(graphicAppsAdapter);
    graphicsList.setNestedScrollingEnabled(false);

    skeleton =
        SkeletonUtils.applySkeleton(graphicsList, R.layout.feature_graphic_home_item_skeleton, 9);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    if (!(homeBundle instanceof AppBundle)) {
      throw new IllegalStateException(this.getClass()
          .getName() + " is getting non AppBundle instance!");
    }
    bundleTitle.setText(
        Translator.translate(homeBundle.getTitle(), itemView.getContext(), marketName));
    if (homeBundle.getContent() == null) {
      skeleton.showSkeleton();
    } else {
      skeleton.showOriginal();
      graphicAppsAdapter.updateBundle(homeBundle, position);
      graphicAppsAdapter.update((List<Application>) homeBundle.getContent());
      graphicsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
}
