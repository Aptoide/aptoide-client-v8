package cm.aptoide.pt.presenter;

import cm.aptoide.pt.notification.NotificationInfo;
import rx.Observable;

/**
 * Created by pedroribeiro on 20/11/17.
 */

public interface NotificationView extends View {

  Observable<NotificationInfo> getNotificationClick();

  Observable<NotificationInfo> getNotificationDismissed();

  Observable<NotificationInfo> getActionBootCompleted();
}
