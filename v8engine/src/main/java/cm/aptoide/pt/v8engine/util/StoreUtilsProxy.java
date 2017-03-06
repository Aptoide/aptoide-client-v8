package cm.aptoide.pt.v8engine.util;

import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
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

  private final AptoideAccountManager accountManager;
  private final AptoideClientUUID aptoideClientUUID;

  public StoreUtilsProxy(AptoideClientUUID aptoideClientUuid, AptoideAccountManager accountManager) {
    this.aptoideClientUUID = aptoideClientUuid;
    this.accountManager = accountManager;
  }

  public void subscribeStore(String storeName) {
    subscribeStore(GetStoreMetaRequest.of(StoreUtils.getStoreCredentials(storeName),
        accountManager.getAccessToken(), aptoideClientUUID.getUniqueIdentifier()), null,
        null, storeName, accountManager);
  }

  public void subscribeStore(GetStoreMetaRequest getStoreMetaRequest,
      @Nullable SuccessRequestListener<GetStoreMeta> successRequestListener,
      @Nullable ErrorRequestListener errorRequestListener, String storeName,
      AptoideAccountManager accountManager) {
    subscribeStore(getStoreMetaRequest, successRequestListener, errorRequestListener, storeName,
        accountManager, null, null);
  }

  public void subscribeStore(GetStoreMetaRequest getStoreMetaRequest,
      @Nullable SuccessRequestListener<GetStoreMeta> successRequestListener,
      @Nullable ErrorRequestListener errorRequestListener, String storeName,
      AptoideAccountManager accountManager, String storeUserName, String storePassword) {
    Analytics.Stores.subscribe(storeName);
    StoreUtils.subscribeStore(getStoreMetaRequest, successRequestListener, errorRequestListener,
        accountManager, storeUserName, storePassword);
  }

  public void subscribeStore(String storeName,
      @Nullable SuccessRequestListener<GetStoreMeta> successRequestListener,
      @Nullable ErrorRequestListener errorRequestListener,
      AptoideAccountManager accountManager) {
    subscribeStore(GetStoreMetaRequest.of(StoreUtils.getStoreCredentials(storeName),
        accountManager.getAccessToken(), aptoideClientUUID.getUniqueIdentifier()),
        successRequestListener, errorRequestListener, storeName, accountManager);
  }

  public void unSubscribeStore(String storeName) {
    Analytics.Stores.unSubscribe(storeName);
    StoreUtils.unsubscribeStore(storeName, accountManager);
  }
}
