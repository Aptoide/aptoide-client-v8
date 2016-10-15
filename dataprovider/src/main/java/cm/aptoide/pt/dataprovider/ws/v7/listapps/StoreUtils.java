/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.utils.CrashReports;
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

  public static List<Long> getSubscribedStoresIds() {

    List<Long> storesNames = new LinkedList<>();
    @Cleanup Realm realm = DeprecatedDatabase.get();
    RealmResults<cm.aptoide.pt.database.realm.Store> stores =
        DeprecatedDatabase.StoreQ.getAll(realm);
    for (cm.aptoide.pt.database.realm.Store store : stores) {
      storesNames.add(store.getStoreId());
    }

    return storesNames;
  }

  public static HashMapNotNull<String, List<String>> getSubscribedStoresAuthMap() {
    @Cleanup Realm realm = DeprecatedDatabase.get();
    HashMapNotNull<String, List<String>> storesAuthMap = new HashMapNotNull<>();
    RealmResults<cm.aptoide.pt.database.realm.Store> stores =
        DeprecatedDatabase.StoreQ.getAll(realm);
    for (cm.aptoide.pt.database.realm.Store store : stores) {
      if (store.getPasswordSha1() != null) {
        storesAuthMap.put(store.getStoreName(),
            new LinkedList<>(Arrays.asList(store.getUsername(), store.getPasswordSha1())));
      }
    }
    return storesAuthMap.size() > 0 ? storesAuthMap : null;
  }

  /**
   * If you want to do event tracking (Analytics) use (v8engine)StoreUtilsProxy.subscribeStore
   * instead, else, use this.
   */
  @Deprecated public static void subscribeStore(GetStoreMetaRequest getStoreMetaRequest,
      @Nullable SuccessRequestListener<GetStoreMeta> successRequestListener,
      @Nullable ErrorRequestListener errorRequestListener) {
    getStoreMetaRequest.execute(getStoreMeta -> {

      if (BaseV7Response.Info.Status.OK.equals(getStoreMeta.getInfo().getStatus())) {

        @Cleanup Realm realm = DeprecatedDatabase.get();

        cm.aptoide.pt.database.realm.Store store = new cm.aptoide.pt.database.realm.Store();

        Store storeData = getStoreMeta.getData();
        store.setStoreId(storeData.getId());
        store.setStoreName(storeData.getName());
        store.setDownloads(storeData.getStats().getDownloads());

        store.setIconPath(storeData.getAvatar());
        store.setTheme(storeData.getAppearance().getTheme());

        if (isPrivateCredentialsSet(getStoreMetaRequest)) {
          store.setUsername(getStoreMetaRequest.getBody().getStoreUser());
          store.setPasswordSha1(getStoreMetaRequest.getBody().getStorePassSha1());
        }

        // TODO: 18-05-2016 neuro private ainda na ta
        if (AptoideAccountManager.isLoggedIn()) {
          AptoideAccountManager.subscribeStore(storeData.getName());
        }

        DeprecatedDatabase.save(store, realm);

        if (successRequestListener != null) {
          successRequestListener.call(getStoreMeta);
        }
      }
    }, (e) -> {
      if (errorRequestListener != null) {
        errorRequestListener.onError(e);
      }
      CrashReports.logException(e);
    });
  }

  private static boolean isPrivateCredentialsSet(GetStoreMetaRequest getStoreMetaRequest) {
    return getStoreMetaRequest.getBody().getStoreUser() != null
        && getStoreMetaRequest.getBody().getStorePassSha1() != null;
  }
}
