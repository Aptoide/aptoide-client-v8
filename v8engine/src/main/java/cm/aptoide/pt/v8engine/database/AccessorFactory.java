package cm.aptoide.pt.v8engine.database;

import android.support.annotation.NonNull;
import cm.aptoide.pt.database.accessors.Accessor;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.accessors.NotificationAccessor;
import cm.aptoide.pt.database.accessors.PaymentAuthorizationAccessor;
import cm.aptoide.pt.database.accessors.RollbackAccessor;
import cm.aptoide.pt.database.accessors.ScheduledAccessor;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.accessors.StoredMinimalAdAccessor;
import cm.aptoide.pt.database.accessors.UpdateAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Notification;
import cm.aptoide.pt.database.realm.PaymentAuthorization;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.database.realm.Update;
import io.realm.RealmObject;

/**
 * Instead of getting an accessor from here, use a Repository
 */
@Deprecated public final class AccessorFactory {

  @NonNull
  public static <T extends RealmObject, A extends Accessor> A getAccessorFor(Database database,
      Class<T> clazz) {

    if (clazz.equals(Scheduled.class)) {
      return (A) new ScheduledAccessor(database);
    } else if (clazz.equals(Installed.class)) {
      return (A) new InstalledAccessor(database);
    } else if (clazz.equals(Download.class)) {
      return (A) new DownloadAccessor(database);
    } else if (clazz.equals(Update.class)) {
      return (A) new UpdateAccessor(database);
    } else if (clazz.equals(Rollback.class)) {
      return (A) new RollbackAccessor(database);
    } else if (clazz.equals(Store.class)) {
      return (A) new StoreAccessor(database);
    } else if (clazz.equals(StoredMinimalAd.class)) {
      return (A) new StoredMinimalAdAccessor(database);
    } else if (clazz.equals(PaymentAuthorization.class)) {
      return (A) new PaymentAuthorizationAccessor(database);
    } else if (clazz.equals(Notification.class)) {
      return (A) new NotificationAccessor(database);
    }

    throw new RuntimeException("Create accessor for class " + clazz.getName());
  }
}
