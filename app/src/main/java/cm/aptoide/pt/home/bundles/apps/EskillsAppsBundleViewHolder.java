package cm.aptoide.pt.home.bundles.apps;

import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.analytics.EventLogger;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.aptoideviews.skeleton.Skeleton;
import cm.aptoide.aptoideviews.skeleton.SkeletonUtils;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.Type;
import cm.aptoide.pt.home.bundles.base.AppBundle;
import cm.aptoide.pt.home.bundles.base.AppBundleViewHolder;
import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.home.more.eskills.EskillsAnalytics;
import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.app.Application;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import okhttp3.OkHttpClient;
import rx.subjects.PublishSubject;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class EskillsAppsBundleViewHolder extends AppBundleViewHolder {
  private final Button moreButton;
  private final AppsInBundleAdapter appsInBundleAdapter;
  private final PublishSubject<HomeEvent> uiEventsListener;
  private final RecyclerView appsList;

  private final Skeleton skeleton;

  @Inject IdsRepository idsRepository;
  @Inject @Named("default") OkHttpClient defaultClient;
  @Inject @Named("indicativeEventLogger") EventLogger eventLogger;
  @Inject NavigationTracker navigationTracker;
  @Inject EskillsAnalytics eskillsAnalytics;


  public EskillsAppsBundleViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener,
      DecimalFormat oneDecimalFormatter) {
    super(view);

    this.uiEventsListener = uiEventsListener;
    moreButton = view.findViewById(R.id.bundle_more);
    appsList = view.findViewById(R.id.apps_list);
    appsInBundleAdapter =
        new AppsInBundleAdapter(new ArrayList<>(), oneDecimalFormatter, uiEventsListener, null);
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


    Resources resources = view.getContext()
        .getResources();
    WindowManager windowManager = (WindowManager) view.getContext()
        .getSystemService(WINDOW_SERVICE);
    skeleton = SkeletonUtils.applySkeleton(appsList, R.layout.app_home_item_skeleton,
        Type.APPS_GROUP.getPerLineCount(resources, windowManager) * 3);

  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    if (!(homeBundle instanceof AppBundle)) {
      throw new IllegalStateException(this.getClass()
          .getName() + " is getting non AppBundle instance!");
    }

    if (homeBundle.getContent() == null) {
      skeleton.showSkeleton();
    } else {
      appsInBundleAdapter.updateBundle(homeBundle, position);
      appsInBundleAdapter.update((List<Application>) homeBundle.getContent());
      skeleton.showOriginal();
      appsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          if (dx > 0) {
            uiEventsListener.onNext(
                new HomeEvent(homeBundle, getAdapterPosition(), HomeEvent.Type.SCROLL_RIGHT));
          }
        }
      });

      itemView.setOnClickListener(v -> {
          uiEventsListener.onNext(
                new HomeEvent(homeBundle, getAdapterPosition(), HomeEvent.Type.ESKILLS_KNOW_MORE));
        eskillsAnalytics.sendHomeBundleHeaderClickEvent();
      });
      moreButton.setOnClickListener(v -> {
        uiEventsListener.onNext(
                new HomeEvent(homeBundle, getAdapterPosition(), HomeEvent.Type.ESKILLS_KNOW_MORE));
        eskillsAnalytics.sendHomeBundleMoreClickEvent();
      });

    }
  }
}
