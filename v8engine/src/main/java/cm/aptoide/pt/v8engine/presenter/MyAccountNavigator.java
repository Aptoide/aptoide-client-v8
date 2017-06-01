package cm.aptoide.pt.v8engine.presenter;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.notification.view.InboxFragment;
import cm.aptoide.pt.v8engine.view.account.store.ManageStoreFragment;
import cm.aptoide.pt.v8engine.view.account.store.ManageStoreViewModel;
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
    ManageStoreViewModel viewModel = new ManageStoreViewModel(store.getId(), store.getAvatar(),
        store.getAppearance()
            .getTheme(), store.getName(), store.getAppearance()
        .getDescription());
    fragmentNavigator.navigateTo(ManageStoreFragment.newInstance(viewModel, false));
  }

  public void navigateToEditProfileView(Account account) {
    fragmentNavigator.navigateTo(
        ManageUserFragment.newInstanceToEdit(account.getAvatar(), account.getNickname()));
  }
}
