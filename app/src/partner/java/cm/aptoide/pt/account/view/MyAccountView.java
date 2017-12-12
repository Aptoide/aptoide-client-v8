package cm.aptoide.pt.account.view;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.notification.AptoideNotification;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

public interface MyAccountView extends View {

  void showAccount(Account account);

  Observable<Void> signOutClick();

  Observable<Void> moreNotificationsClick();

  Observable<AptoideNotification> notificationSelection();

  void showNotifications(List<AptoideNotification> notifications);

  Observable<GetStore> getStore();

  Observable<Void> editUserProfileClick();

  void showHeader();

  void hideHeader();
}
