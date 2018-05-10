package cm.aptoide.pt.view.settings;

import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.account.view.MyAccountNavigator;
import cm.aptoide.pt.account.view.store.ManageStoreFragment;
import cm.aptoide.pt.account.view.store.ManageStoreViewModel;
import cm.aptoide.pt.addressbook.view.AddressBookFragment;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.notification.view.InboxFragment;
import rx.Observable;

/**
 * Created by franciscocalado on 13/03/18.
 */

public class NewAccountNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final MyAccountNavigator accountNavigator;
  private final AccountNavigator navigator;

  private final String UPLOADER_UNAME = "aptoide-uploader";
  private final String BACKUP_APPS_UNAME = "aptoide-backup-apps";

  public NewAccountNavigator(FragmentNavigator fragmentNavigator,
      MyAccountNavigator accountNavigator, AccountNavigator navigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.accountNavigator = accountNavigator;
    this.navigator = navigator;
  }

  public void navigateToAppView(String uname) {
    fragmentNavigator.navigateTo(AppViewFragment.newInstanceUname(uname), true);
  }

  public void navigateToUploader() {
    fragmentNavigator.navigateTo(AppViewFragment.newInstanceUname(UPLOADER_UNAME), true);
  }

  public void navigateToBackupApps() {
    fragmentNavigator.navigateTo(AppViewFragment.newInstanceUname(BACKUP_APPS_UNAME), true);
  }

  public void navigateToEditStoreView(Store store, int requestCode) {
    accountNavigator.navigateToEditStoreView(store, requestCode);
  }

  public Observable<Boolean> editStoreResult(int requestCode) {
    return accountNavigator.editStoreResult(requestCode);
  }

  public void navigateToEditProfileView() {
    accountNavigator.navigateToEditProfileView();
  }

  public void navigateToUserView(String userId, String theme) {
    accountNavigator.navigateToUserView(userId, theme);
  }

  public void navigateToStoreView(String storeName, String theme) {
    accountNavigator.navigateToStoreView(storeName, theme);
  }

  public void navigateToLoginView(AccountAnalytics.AccountOrigins accountOrigins) {
    navigator.navigateToAccountView(accountOrigins);
  }

  public void navigateToCreateStore() {
    fragmentNavigator.navigateTo(ManageStoreFragment.newInstance(new ManageStoreViewModel(), false),
        true);
  }

  public void navigateToFindFriends() {
    fragmentNavigator.navigateTo(AddressBookFragment.newInstance(), true);
  }

  public void navigateToSettings() {
    fragmentNavigator.navigateTo(SettingsFragment.newInstance(), true);
  }

  public void navigateToNotificationHistory() {
    fragmentNavigator.navigateTo(new InboxFragment(), true);
  }
}
