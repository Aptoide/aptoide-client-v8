package cm.aptoide.pt.notification;

import cm.aptoide.pt.notification.AptoideNotification;
import java.util.Collections;
import java.util.List;
import rx.Single;

public class NotificationService {

  public Single<List<AptoideNotification>> getSocialNotifications() {
    return Single.just(Collections.emptyList());
  }
}
