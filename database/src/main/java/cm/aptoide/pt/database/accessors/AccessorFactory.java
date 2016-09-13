/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.PaymentConfirmation;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.database.realm.Update;
import io.realm.RealmObject;

/**
 * Created by sithengineer on 02/09/16.
 */
public final class AccessorFactory {

  public static <T extends RealmObject, A extends Accessor> A getAccessorFor(Class<T> clazz) {
    if (clazz.equals(Scheduled.class)) {
      return (A) new ScheduledAccessor(new Database());
    } else if (clazz.equals(PaymentConfirmation.class)) {
      return (A) new PaymentAccessor(new Database());
    } else if (clazz.equals(Installed.class)) {
      return (A) new InstalledAccessor(new Database());
    } else if (clazz.equals(Download.class)) {
      return (A) new DownloadAccessor(new Database());
    } else if (clazz.equals(Update.class)) {
      return (A) new UpdatesAccessor(new Database());
    } else if (clazz.equals(Rollback.class)) {
      return (A) new RollbackAccessor(new Database());
    }

    // TODO: 02/09/16 add missing cases

    return null;
  }
}
