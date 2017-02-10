package cm.aptoide.pt.v8engine.util;

import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;

/**
 * This Proxy class was created to solve the issue with calling Analytics tracking events inside
 * StoreUtils, which is in the dataprovider module. If you want
 * an event to be tracked (User subscribes a store) use this class's subscribeStore method. Else,
 * just keep doing whatever you are doing.
 *
 * Created by jdandrade on 02/08/16.
 */
public class StoreUtilsProxy {

  private static final AptoideClientUUID aptoideClientUUID;

  static {
    aptoideClientUUID = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext());
  }

  public static void subscribeStore(String storeName, AptoideAccountManager accountManager) {
    subscribeStore(GetStoreMetaRequest.of(StoreUtils.getStoreCredentials(storeName),
        accountManager.getAccessToken(), aptoideClientUUID.getAptoideClientUUID()), null,
        null, storeName, accountManager);
  }

  public static void subscribeStore(GetStoreMetaRequest getStoreMetaRequest,
      @Nullable SuccessRequestListener<GetStoreMeta> successRequestListener,
      @Nullable ErrorRequestListener errorRequestListener, String storeName,
      AptoideAccountManager accountManager) {
    Logger.d(StoreUtilsProxy.class.getName(),
        "LOCALYTICS TESTING - STORES: ACTION SUBSCRIBE " + storeName);
    Analytics.Stores.subscribe(storeName);
    StoreUtils.subscribeStore(getStoreMetaRequest, successRequestListener, errorRequestListener,
        accountManager);
  }

  public static void subscribeStore(String storeName,
      @Nullable SuccessRequestListener<GetStoreMeta> successRequestListener,
      @Nullable ErrorRequestListener errorRequestListener,
      AptoideAccountManager accountManager) {
    subscribeStore(GetStoreMetaRequest.of(StoreUtils.getStoreCredentials(storeName),
        accountManager.getAccessToken(), aptoideClientUUID.getAptoideClientUUID()),
        successRequestListener, errorRequestListener, storeName,
        accountManager);
  }
}
