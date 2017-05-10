package cm.aptoide.pt.v8engine.pull;

import android.support.annotation.NonNull;
import cm.aptoide.pt.database.accessors.NotificationAccessor;
import cm.aptoide.pt.database.realm.AptoideNotification;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import io.realm.Sort;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 09/05/2017.
 */

public class NotificationCenter {
  private final Integer[] commentLikeNotificationIds;
  private final Integer[] popularNotificationIds;
  private final CrashReport crashReport;
  private NotificationHandler notificationHandler;
  private NotificationAccessor notificationAccessor;
  private NotificationSyncScheduler notificationSyncScheduler;
  private SystemNotificationShower notificationShower;

  public NotificationCenter(NotificationHandler notificationHandler,
      NotificationAccessor notificationAccessor,
      NotificationSyncScheduler notificationSyncScheduler,
      SystemNotificationShower notificationShower, CrashReport crashReport) {
    this.notificationHandler = notificationHandler;
    this.notificationAccessor = notificationAccessor;
    this.notificationSyncScheduler = notificationSyncScheduler;
    this.notificationShower = notificationShower;
    this.crashReport = crashReport;
    commentLikeNotificationIds =
        new Integer[] { AptoideNotification.COMMENT, AptoideNotification.LIKE };
    popularNotificationIds = new Integer[] { AptoideNotification.POPULAR };
  }

  public void start() {
    notificationSyncScheduler.schedule();
    getNewNotifications().flatMapCompletable(
        aptoideNotification -> notificationShower.showNotification(aptoideNotification,
            getNotificationId(aptoideNotification))
            .andThen(updateNotification(aptoideNotification))).subscribe(aptoideNotification -> {
    }, throwable -> crashReport.log(throwable));
  }

  private Completable updateNotification(AptoideNotification aptoideNotification) {
    return Completable.fromAction(() -> {
      aptoideNotification.setShowed(true);
      notificationAccessor.insert(aptoideNotification);
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
            }));
  }

  private Single<Boolean> shouldShowNotification(AptoideNotification aptoideNotificationToShow) {
    switch (aptoideNotificationToShow.getType()) {
      case AptoideNotification.CAMPAIGN:
        return Single.just(true);
      case AptoideNotification.COMMENT:
      case AptoideNotification.LIKE:
        return shouldShowSocialNotification(commentLikeNotificationIds);
      case AptoideNotification.POPULAR:
        return shouldShowSocialNotification(popularNotificationIds);
      default:
        return Single.just(false);
    }
  }

  private Single<Boolean> shouldShowSocialNotification(Integer[] notificationsIds) {
    // TODO: 10/05/2017 trinkes consider visible notifications
    return notificationAccessor.getAllSorted(Sort.DESCENDING, notificationsIds)
        .first()
        .observeOn(Schedulers.computation())
        .map(notifications -> applySocialPolicies(notifications))
        .toSingle();
  }

  private boolean applySocialPolicies(List<AptoideNotification> aptoideNotifications) {
    return !isShowedLimitReached(aptoideNotifications, 1, TimeUnit.HOURS.toMillis(1))
        && !isShowedLimitReached(aptoideNotifications, 3, TimeUnit.DAYS.toMillis(1));
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

  private int getNotificationId(AptoideNotification aptoideNotification) {
    switch (aptoideNotification.getType()) {
      case AptoideNotification.CAMPAIGN:
        return AptoideNotification.CAMPAIGN;
      case AptoideNotification.COMMENT:
      case AptoideNotification.LIKE:
        return aptoideNotification.getType();
      case AptoideNotification.POPULAR:
        return AptoideNotification.POPULAR;
      default:
        return aptoideNotification.getType();
    }
  }
}
