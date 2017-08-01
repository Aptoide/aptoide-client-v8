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
import java.util.concurrent.Executors;
import rx.Scheduler;
import rx.schedulers.Schedulers;

// TODO: 13/1/2017 this should not be public

/**
 * Instead of getting an accessor from here, use a Repository
 */
public final class AccessorFactory {

  // FIXME replace for Schedulers.io()
  // if issue regarding io thread blowing up application
  // with out-of-memory exception is properly fixed
  private static Scheduler observingScheduler;
  static {
    int nrIoCores = Runtime.getRuntime()
        .availableProcessors() * 5;
    observingScheduler = Schedulers.from(Executors.newFixedThreadPool(nrIoCores));
  }

  @NonNull
  public static <T extends RealmObject, A extends Accessor> A getAccessorFor(Class<T> clazz) {
    if (clazz.equals(Scheduled.class)) {
      return (A) new ScheduledAccessor(new Database(), observingScheduler);
    } else if (clazz.equals(PaymentConfirmation.class)) {
      return (A) new TransactionAccessor(new Database(), observingScheduler);
    } else if (clazz.equals(Installed.class)) {
      return (A) new InstalledAccessor(new Database(), observingScheduler);
    } else if (clazz.equals(Download.class)) {
      return (A) new DownloadAccessor(new Database(), observingScheduler);
    } else if (clazz.equals(Update.class)) {
      return (A) new UpdateAccessor(new Database(), observingScheduler);
    } else if (clazz.equals(Rollback.class)) {
      return (A) new RollbackAccessor(new Database(), observingScheduler);
    } else if (clazz.equals(Store.class)) {
      return (A) new StoreAccessor(new Database(), observingScheduler);
    } else if (clazz.equals(StoredMinimalAd.class)) {
      return (A) new StoredMinimalAdAccessor(new Database(), observingScheduler);
    } else if (clazz.equals(PaymentAuthorization.class)) {
      return (A) new PaymentAuthorizationAccessor(new Database(), observingScheduler);
    } else if (clazz.equals(Notification.class)) {
      return (A) new NotificationAccessor(new Database(), observingScheduler);
    }

    throw new RuntimeException("Create accessor for class " + clazz.getName());
  }
}
