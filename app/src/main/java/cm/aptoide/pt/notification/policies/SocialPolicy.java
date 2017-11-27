package cm.aptoide.pt.notification.policies;

import android.support.annotation.NonNull;
import cm.aptoide.pt.notification.AptoideNotification;
import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.notification.Policy;
import java.util.concurrent.TimeUnit;
import rx.Single;

/**
 * Created by trinkes on 16/05/2017.
 */

public class SocialPolicy implements Policy {

  private final static long police1timeFrame = TimeUnit.HOURS.toMillis(1);
  private final static long police2timeFrame = TimeUnit.DAYS.toMillis(1);
  private final static int police1Occurrences = 1;
  private final static int police2Occurrences = 3;

  private NotificationProvider notificationProvider;
  @AptoideNotification.NotificationType private Integer[] notificationsTypes;

  public SocialPolicy(NotificationProvider notificationProvider,
      @AptoideNotification.NotificationType Integer[] notificationsTypes) {
    this.notificationProvider = notificationProvider;
    this.notificationsTypes = notificationsTypes;
  }

  @Override public Single<Boolean> shouldShow() {
    long now = System.currentTimeMillis();
    long police1startTime = now - police1timeFrame;
    long police2startTime = now - police2timeFrame;

    return Single.zip(createPolicy(notificationsTypes, now, police1startTime, police1Occurrences),
        createPolicy(notificationsTypes, now, police2startTime, police2Occurrences),
        (passRule1, passRule2) -> passRule1 && passRule2);
  }

  @NonNull private Single<Boolean> createPolicy(
      @AptoideNotification.NotificationType Integer[] notificationsTypes, long endTime,
      long startTime, int occurrences) {
    return notificationProvider.getDismissedNotifications(notificationsTypes, startTime, endTime)
        .map(aptoideNotifications -> aptoideNotifications.size() < occurrences);
  }
}
