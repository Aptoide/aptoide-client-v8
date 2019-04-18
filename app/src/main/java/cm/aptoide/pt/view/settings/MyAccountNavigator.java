package cm.aptoide.pt.view.settings;

import android.app.Activity;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.account.view.store.ManageStoreFragment;
import cm.aptoide.pt.account.view.store.ManageStoreViewModel;
import cm.aptoide.pt.account.view.user.ManageUserFragment;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.notification.view.InboxFragment;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.store.view.StoreFragment;
import rx.Observable;

/**
 * Created by franciscocalado on 13/03/18.
 */

public class MyAccountNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final AccountNavigator navigator;
  private final AppNavigator appNavigator;

  private final String UPLOADER_UNAME = "aptoide-uploader";
  private final String BACKUP_APPS_UNAME = "aptoide-backup-apps";

  public MyAccountNavigator(FragmentNavigator fragmentNavigator, AccountNavigator navigator,
      AppNavigator appNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.navigator = navigator;
    this.appNavigator = appNavigator;
  }

  public void navigateToAppView(String uname) {
    appNavigator.navigateWithUname(uname);
  }

  public void navigateToUploader() {
    appNavigator.navigateWithUname(UPLOADER_UNAME);
  }

  public void navigateToBackupApps() {
    appNavigator.navigateWithUname(BACKUP_APPS_UNAME);
  }

  public void navigateToEditStoreView(Store store, int requestCode) {
    ManageStoreViewModel viewModel = new ManageStoreViewModel(store.getId(), StoreTheme.fromName(
        store.getAppearance()
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

  public void navigateToLoginView(AccountAnalytics.AccountOrigins accountOrigins) {
    navigator.navigateToAccountView(accountOrigins);
  }

  public void navigateToCreateStore() {
    fragmentNavigator.navigateTo(ManageStoreFragment.newInstance(new ManageStoreViewModel(), false),
        true);
  }

  public void navigateToSettings() {
    fragmentNavigator.navigateTo(SettingsFragment.newInstance(), true);
  }

  public void navigateToNotificationHistory() {
    fragmentNavigator.navigateTo(new InboxFragment(), true);
  }
}
