package cm.aptoide.pt.v8engine.notification;

import android.support.annotation.NonNull;
import cm.aptoide.pt.database.accessors.NotificationAccessor;
import android.content.SharedPreferences;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import io.realm.Sort;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscription;

/**
 * Created by trinkes on 09/05/2017.
 */

public class NotificationCenter {

  public static final String NOTIFICATION_CENTER_ENABLE = "notification_campaign_and_social";
  private final CrashReport crashReport;
  private final NotificationIdsMapper notificationIdsMapper;
  private final NotificationAccessor notificationAccessor;
  private NotificationHandler notificationHandler;
  private NotificationSyncScheduler notificationSyncScheduler;
  private SystemNotificationShower notificationShower;
  private NotificationPolicyFactory notificationPolicyFactory;
  private SharedPreferences sharedPreferences;
  private Subscription notificationProviderSubscription;

  public NotificationCenter(NotificationIdsMapper notificationIdsMapper,
      NotificationHandler notificationHandler, NotificationProvider notificationProvider, NotificationSyncScheduler notificationSyncScheduler,
      SystemNotificationShower notificationShower, CrashReport crashReport,
      NotificationPolicyFactory notificationPolicyFactory,
      NotificationAccessor notificationAccessor, SharedPreferences sharedPreferences) {
    this.notificationIdsMapper = notificationIdsMapper;
    this.notificationHandler = notificationHandler;
    this.notificationSyncScheduler = notificationSyncScheduler;
    this.notificationShower = notificationShower;
    this.crashReport = crashReport;
    this.notificationPolicyFactory = notificationPolicyFactory;
    this.sharedPreferences = sharedPreferences;
    this.notificationAccessor = notificationAccessor;
  }

  public void enable() {
    sharedPreferences.edit()
        .putBoolean(NOTIFICATION_CENTER_ENABLE, true)
        .apply();
  }

  public void disable() {
    sharedPreferences.edit()
        .putBoolean(NOTIFICATION_CENTER_ENABLE, false)
        .apply();
  }

  public void startIfEnabled() {
    if (isEnable()) {
      start();
    }
  }

  public void start() {
      notificationSyncScheduler.schedule();
      notificationProviderSubscription = getNewNotifications().flatMapCompletable(
          aptoideNotification -> notificationShower.showNotification(aptoideNotification,
              notificationIdsMapper.getNotificationId(aptoideNotification.getType())))
          .subscribe(aptoideNotification -> {
          }, throwable -> crashReport.log(throwable));
  }

  public void forceSync() {
    notificationSyncScheduler.forceSync();
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

  public boolean isEnable() {
    return sharedPreferences.getBoolean(NOTIFICATION_CENTER_ENABLE, true);
  }

  public Observable<List<AptoideNotification>> getInboxNotifications(int entries) {
    return notificationAccessor.getAllSorted(Sort.DESCENDING)
        .flatMap(notifications -> Observable.from(notifications)
            .map(notification -> notificationProvider.convertToAptoideNotification(notification))
            .take(entries)
            .toList());
  }
}
