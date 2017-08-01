/*
 * Copyright (c) 2016.
 * Modified on 22/08/2016.
 */

package cm.aptoide.pt.view.app.widget;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.app.displayable.AppViewScreenshotsDisplayable;
import cm.aptoide.pt.view.app.screenshots.ScreenshotsAdapter;
import cm.aptoide.pt.view.recycler.widget.Displayables;
import cm.aptoide.pt.view.recycler.widget.Widget;

/**
 * Created on 11/05/16.
 */
@Displayables({ AppViewScreenshotsDisplayable.class }) public class AppViewScreenshotsWidget
    extends Widget<AppViewScreenshotsDisplayable> {

  private RecyclerView mediaList;

  public AppViewScreenshotsWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    mediaList = (RecyclerView) itemView.findViewById(R.id.screenshots_list);
  }

  @Override public void bindView(AppViewScreenshotsDisplayable displayable) {
    final GetAppMeta.Media media = displayable.getPojo()
        .getMedia();
    //		mediaList.addItemDecoration(new DividerItemDecoration(AptoideUtils.ScreenU.getPixels(6), (DividerItemDecoration.RIGHT | DividerItemDecoration
    // .BOTTOM))
    //		);
    mediaList.setLayoutManager(
        new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
    mediaList.setNestedScrollingEnabled(false); // because otherwise the AppBar won't be collapsed
    mediaList.setAdapter(
        new ScreenshotsAdapter(media, getFragmentNavigator(), displayable.getAppViewAnalytics()));
  }
}
