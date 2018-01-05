/*
 * Copyright (c) 2016.
 * Modified on 22/08/2016.
 */

package cm.aptoide.pt.app.view.widget;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.view.AppViewNavigator;
import cm.aptoide.pt.app.view.displayable.AppViewScreenshotsDisplayable;
import cm.aptoide.pt.app.view.screenshots.ScreenshotsAdapter;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.view.BaseActivity;
import cm.aptoide.pt.view.recycler.widget.Widget;
import javax.inject.Inject;

/**
 * Created on 11/05/16.
 */
public class AppViewScreenshotsWidget extends Widget<AppViewScreenshotsDisplayable> {

  @Inject FragmentNavigator fragmentNavigator;
  private RecyclerView mediaList;
  private boolean isMultiStoreSearch;
  private String defaultStoreName;

  public AppViewScreenshotsWidget(View itemView) {
    super(itemView);
    ((BaseActivity) getContext()).getActivityComponent()
        .inject(this);
  }

  @Override protected void assignViews(View itemView) {
    mediaList = (RecyclerView) itemView.findViewById(R.id.screenshots_list);
  }

  @Override public void unbindView() {
    if (mediaList != null) {
      mediaList.setAdapter(null);
    }
    super.unbindView();
  }

  @Override public void bindView(AppViewScreenshotsDisplayable displayable) {
    final GetAppMeta.Media media = displayable.getPojo()
        .getMedia();
    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    isMultiStoreSearch = application.hasMultiStoreSearch();
    defaultStoreName = application.getDefaultStoreName();
    mediaList.setLayoutManager(
        new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
    mediaList.setNestedScrollingEnabled(false); // because otherwise the AppBar won't be collapsed

    final AppViewAnalytics appViewAnalytics = displayable.getAppViewAnalytics();
    final AppViewNavigator appViewNavigator = getAppViewNavigator();
    final CrashReport crashReport = CrashReport.getInstance();
    final ScreenshotsAdapter screenshotsAdapter =
        new ScreenshotsAdapter(media.getVideos(), media.getScreenshots());

    compositeSubscription.add(screenshotsAdapter.getScreenShotClick()
        .filter(event -> event.isVideo())
        .subscribe(videoClick -> {
          appViewAnalytics.sendOpenVideoEvent();
          appViewNavigator.navigateToUri(videoClick.getUri());
        }, err -> crashReport.log(err)));

    compositeSubscription.add(screenshotsAdapter.getScreenShotClick()
        .filter(event -> !event.isVideo())
        .subscribe(imageClick -> {
          appViewAnalytics.sendOpenScreenshotEvent();
          appViewNavigator.navigateToScreenshots(imageClick.getImagesUris(),
              imageClick.getImagesIndex());
        }, err -> crashReport.log(err)));

    mediaList.setAdapter(screenshotsAdapter);
  }

  private AppViewNavigator getAppViewNavigator() {
    return new AppViewNavigator(fragmentNavigator, getActivityNavigator(), isMultiStoreSearch,
        defaultStoreName);
  }
}
