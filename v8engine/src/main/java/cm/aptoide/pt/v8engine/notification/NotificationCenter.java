package cm.aptoide.pt.v8engine.notification;

import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import rx.Observable;

/**
 * Created by trinkes on 09/05/2017.
 */

public class NotificationCenter {
  private final CrashReport crashReport;
  private final NotificationIdsMapper notificationIdsMapper;
  private NotificationHandler notificationHandler;
  private NotificationSyncScheduler notificationSyncScheduler;
  private SystemNotificationShower notificationShower;
  private NotificationPolicyFactory notificationPolicyFactory;

  public NotificationCenter(NotificationIdsMapper notificationIdsMapper,
      NotificationHandler notificationHandler, NotificationSyncScheduler notificationSyncScheduler,
      SystemNotificationShower notificationShower, CrashReport crashReport,
      NotificationPolicyFactory notificationPolicyFactory) {
    this.notificationIdsMapper = notificationIdsMapper;
    this.notificationHandler = notificationHandler;
    this.notificationSyncScheduler = notificationSyncScheduler;
    this.notificationShower = notificationShower;
    this.crashReport = crashReport;
    this.notificationPolicyFactory = notificationPolicyFactory;
  }

  public void start() {
    notificationSyncScheduler.schedule();
    getNewNotifications().flatMapCompletable(
        aptoideNotification -> notificationShower.showNotification(aptoideNotification,
            notificationIdsMapper.getNotificationId(aptoideNotification.getType())))
        .subscribe(aptoideNotification -> {
        }, throwable -> crashReport.log(throwable));
  }

  private Observable<AptoideNotification> getNewNotifications() {
    return notificationHandler.getHandlerNotifications()
        .flatMap(aptideNotification -> notificationPolicyFactory.getPolicy(aptideNotification)
            .shouldShow()
            .flatMapObservable(shouldShow -> {
              if (shouldShow) {
                return Observable.just(aptideNotification);
              } else {
                return Observable.empty();
              }
            }))
        .onErrorResumeNext(throwable -> {
          throwable.printStackTrace();
          return Observable.empty();
        });
  }
}
