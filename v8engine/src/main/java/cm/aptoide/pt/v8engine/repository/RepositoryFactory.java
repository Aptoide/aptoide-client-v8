/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.database.realm.Update;
import io.realm.RealmObject;

/**
 * Created by sithengineer on 02/09/16.
 */
public final class RepositoryFactory {

  public static <T extends RealmObject, A extends Repository> A getRepositoryFor(Class<T> clazz) {
    if (clazz.equals(Scheduled.class)) {
      return (A) new ScheduledDownloadRepository(AccessorFactory.getAccessorFor(Scheduled.class));
    } else if (clazz.equals(Rollback.class)) {
      return (A) new RollbackRepository(AccessorFactory.getAccessorFor(Rollback.class));
    } else if (clazz.equals(Update.class)) {
      return (A) new UpdateRepository(AccessorFactory.getAccessorFor(Update.class));
    } else if (clazz.equals(Installed.class)) {
      return (A) new InstalledRepository(AccessorFactory.getAccessorFor(Installed.class));
    }

    // TODO: 02/09/16 add missing cases

    return null;
  }
}
