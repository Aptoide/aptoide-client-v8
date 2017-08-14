/*
 * Copyright (c) 2016.
 * Modified on 22/08/2016.
 */

package cm.aptoide.pt.v8engine.view.app.widget;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.app.AppViewAnalytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.app.AppViewNavigator;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewScreenshotsDisplayable;
import cm.aptoide.pt.v8engine.view.app.screenshots.ScreenshotsAdapter;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created on 11/05/16.
 */
public class AppViewScreenshotsWidget extends Widget<AppViewScreenshotsDisplayable> {

  private RecyclerView mediaList;

  public AppViewScreenshotsWidget(View itemView) {
    super(itemView);
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
    return new AppViewNavigator(getFragmentNavigator(), getActivityNavigator());
  }
}
