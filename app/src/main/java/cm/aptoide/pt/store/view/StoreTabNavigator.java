/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.store.view;

import cm.aptoide.pt.comment.CommentsFragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.navigator.FragmentNavigator;

public class StoreTabNavigator {

  private final FragmentNavigator fragmentNavigator;

  public StoreTabNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToStoreTabGridRecyclerView(Event event, String title, String storeTheme,
      String tag, StoreContext storeContext) {
    fragmentNavigator.navigateTo(
        StoreTabGridRecyclerFragment.newInstance(event, title, storeTheme, tag, storeContext),
        true);
  }

  public void navigateToCommentsList() {
    fragmentNavigator.navigateTo(new CommentsFragment(), true);
  }
}
