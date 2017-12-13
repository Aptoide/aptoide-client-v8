package cm.aptoide.pt.account.view;

import android.app.Activity;
import cm.aptoide.pt.account.view.store.ManageStoreFragment;
import cm.aptoide.pt.account.view.store.ManageStoreViewModel;
import cm.aptoide.pt.account.view.user.ManageUserFragment;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.notification.AptoideNotification;
import cm.aptoide.pt.notification.view.InboxFragment;
import cm.aptoide.pt.notification.view.NotificationNavigator;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.store.view.StoreFragment;
import rx.Observable;

public class MyAccountNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final AccountNavigator accountNavigator;
  private final NotificationNavigator notificationNavigator;

  public MyAccountNavigator(FragmentNavigator fragmentNavigator, AccountNavigator accountNavigator,
      NotificationNavigator notificationNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.accountNavigator = accountNavigator;
    this.notificationNavigator = notificationNavigator;
  }

  public void navigateToInboxView() {
    fragmentNavigator.navigateTo(new InboxFragment(), true);
  }

  public void navigateToEditStoreView(Store store, int requestCode) {
    ManageStoreViewModel viewModel =
        new ManageStoreViewModel(store.getId(), StoreTheme.fromName(store.getAppearance()
            .getTheme()), store.getName(), store.getAppearance()
            .getDescription(), store.getAvatar(), store.getSocialChannels());
    fragmentNavigator.navigateForResult(ManageStoreFragment.newInstance(viewModel, false),
        requestCode, true);
  }

  public Observable<Boolean> editStoreResult(int requestCode) {
    return fragmentNavigator.results(requestCode)
        .map(result -> result.getResultCode() == Activity.RESULT_OK);
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

  public void navigateToNotification(AptoideNotification notification) {
    notificationNavigator.navigateToNotification(notification);
  }

  public void navigateToHome() {
    accountNavigator.navigateToHomeView();
  }
}
