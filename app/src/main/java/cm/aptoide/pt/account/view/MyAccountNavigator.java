package cm.aptoide.pt.account.view;

import cm.aptoide.pt.account.view.store.ManageStoreFragment;
import cm.aptoide.pt.account.view.user.ManageUserFragment;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.notification.view.InboxFragment;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.store.view.StoreFragment;

/**
 * Created by pedroribeiro on 16/05/17.
 */

public class MyAccountNavigator {

  private final FragmentNavigator fragmentNavigator;

  public MyAccountNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToInboxView() {
    fragmentNavigator.navigateTo(new InboxFragment(), true);
  }

  public void navigateToEditStoreView(Store store) {
    ManageStoreFragment.ViewModel viewModel = new ManageStoreFragment.ViewModel(store.getId(),
        StoreTheme.fromName(store.getAppearance()
            .getTheme()), store.getName(), store.getAppearance()
        .getDescription(), store.getAvatar());
    fragmentNavigator.navigateTo(ManageStoreFragment.newInstance(viewModel, false), true);
  }

  public void navigateToEditProfileView() {
    fragmentNavigator.navigateTo(ManageUserFragment.newInstanceToEdit(), true);
  }

  public void navigateToUserView(String userId, String storeTheme) {
    fragmentNavigator.navigateTo(
        StoreFragment.newInstance(userId, storeTheme, StoreFragment.OpenType.GetHome), true);
  }

  public void navigateToStoreView(String storeName, String storeTheme) {
    fragmentNavigator.navigateTo(
        StoreFragment.newInstance(storeName, storeTheme, StoreFragment.OpenType.GetStore), true);
  }
}
