package cm.aptoide.pt.notification.view;

import cm.aptoide.pt.notification.AptoideNotification;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

public interface InboxView extends View {

  void showNotifications(List<AptoideNotification> notifications);

  Observable<AptoideNotification> notificationSelection();

  void showEmptyState();
}
