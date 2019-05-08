package cm.aptoide.pt.notification;

import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.realm.RealmLocalNotificationSync;
import cm.aptoide.pt.notification.sync.LocalNotificationSync;
import cm.aptoide.pt.sync.Sync;
import rx.Observable;

public class RealmLocalNotificationSyncPersistence implements LocalNotificationSyncPersistence {

  private Database database;
  private RealmLocalNotificationSyncMapper mapper;
  private NotificationProvider provider;

  public RealmLocalNotificationSyncPersistence(Database database,
      RealmLocalNotificationSyncMapper mapper, NotificationProvider provider) {
    this.database = database;
    this.mapper = mapper;
    this.provider = provider;
  }

  @Override public void save(LocalNotificationSync notification) {
    database.insert(mapper.map(notification));
  }

  @Override public Observable<Sync> get() {
    return database.get(RealmLocalNotificationSync.class,
        RealmLocalNotificationSync.PRIMARY_KEY_NAME,
        LocalNotificationSync.APPC_CAMPAIGN_NOTIFICATION)
        .map(sync -> {
          if (sync != null) {
            return mapper.map(sync, provider);
          } else {
            return null;
          }
        });
  }

  @Override public void remove(String id) {
    database.delete(RealmLocalNotificationSync.class, RealmLocalNotificationSync.PRIMARY_KEY_NAME,
        LocalNotificationSync.APPC_CAMPAIGN_NOTIFICATION);
  }
}
