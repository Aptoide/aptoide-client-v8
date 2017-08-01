package cm.aptoide.pt.view.account;

import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.notification.view.InboxFragment;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.view.account.store.ManageStoreFragment;
import cm.aptoide.pt.view.account.user.ManageUserFragment;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import cm.aptoide.pt.view.store.StoreFragment;

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
    ManageStoreFragment.ViewModel viewModel = new ManageStoreFragment.ViewModel(store.getId(),
        StoreTheme.fromName(store.getAppearance()
            .getTheme()), store.getName(), store.getAppearance()
        .getDescription(), store.getAvatar());
    fragmentNavigator.navigateTo(ManageStoreFragment.newInstance(viewModel, false));
  }

  public void navigateToEditProfileView() {
    fragmentNavigator.navigateTo(ManageUserFragment.newInstanceToEdit());
  }

  public void navigateToUserView(String userId, String storeTheme) {
    fragmentNavigator.navigateTo(
        StoreFragment.newInstance(userId, storeTheme, StoreFragment.OpenType.GetHome));
  }

  public void navigateToStoreView(String storeName, String storeTheme) {
    fragmentNavigator.navigateTo(
        StoreFragment.newInstance(storeName, storeTheme, StoreFragment.OpenType.GetStore));
  }
}
