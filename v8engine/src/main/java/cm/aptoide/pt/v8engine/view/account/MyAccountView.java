package cm.aptoide.pt.v8engine.view.account;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.v8engine.notification.AptoideNotification;
import cm.aptoide.pt.v8engine.presenter.View;
import java.util.List;
import rx.Observable;

public interface MyAccountView extends View {

  void showAccount(Account account);

  Observable<Void> signOutClick();

  Observable<Void> moreNotificationsClick();

  Observable<Void> storeClick();

  Observable<Void> userClick();

  Observable<AptoideNotification> notificationSelection();

  void showNotifications(List<AptoideNotification> notifications);

  Observable<Void> editStoreClick();

  Observable<GetStore> getStore();

  Observable<Void> editUserProfileClick();

  void navigateToHome();

  void showHeader();

  void hideHeader();

  void refreshUI(Store store);
}
