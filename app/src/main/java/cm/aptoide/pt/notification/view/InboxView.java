package cm.aptoide.pt.notification.view;

import cm.aptoide.pt.notification.AptoideNotification;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

/**
 * Created by pedroribeiro on 16/05/17.
 */

public interface InboxView extends View {

  void showNotifications(List<AptoideNotification> notifications);

  Observable<AptoideNotification> notificationSelection();

  void goHome();
}
