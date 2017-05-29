package cm.aptoide.pt.v8engine.notification;

import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import java.util.List;
import rx.Observable;
import rx.Subscription;

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
  private Subscription notificationProviderSubscription;
  private NotificationProvider notificationProvider;

  public NotificationCenter(NotificationIdsMapper notificationIdsMapper,
      NotificationHandler notificationHandler, NotificationProvider notificationProvider,
      NotificationSyncScheduler notificationSyncScheduler,
      SystemNotificationShower notificationShower, CrashReport crashReport,
      NotificationPolicyFactory notificationPolicyFactory) {
    this.notificationIdsMapper = notificationIdsMapper;
    this.notificationHandler = notificationHandler;
    this.notificationSyncScheduler = notificationSyncScheduler;
    this.notificationShower = notificationShower;
    this.notificationProvider = notificationProvider;
    this.crashReport = crashReport;
    this.notificationPolicyFactory = notificationPolicyFactory;
  }

  public void start() {
    notificationSyncScheduler.schedule();
    notificationProviderSubscription = getNewNotifications().flatMapCompletable(
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

  public void stop() {
    if (!notificationProviderSubscription.isUnsubscribed()) {
      notificationProviderSubscription.unsubscribe();
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
