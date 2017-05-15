package cm.aptoide.pt.v8engine.notification;

import android.support.annotation.NonNull;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Single;

/**
 * Created by trinkes on 09/05/2017.
 */

public class NotificationCenter {
  private final CrashReport crashReport;
  private final NotificationIdsMapper notificationIdsMapper;
  private NotificationHandler notificationHandler;
  private NotificationProvider notificationProvider;
  private NotificationSyncScheduler notificationSyncScheduler;
  private SystemNotificationShower notificationShower;

  public NotificationCenter(NotificationIdsMapper notificationIdsMapper,
      NotificationHandler notificationHandler, NotificationProvider notificationProvider,
      NotificationSyncScheduler notificationSyncScheduler,
      SystemNotificationShower notificationShower, CrashReport crashReport) {
    this.notificationIdsMapper = notificationIdsMapper;
    this.notificationHandler = notificationHandler;
    this.notificationProvider = notificationProvider;
    this.notificationSyncScheduler = notificationSyncScheduler;
    this.notificationShower = notificationShower;
    this.crashReport = crashReport;
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
        .flatMap(aptideNotification -> shouldShowNotification(aptideNotification).flatMapObservable(
            shouldShow -> {
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

  private Single<Boolean> shouldShowNotification(AptoideNotification aptoideNotificationToShow) {
    switch (aptoideNotificationToShow.getType()) {
      case AptoideNotification.CAMPAIGN:
        return Single.just(true);
      case AptoideNotification.COMMENT:
      case AptoideNotification.LIKE:
        return shouldShowByRules(
            new Integer[] { AptoideNotification.COMMENT, AptoideNotification.LIKE });
      case AptoideNotification.POPULAR:
        return shouldShowByRules(new Integer[] { AptoideNotification.POPULAR });
      default:
        return Single.just(false);
    }
  }

  private Single<Boolean> shouldShowByRules(Integer[] notificationsTypes) {
    long now = System.currentTimeMillis();

    long police1timeFrame = TimeUnit.MINUTES.toMillis(2);
    long police2timeFrame = TimeUnit.MINUTES.toMillis(10);
    long police1startTime = now - police1timeFrame;
    long police2startTime = now - police2timeFrame;
    int police1Occurrences = 1;
    int police2Occurrences = 3;
    return Single.zip(createPolicy(notificationsTypes, now, police1startTime, police1Occurrences),
        createPolicy(notificationsTypes, now, police2startTime, police2Occurrences),
        (passRule1, passRule2) -> passRule1 && passRule2);
  }

  @NonNull
  private Single<Boolean> createPolicy(Integer[] notificationsTypes, long endTime, long startTime,
      int occurrences) {
    return notificationProvider.getDismissedNotifications(notificationsTypes, startTime, endTime)
        .map(aptoideNotifications -> aptoideNotifications.size() < occurrences);
  }
}
