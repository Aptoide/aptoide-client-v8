package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.v8engine.notification.view.InboxFragment;
import cm.aptoide.pt.v8engine.view.account.store.CreateStoreFragment;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;

/**
 * Created by pedroribeiro on 16/05/17.
 */

public class MyAccountNavigator {

  private final FragmentNavigator fragmentNavigator;

  public MyAccountNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToInboxView(Bundle bundle) {
    Fragment inboxFragment = new InboxFragment();
    inboxFragment.setArguments(bundle);
    fragmentNavigator.navigateTo(inboxFragment);
  }

  public void navigateToEditStoreView() {
    fragmentNavigator.navigateTo(CreateStoreFragment.newInstance());
  }
}
