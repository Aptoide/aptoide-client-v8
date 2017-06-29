package cm.aptoide.pt.v8engine.sync.alarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.NotificationAccessor;
import cm.aptoide.pt.database.realm.Notification;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.notification.NotificationNetworkService;
import cm.aptoide.pt.v8engine.notification.NotificationProvider;
import rx.schedulers.Schedulers;

public class NotificationSyncService extends Service {
  public static final String NOTIFICATIONS_SOCIAL_ACTION = "PUSH_NOTIFICATIONS_SOCIAL_ACTION";
  public static final String NOTIFICATIONS_CAMPAIGN_ACTION = "PUSH_NOTIFICATIONS_CAMPAIGN_ACTION";
  private NotificationSync notificationSync;

  @Override public void onCreate() {
    super.onCreate();

    NotificationAccessor notificationAccessor = AccessorFactory.getAccessorFor(Notification.class);
    NotificationProvider notificationProvider =
        new NotificationProvider(notificationAccessor, Schedulers.io());
    final NotificationNetworkService notificationHandler =
        ((V8Engine) getApplicationContext()).getNotificationNetworkService();
    notificationSync = new NotificationSync(notificationProvider, notificationHandler);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {

    if (intent != null && intent.getAction() != null) {
      switch (intent.getAction()) {
        case NOTIFICATIONS_CAMPAIGN_ACTION:
          notificationSync.syncCampaigns()
              .doOnTerminate(() -> stopSelf(startId))
              .subscribe(() -> {
              }, throwable -> throwable.printStackTrace());
          break;
        case NOTIFICATIONS_SOCIAL_ACTION:
          notificationSync.syncSocial()
              .doOnTerminate(() -> stopSelf(startId))
              .subscribe(() -> {
              }, throwable -> throwable.printStackTrace());
      }
    }

    return Service.START_NOT_STICKY;
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }
}
