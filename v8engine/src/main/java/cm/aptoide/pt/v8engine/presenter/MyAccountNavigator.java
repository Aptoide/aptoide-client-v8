package cm.aptoide.pt.v8engine.presenter;

import cm.aptoide.pt.model.v7.store.Store;
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

  public void navigateToInboxView() {
    fragmentNavigator.navigateTo(new InboxFragment());
  }

  public void navigateToEditStoreView(Store store) {
    fragmentNavigator.navigateTo(CreateStoreFragment.newInstance(store.getId(),
        store.getAppearance()
            .getTheme(), store.getAppearance()
            .getDescription(), store.getAvatar(), CreateStoreFragment.STORE_FROM_DEFAULT_VALUE));
  }
}
