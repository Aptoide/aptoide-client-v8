package cm.aptoide.pt.notification;

import java.util.Collections;
import java.util.List;
import rx.Single;

public class NotificationService {

  public Single<List<AptoideNotification>> getPushNotifications() {
    return Single.just(Collections.emptyList());
  }
}
