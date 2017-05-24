package cm.aptoide.pt.database.accessors;

import android.support.annotation.NonNull;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Notification;
import cm.aptoide.pt.database.realm.PaymentAuthorization;
import cm.aptoide.pt.database.realm.PaymentConfirmation;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.database.realm.Update;
import io.realm.RealmObject;

// TODO: 13/1/2017 this should not be public

/**
 * Instead of getting an accessor from here, use a Repository
 */
public final class AccessorFactory {

  @NonNull
  public static <T extends RealmObject, A extends Accessor> A getAccessorFor(Class<T> clazz) {
    if (clazz.equals(Scheduled.class)) {
      return (A) new ScheduledAccessor(new Database());
    } else if (clazz.equals(PaymentConfirmation.class)) {
      return (A) new PaymentConfirmationAccessor(new Database());
    } else if (clazz.equals(Installed.class)) {
      return (A) new InstalledAccessor(new Database());
    } else if (clazz.equals(Download.class)) {
      return (A) new DownloadAccessor(new Database());
    } else if (clazz.equals(Update.class)) {
      return (A) new UpdateAccessor(new Database());
    } else if (clazz.equals(Rollback.class)) {
      return (A) new RollbackAccessor(new Database());
    } else if (clazz.equals(Store.class)) {
      return (A) new StoreAccessor(new Database());
    } else if (clazz.equals(StoredMinimalAd.class)) {
      return (A) new StoredMinimalAdAccessor(new Database());
    } else if (clazz.equals(PaymentAuthorization.class)) {
      return (A) new PaymentAuthorizationAccessor(new Database());
    } else if (clazz.equals(Notification.class)) {
      return (A) new NotificationAccessor(new Database());
    }

    throw new RuntimeException("Create accessor for class " + clazz.getName());
  }
}
