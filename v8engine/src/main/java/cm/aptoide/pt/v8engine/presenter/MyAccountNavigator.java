package cm.aptoide.pt.v8engine.presenter;

import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.v8engine.notification.view.InboxFragment;
import cm.aptoide.pt.v8engine.view.account.store.ManageStoreFragment;
import cm.aptoide.pt.v8engine.view.account.user.ManageUserFragment;
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
    ManageStoreFragment.ViewModel viewModel = new ManageStoreFragment.ViewModel(store.getId(),
        store.getAppearance()
            .getTheme(), store.getName(), store.getAppearance()
        .getDescription(), store.getAvatar());
    fragmentNavigator.navigateTo(ManageStoreFragment.newInstance(viewModel, false));
  }

  public void navigateToEditProfileView() {
    fragmentNavigator.navigateTo(
        ManageUserFragment.newInstanceToEdit());
  }
}
