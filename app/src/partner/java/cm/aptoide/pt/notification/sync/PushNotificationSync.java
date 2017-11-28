package cm.aptoide.pt.notification.sync;

import android.content.SharedPreferences;
import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.notification.NotificationService;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.sync.Sync;
import rx.Completable;

public class PushNotificationSync extends Sync {

  private final NotificationService networkService;
  private final NotificationProvider provider;
  private final SharedPreferences sharedPreferences;

  public PushNotificationSync(String id, NotificationService networkService,
      NotificationProvider provider, boolean periodic, boolean exact, long interval, long trigger,
      SharedPreferences sharedPreferences) {
    super(id, periodic, exact, trigger, interval);
    this.networkService = networkService;
    this.provider = provider;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public Completable execute() {
    return networkService.getPushNotifications(
        ManagerPreferences.getLastPushNotificationId(sharedPreferences))
        .flatMapCompletable(notifications -> provider.save(notifications));
  }
}
