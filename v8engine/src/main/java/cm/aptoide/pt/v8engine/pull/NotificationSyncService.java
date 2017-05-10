package cm.aptoide.pt.v8engine.pull;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.NotificationAccessor;
import cm.aptoide.pt.database.realm.AptoideNotification;
import cm.aptoide.pt.v8engine.V8Engine;

/**
 * Created by trinkes on 09/05/2017.
 */

public class NotificationSyncService extends Service {
  public static final String PUSH_NOTIFICATIONS_ACTION = "PUSH_NOTIFICATIONS_ACTION";
  private NotificationHandler notificationHandler;
  private NotificationSync notificationSync;

  @Override public void onCreate() {
    super.onCreate();

    NotificationAccessor notificationAccessor =
        AccessorFactory.getAccessorFor(AptoideNotification.class);
    notificationHandler = ((V8Engine) getApplicationContext()).getNotificationHandler();
    notificationSync = new NotificationSync(notificationAccessor, notificationHandler);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    notificationSync.syncCampaigns()
        .subscribe(() -> stopSelf(startId), throwable -> stopSelf(startId));
    notificationSync.syncSocial()
        .subscribe(() -> stopSelf(startId), throwable -> stopSelf(startId));

    return Service.START_NOT_STICKY;
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }
}
