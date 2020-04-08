/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.repository;

import android.content.Context;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;

/**
 * Created on 02/09/16.
 */
public final class RepositoryFactory {

  public static cm.aptoide.pt.repository.StoreRepository getStoreRepository(Context context) {
    return new cm.aptoide.pt.repository.StoreRepository(AccessorFactory.getAccessorFor(
        ((AptoideApplication) context.getApplicationContext()).getDatabase(), Store.class));
  }
}
