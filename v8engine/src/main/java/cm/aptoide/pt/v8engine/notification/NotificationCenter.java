package cm.aptoide.pt.v8engine.notification;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import java.util.List;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

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
  private NotificationsCleaner notificationsCleaner;
  private CompositeSubscription subscriptions;
  private NotificationProvider notificationProvider;
  private AptoideAccountManager accountManager;

  public NotificationCenter(NotificationIdsMapper notificationIdsMapper,
      NotificationHandler notificationHandler, NotificationProvider notificationProvider,
      NotificationSyncScheduler notificationSyncScheduler,
      SystemNotificationShower notificationShower, CrashReport crashReport,
      NotificationPolicyFactory notificationPolicyFactory,
      NotificationsCleaner notificationsCleaner, AptoideAccountManager accountManager) {
    this.notificationIdsMapper = notificationIdsMapper;
    this.notificationHandler = notificationHandler;
    this.notificationSyncScheduler = notificationSyncScheduler;
    this.notificationShower = notificationShower;
    this.notificationProvider = notificationProvider;
    this.crashReport = crashReport;
    this.notificationPolicyFactory = notificationPolicyFactory;
    this.notificationsCleaner = notificationsCleaner;
    this.accountManager = accountManager;
    subscriptions = new CompositeSubscription();
  }

  public void start() {
    notificationSyncScheduler.schedule();
    subscriptions.add(getNewNotifications().flatMapCompletable(
        aptoideNotification -> notificationShower.showNotification(aptoideNotification,
            notificationIdsMapper.getNotificationId(aptoideNotification.getType())))
        .subscribe(aptoideNotification -> {
        }, throwable -> crashReport.log(throwable)));

    subscriptions.add(accountManager.accountStatus()
        .filter(account -> account.isLoggedIn())
        .flatMapCompletable(account -> notificationsCleaner.cleanNotifications(account.getId()))
        .subscribe(notificationsCleaned -> {
        }, throwable -> crashReport.log(throwable)));
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

  public void stop() {
    if (!subscriptions.isUnsubscribed()) {
      subscriptions.clear();
    }
    notificationSyncScheduler.stop();
  }

  public Observable<List<AptoideNotification>> getInboxNotifications(int entries) {
    return notificationProvider.getNotifications(entries);
  }

  public Observable<Boolean> haveNotifications() {
    return notificationProvider.getNotifications(1)
        .map(list -> !list.isEmpty());
  }
}
