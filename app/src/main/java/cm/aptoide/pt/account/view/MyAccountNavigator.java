package cm.aptoide.pt.account.view;

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
  private final int editStoreRequestCode;

  public MyAccountNavigator(FragmentNavigator fragmentNavigator, AccountNavigator accountNavigator,
      NotificationNavigator notificationNavigator, int editStoreRequestCode) {
    this.fragmentNavigator = fragmentNavigator;
    this.accountNavigator = accountNavigator;
    this.notificationNavigator = notificationNavigator;
    this.editStoreRequestCode = editStoreRequestCode;
  }

  public void navigateToInboxView() {
    fragmentNavigator.navigateTo(new InboxFragment(), true);
  }

  public void navigateToEditStoreView(Store store) {
    ManageStoreViewModel viewModel = new ManageStoreViewModel(store.getId(), StoreTheme.fromName(
        store.getAppearance()
            .getTheme()), store.getName(), store.getAppearance()
        .getDescription(), store.getAvatar(), store.getSocialChannels());
    fragmentNavigator.navigateForResult(ManageStoreFragment.newInstance(viewModel, false),
        editStoreRequestCode, true);
  }

  public Observable<Void> editStoreResult() {
    return fragmentNavigator.results(editStoreRequestCode)
        .map(result -> null);
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
