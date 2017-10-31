/*
 * Copyright (c) 2016.
 * Modified on 12/05/2016.
 */

package cm.aptoide.pt.view.swipe;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.ReloadInterface;
import cm.aptoide.pt.view.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.navigator.ActivityNavigator;

/**
 * Handler for Swipe Loader Layouts. Needs five identified views in the corresponding layout:<br>
 * <br>&#9{@link R.id#progress_bar} <br>&#9{@link R.id#generic_error} <br>&#9{@link
 * R.id#no_network_connection} <br>&#9{@link R.id#retry} <br>&#9{@link R.id#swipe_container}
 */
public class SwipeLoaderLayoutHandler extends LoaderLayoutHandler {

  private SwipeRefreshLayout swipeContainer;

  public SwipeLoaderLayoutHandler(int baseViewId, ActivityNavigator activityNavigator,
      ReloadInterface reloadInterface) {
    super(reloadInterface, activityNavigator, baseViewId);
  }

  public SwipeLoaderLayoutHandler(int[] viewsToShowAfterLoadingId,
      ActivityNavigator activityNavigator, GridRecyclerSwipeFragment reloadInterface) {
    super(reloadInterface, activityNavigator, viewsToShowAfterLoadingId);
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
    swipeContainer.setColorSchemeResources(R.color.default_progress_bar_color,
        R.color.default_color, R.color.default_progress_bar_color, R.color.default_color);
    swipeContainer.setOnRefreshListener(() -> ((ReloadInterface) loadInterface).reload());
  }

  @Override public void onFinishLoading(Throwable throwable) {
    super.onFinishLoading(throwable);
    swipeContainer.setRefreshing(false);
    swipeContainer.setEnabled(false);
  }

  @Override public void restoreState() {
    super.restoreState();
    swipeContainer.setEnabled(true);
  }

  @Override protected void onFinishLoading() {
    super.onFinishLoading();
    swipeContainer.setRefreshing(false);
    swipeContainer.setEnabled(true);
  }

  @Override public void unbindViews() {
    swipeContainer.setOnRefreshListener(null);
    super.unbindViews();
  }
}
