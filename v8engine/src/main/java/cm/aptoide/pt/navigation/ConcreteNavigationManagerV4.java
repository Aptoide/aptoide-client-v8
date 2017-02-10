package cm.aptoide.pt.navigation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.util.FragmentUtils;

class ConcreteNavigationManagerV4 implements NavigationManagerV4 {

  private final FragmentActivity fragmentActivityV4;

  ConcreteNavigationManagerV4(FragmentActivity fragmentActivityV4) {
    this.fragmentActivityV4 = fragmentActivityV4;
  }

  @Override public void navigateUsing(Event event, String storeTheme, String title, String tag,
      StoreContext storeContext) {
    Fragment fragment;

    // TODO: 22/12/2016 sithengineer refactor this using the rules present in "StoreTabGridRecyclerFragment.java"
    if (event.getName() == Event.Name.listComments) {
      String action = event.getAction();
      String url = action != null ? action.replace(V7.BASE_HOST, "") : null;

      fragment =
          V8Engine.getFragmentProvider().newCommentGridRecyclerFragmentUrl(CommentType.STORE, url);
    } else {
      fragment = V8Engine.getFragmentProvider()
          .newStoreTabGridRecyclerFragment(event, title, storeTheme, tag, storeContext);
    }

    navigateTo(fragment);
  }

  @Override public void navigateTo(Fragment fragment) {
    FragmentUtils.replaceFragmentV4(fragmentActivityV4, fragment);
  }
}
