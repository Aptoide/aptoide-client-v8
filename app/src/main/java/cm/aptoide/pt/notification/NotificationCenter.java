package cm.aptoide.pt.notification;

import java.util.List;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 09/05/2017.
 */

public class NotificationCenter {

  private NotificationSyncScheduler notificationSyncScheduler;
  private NotificationPolicyFactory notificationPolicyFactory;
  private NotificationProvider notificationProvider;
  private NotificationAnalytics notificationAnalytics;

  public NotificationCenter(NotificationProvider notificationProvider,
      NotificationSyncScheduler notificationSyncScheduler,
      NotificationPolicyFactory notificationPolicyFactory,
      NotificationAnalytics notificationAnalytics) {
    this.notificationSyncScheduler = notificationSyncScheduler;
    this.notificationProvider = notificationProvider;
    this.notificationPolicyFactory = notificationPolicyFactory;
    this.notificationAnalytics = notificationAnalytics;
  }

  public void setup() {
    notificationSyncScheduler.schedule();
  }

  public Observable<AptoideNotification> getNewNotifications() {
    return notificationProvider.getAptoideNotifications()
        .flatMapIterable(notifications -> notifications)
        .filter(notification -> !notification.isProcessed())
        .flatMapSingle(notification -> {
          notificationAnalytics.sendPushNotificationReceivedEvent(notification.getType(),
              notification.getAbTestingGroup(), notification.getCampaignId(),
              notification.getUrl());
          notification.setProcessed(true);
          return notificationProvider.save(notification)
              .toSingleDefault(notification);
        })
        .flatMap(aptoideNotification -> notificationPolicyFactory.getPolicy(aptoideNotification)
            .shouldShow()
            .flatMapObservable(shouldShow -> {
              if (shouldShow) {
                return Observable.just(aptoideNotification);
              } else {
                return Observable.empty();
              }
            }))
        .onErrorResumeNext(throwable -> {
          throwable.printStackTrace();
          return Observable.empty();
        });
  }

  public Observable<List<AptoideNotification>> getInboxNotifications(int entries) {
    return notificationProvider.getNotifications(entries);
  }

  public Observable<Boolean> haveNotifications() {
    return notificationProvider.getNotifications(1)
        .map(list -> !list.isEmpty());
  }

  public Completable notificationDismissed(
      @AptoideNotification.NotificationType Integer[] notificationType) {
    return notificationProvider.getLastShowed(notificationType)
        .flatMapCompletable(notification -> {
          notification.setDismissed(System.currentTimeMillis());
          return notificationProvider.save(notification);
        });
  }

  public Completable setAllNotificationsRead() {
    return notificationProvider.getNotifications()
        .first()
        .flatMapIterable(notifications -> notifications)
        .flatMapCompletable(notification -> {
          if (notification.getDismissed() == AptoideNotification.NOT_DISMISSED) {
            notification.setDismissed(System.currentTimeMillis());
            return notificationProvider.save(notification);
          }
          return Completable.complete();
        })
        .toCompletable();
  }
}
