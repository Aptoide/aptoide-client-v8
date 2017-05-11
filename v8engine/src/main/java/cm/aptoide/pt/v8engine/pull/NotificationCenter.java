package cm.aptoide.pt.v8engine.pull;

import android.support.annotation.NonNull;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by trinkes on 09/05/2017.
 */

public class NotificationCenter {
  private final CrashReport crashReport;
  private final NotificationIdsMapper notificationIdsMapper;
  private NotificationStatusManager notificationStatusManager;
  private NotificationHandler notificationHandler;
  private NotificationProvider notificationProvider;
  private NotificationSyncScheduler notificationSyncScheduler;
  private SystemNotificationShower notificationShower;

  public NotificationCenter(NotificationIdsMapper notificationIdsMapper,
      NotificationHandler notificationHandler, NotificationProvider notificationProvider,
      NotificationSyncScheduler notificationSyncScheduler,
      SystemNotificationShower notificationShower, CrashReport crashReport,
      NotificationStatusManager notificationStatusManager) {
    this.notificationIdsMapper = notificationIdsMapper;
    this.notificationHandler = notificationHandler;
    this.notificationProvider = notificationProvider;
    this.notificationSyncScheduler = notificationSyncScheduler;
    this.notificationShower = notificationShower;
    this.crashReport = crashReport;
    this.notificationStatusManager = notificationStatusManager;
  }

  public void start() {
    notificationSyncScheduler.schedule();
    getNewNotifications().flatMapCompletable(
        aptoideNotification -> notificationShower.showNotification(aptoideNotification,
            notificationIdsMapper.getNotificationId(aptoideNotification.getType()))
            .andThen(updateNotification(aptoideNotification))
            .onErrorComplete(throwable -> {
              crashReport.log(throwable);
              return true;
            })).subscribe(aptoideNotification -> {
    }, throwable -> crashReport.log(throwable));
  }

  private Completable updateNotification(AptoideNotification aptoideNotification) {
    aptoideNotification.setShowed(true);
    notificationStatusManager.setVisible(
        notificationIdsMapper.getNotificationId(aptoideNotification.getType()), true);
    return notificationProvider.save(aptoideNotification);
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
        return shouldShowSocialNotification(
            notificationIdsMapper.getNotificationId(aptoideNotificationToShow.getType()),
            notificationIdsMapper.getNotificationType(aptoideNotificationToShow.getType()));
      case AptoideNotification.POPULAR:
        return shouldShowSocialNotification(
            notificationIdsMapper.getNotificationId(aptoideNotificationToShow.getType()),
            notificationIdsMapper.getNotificationType(aptoideNotificationToShow.getType()));
      default:
        return Single.just(false);
    }
  }

  private Single<Boolean> shouldShowSocialNotification(int notificationId,
      Integer[] notificationsIds) {
    return Single.zip(isNotificationVisible(notificationId), applySocialPolicies(notificationsIds),
        (isNotificationVisible, isPolicies) -> isNotificationVisible || isPolicies);
  }

  private Single<Boolean> isNotificationVisible(int notificationsId) {
    return Single.just(notificationStatusManager.isVisible(notificationsId));
  }

  private Single<Boolean> applySocialPolicies(Integer[] notificationsTypes) {
    return notificationProvider.getNotifications(notificationsTypes)
        .map(notifications -> !isShowedLimitReached(notifications, 1, TimeUnit.MINUTES.toMillis(2),
            System.currentTimeMillis()) && !isShowedLimitReached(notifications, 3,
            TimeUnit.MINUTES.toMillis(10), System.currentTimeMillis()));
  }

  @NonNull private Boolean isShowedLimitReached(List<AptoideNotification> aptoideNotifications,
      int occurrencesLimit, long timeFrame, long currentTime) {
    int occurrences = 0;
    for (int i = 0; i < aptoideNotifications.size() && occurrences < occurrencesLimit; i++) {
      AptoideNotification aptoideNotification = aptoideNotifications.get(i);
      if (aptoideNotification.getTimeStamp() < currentTime - timeFrame) {
        break;
      }
      if (aptoideNotification.isShowed()
          && aptoideNotification.getTimeStamp() > currentTime - timeFrame) {
        occurrences++;
      }
    }
    return occurrences >= occurrencesLimit;
  }
}
