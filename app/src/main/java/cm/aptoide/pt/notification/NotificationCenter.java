package cm.aptoide.pt.notification;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReport;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by trinkes on 09/05/2017.
 */

public class NotificationCenter {

  public static final int MAX_NUMBER_NOTIFICATIONS_SAVED = 50;
  private final CrashReport crashReport;
  private NotificationSyncScheduler notificationSyncScheduler;
  private SystemNotificationShower notificationShower;
  private NotificationPolicyFactory notificationPolicyFactory;
  private NotificationsCleaner notificationsCleaner;
  private CompositeSubscription subscriptions;
  private NotificationProvider notificationProvider;
  private AptoideAccountManager accountManager;

  public NotificationCenter(NotificationProvider notificationProvider,
      NotificationSyncScheduler notificationSyncScheduler,
      SystemNotificationShower notificationShower, CrashReport crashReport,
      NotificationPolicyFactory notificationPolicyFactory,
      NotificationsCleaner notificationsCleaner, AptoideAccountManager accountManager) {
    this.notificationSyncScheduler = notificationSyncScheduler;
    this.notificationShower = notificationShower;
    this.notificationProvider = notificationProvider;
    this.crashReport = crashReport;
    this.notificationPolicyFactory = notificationPolicyFactory;
    this.notificationsCleaner = notificationsCleaner;
    this.accountManager = accountManager;
    subscriptions = new CompositeSubscription();
  }

  public void setup() {
    notificationSyncScheduler.schedule();
    subscriptions.add(getNewNotifications().flatMapCompletable(
        aptoideNotification -> notificationShower.showNotification(aptoideNotification))
        .subscribe(aptoideNotification -> {
        }, throwable -> crashReport.log(throwable)));

    subscriptions.add(accountManager.accountStatus()
        .filter(account -> account.isLoggedIn())
        .flatMapCompletable(
            account -> notificationsCleaner.cleanOtherUsersNotifications(account.getId()))
        .subscribe(notificationsCleaned -> {
        }, throwable -> crashReport.log(throwable)));

    subscriptions.add(notificationProvider.getNotifications(1)
        .flatMapCompletable(
            aptoideNotifications -> notificationsCleaner.cleanLimitExceededNotifications(
                MAX_NUMBER_NOTIFICATIONS_SAVED))
        .subscribe(aptoideNotifications -> {
        }, throwable -> crashReport.log(throwable)));
  }

  private Observable<AptoideNotification> getNewNotifications() {
    return notificationProvider.getAptoideNotifications()
        .flatMapIterable(notifications -> notifications)
        .filter(notification -> !notification.isProcessed())
        .flatMapSingle(notification -> {
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

  public Observable<List<AptoideNotification>> getUnreadNotifications() {
    return notificationProvider.getUnreadNotifications();
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
