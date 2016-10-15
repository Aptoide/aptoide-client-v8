/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import lombok.Cleanup;

/**
 * Created by neuro on 11-05-2016.
 */
public class StoreUtils {

  public static final String PRIVATE_STORE_ERROR = "STORE-3";
  public static final String PRIVATE_STORE_WRONG_CREDENTIALS = "STORE-4";

  public static HashMapNotNull<String, List<String>> getSubscribedStoresAuthMap() {
    @Cleanup Realm realm = DeprecatedDatabase.get();
    HashMapNotNull<String, List<String>> storesAuthMap = new HashMapNotNull<>();
    RealmResults<Store> stores =
        DeprecatedDatabase.StoreQ.getAll(realm);
    for (Store store : stores) {
      if (store.getPasswordSha1() != null) {
        storesAuthMap.put(store.getStoreName(),
            new LinkedList<>(Arrays.asList(store.getUsername(), store.getPasswordSha1())));
      }
    }
    return storesAuthMap.size() > 0 ? storesAuthMap : null;
  }
}
