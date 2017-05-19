package cm.aptoide.pt.v8engine.presenter;

import cm.aptoide.pt.v8engine.notification.AptoideNotification;
import java.util.List;
import rx.Observable;

public interface MyAccountView extends View {
  Observable<Void> signOutClick();

  Observable<Void> moreNotificationsClick();

  void showNotifications(List<AptoideNotification> notifications);

  Observable<Void> editStoreClick();

  void navigateToHome();
}
