/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 22/08/2016.
 */

package cm.aptoide.pt.v8engine.view.app.widget;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.app.screenshots.ScreenshotsAdapter;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewScreenshotsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 11/05/16.
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
    final GetAppMeta.Media media = displayable.getPojo().getMedia();
    //		mediaList.addItemDecoration(new DividerItemDecoration(AptoideUtils.ScreenU.getPixels(6), (DividerItemDecoration.RIGHT | DividerItemDecoration
    // .BOTTOM))
    //		);
    mediaList.setLayoutManager(
        new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
    mediaList.setNestedScrollingEnabled(false); // because otherwise the AppBar won't be collapsed
    mediaList.setAdapter(new ScreenshotsAdapter(media, getFragmentNavigator()));
  }
}
