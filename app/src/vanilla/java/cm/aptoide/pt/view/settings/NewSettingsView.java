package cm.aptoide.pt.view.settings;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.presenter.View;
import rx.Observable;

/**
 * Created by franciscocalado on 13/03/18.
 */

public interface NewSettingsView extends View {

  void showAccount(Account account);

  Observable<Void> loginClick();

  Observable<Void> signOutClick();

  Observable<Void> findFriendsClick();

  Observable<Void> storeClick();

  Observable<Void> userClick();

  Observable<Void> editStoreClick();

  Observable<Void> editUserProfileClick();

  Observable<Void> settingsClicked();

  Observable<Void> notificationsClicked();

  Observable<GetStore> getStore();

  void refreshUI(Store store);

  void showLoginAccountDisplayable();

  Observable<Void> createStoreClick();
}
