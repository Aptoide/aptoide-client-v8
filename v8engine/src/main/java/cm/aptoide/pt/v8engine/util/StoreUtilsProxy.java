package cm.aptoide.pt.v8engine.util;

import android.support.annotation.Nullable;

import cm.aptoide.pt.dataprovider.ws.v7.listapps.StoreUtils;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.v8engine.analytics.Analytics;

/**
 * This Proxy class was created to solve the issue with calling Analytics tracking events inside StoreUtils, which is in the dataprovider module. If you want
 * an event to be tracked (User subscribes a store) use this class's subscribeStore method. Else, just keep doing whatever you are doing.
 *
 * Created by jdandrade on 02/08/16.
 */
public class StoreUtilsProxy {

    public static void subscribeStore(String storeName) {
        subscribeStore(GetStoreMetaRequest.of(storeName), null, null, storeName);
    }

    public static void subscribeStore(String storeName, @Nullable SuccessRequestListener<GetStoreMeta> successRequestListener, @Nullable ErrorRequestListener
            errorRequestListener) {
        subscribeStore(GetStoreMetaRequest.of(storeName), successRequestListener, errorRequestListener, storeName);
    }

    public static void subscribeStore(GetStoreMetaRequest getStoreMetaRequest, @Nullable
    SuccessRequestListener<GetStoreMeta> successRequestListener, @Nullable ErrorRequestListener errorRequestListener, String storeName) {
        Logger.d(StoreUtilsProxy.class.getName(), "LOCALYTICS TESTING - STORES: ACTION SUBSCRIBE " + storeName);
        Analytics.Stores.subscribe(storeName);
        StoreUtils.subscribeStore(getStoreMetaRequest, successRequestListener, errorRequestListener);
    }
}
