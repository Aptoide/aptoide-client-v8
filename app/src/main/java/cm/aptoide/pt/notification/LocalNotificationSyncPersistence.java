package cm.aptoide.pt.notification;

import cm.aptoide.pt.notification.sync.LocalNotificationSync;
import cm.aptoide.pt.sync.Sync;
import rx.Observable;

public interface LocalNotificationSyncPersistence {

  void save(LocalNotificationSync notification);

  Observable<Sync> get();

  void remove(String id);
}
