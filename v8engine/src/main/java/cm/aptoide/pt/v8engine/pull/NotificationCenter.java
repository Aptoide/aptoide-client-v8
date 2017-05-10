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
  private NotificationStatusManager notificationStatusManager;
  private NotificationHandler notificationHandler;
  private NotificationProvider notificationProvider;
  private NotificationSyncScheduler notificationSyncScheduler;
  private SystemNotificationShower notificationShower;

  public NotificationCenter(NotificationHandler notificationHandler,
      NotificationProvider notificationProvider,
      NotificationSyncScheduler notificationSyncScheduler,
      SystemNotificationShower notificationShower, CrashReport crashReport,
      NotificationStatusManager notificationStatusManager) {
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
            getNotificationId(aptoideNotification))
            .andThen(updateNotification(aptoideNotification))
            .onErrorComplete(throwable -> {
              crashReport.log(throwable);
              return true;
            })).subscribe(aptoideNotification -> {
    }, throwable -> crashReport.log(throwable));
  }

  private Completable updateNotification(AptoideNotification aptoideNotification) {
    return Completable.fromAction(() -> {
      aptoideNotification.setShowed(true);
      notificationProvider.save(aptoideNotification);
    });
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
        .onErrorResumeNext(throwable -> Observable.empty());
  }

  private Single<Boolean> shouldShowNotification(AptoideNotification aptoideNotificationToShow) {
    switch (aptoideNotificationToShow.getType()) {
      case AptoideNotification.CAMPAIGN:
        return Single.just(true);
      case AptoideNotification.COMMENT:
      case AptoideNotification.LIKE:
        return shouldShowSocialNotification(getNotificationId(aptoideNotificationToShow),
            getNotificationType(aptoideNotificationToShow));
      case AptoideNotification.POPULAR:
        return shouldShowSocialNotification(getNotificationId(aptoideNotificationToShow),
            getNotificationType(aptoideNotificationToShow));
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
    return notificationStatusManager.isVisible(notificationsId);
  }

  private Single<Boolean> applySocialPolicies(Integer[] notificationsIds) {
    return notificationProvider.getNotifications(notificationsIds)
        .map(notifications -> !isShowedLimitReached(notifications, 1, TimeUnit.MINUTES.toMillis(1))
            && !isShowedLimitReached(notifications, 3, TimeUnit.MINUTES.toMillis(5)));
  }

  @NonNull private Boolean isShowedLimitReached(List<AptoideNotification> aptoideNotifications,
      int occurrencesLimit, long timeFrame) {
    int occurrences = 0;
    for (int i = 0; i < aptoideNotifications.size() && occurrences < occurrencesLimit; i++) {
      AptoideNotification aptoideNotification = aptoideNotifications.get(i);
      if (aptoideNotification.getTimeStamp() < System.currentTimeMillis() - timeFrame) {
        break;
      }
      if (aptoideNotification.isShowed()
          && aptoideNotification.getTimeStamp() > System.currentTimeMillis() - timeFrame) {
        occurrences++;
      }
    }
    return occurrences >= occurrencesLimit;
  }

  private int getNotificationId(AptoideNotification aptoideNotification) throws RuntimeException {
    switch (aptoideNotification.getType()) {
      case AptoideNotification.CAMPAIGN:
        return 0;
      case AptoideNotification.COMMENT:
      case AptoideNotification.LIKE:
        return 1;
      case AptoideNotification.POPULAR:
        return 2;
      default:
        throw new RuntimeException("unknown notification type ");
    }
  }

  private Integer[] getNotificationType(AptoideNotification aptoideNotificationToShow) {
    switch (aptoideNotificationToShow.getType()) {
      case AptoideNotification.CAMPAIGN:
        return new Integer[] {
            AptoideNotification.CAMPAIGN
        };
      case AptoideNotification.COMMENT:
      case AptoideNotification.LIKE:
        return new Integer[] {
            AptoideNotification.LIKE, AptoideNotification.COMMENT
        };
      case AptoideNotification.POPULAR:
        return new Integer[] {
            AptoideNotification.POPULAR,
        };
      default:
        throw new RuntimeException("unknown notification type ");
    }
  }
}
