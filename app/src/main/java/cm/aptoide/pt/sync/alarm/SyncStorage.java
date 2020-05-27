package cm.aptoide.pt.sync.alarm;

import cm.aptoide.pt.notification.LocalNotificationSyncPersistence;
import cm.aptoide.pt.notification.sync.LocalNotificationSync;
import cm.aptoide.pt.sync.Sync;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import rx.Completable;
import rx.schedulers.Schedulers;

public class SyncStorage {

  private final Map<String, Sync> syncs;
  private final LocalNotificationSyncPersistence persistence;

  public SyncStorage(Map<String, Sync> syncs, LocalNotificationSyncPersistence persistence) {
    this.syncs = syncs;
    this.persistence = persistence;
  }

  public void save(Sync sync) {
    Completable.fromAction(() -> {
      if (sync instanceof LocalNotificationSync) persistence.save((LocalNotificationSync) sync);
      syncs.put(sync.getId(), sync);
    })
        .subscribeOn(Schedulers.io())
        .subscribe();
  }

  public Sync get(String syncId) {
    if (syncId.equals(LocalNotificationSync.APPC_CAMPAIGN_NOTIFICATION) || syncId.equals(
        LocalNotificationSync.NEW_FEATURE)) {
      return persistence.get(syncId)
          .toBlocking()
          .first();
    } else {
      return syncs.get(syncId);
    }
  }

  public List<Sync> getAll() {
    return new ArrayList<>(syncs.values());
  }

  public void remove(String syncId) {
    Completable.fromAction(() -> {
      if (syncId.equals(LocalNotificationSync.APPC_CAMPAIGN_NOTIFICATION)) {
        persistence.remove(syncId);
      }
      syncs.remove(syncId);
    })
        .subscribeOn(Schedulers.io())
        .subscribe();
  }
}
