package cm.aptoide.pt.v8engine.util;

import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import rx.Completable;
import rx.Observable;

/**
 * This Proxy class was created to solve the issue with calling Analytics tracking events inside
 * StoreUtils, which is in the dataprovider module. If you want
 * an event to be tracked (User subscribes a store) use this class's subscribeStore method. Else,
 * just keep doing whatever you are doing.
 *
 * Created by jdandrade on 02/08/16.
 */
public class StoreUtilsProxy {

  private final StoreAccessor storeAccessor;
  private final AptoideAccountManager accountManager;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final StoreCredentialsProvider storeCredentialsProvider;

  public StoreUtilsProxy(AptoideAccountManager accountManager,
      BodyInterceptor<BaseBody> bodyInterceptor,
      StoreCredentialsProvider storeCredentialsProvider, StoreAccessor storeAccessor) {
    this.accountManager = accountManager;
    this.bodyInterceptor = bodyInterceptor;
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.storeAccessor = storeAccessor;
  }

  public void subscribeStore(String storeName) {
    subscribeStore(
        GetStoreMetaRequest.of(StoreUtils.getStoreCredentials(storeName, storeCredentialsProvider),
            bodyInterceptor), null, null, storeName, accountManager);
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
      @Nullable ErrorRequestListener errorRequestListener, AptoideAccountManager accountManager) {

    subscribeStore(
        GetStoreMetaRequest.of(StoreUtils.getStoreCredentials(storeName, storeCredentialsProvider),
            bodyInterceptor), successRequestListener, errorRequestListener, storeName,
        accountManager);
  }

  public void unSubscribeStore(String storeName,
      StoreCredentialsProvider storeCredentialsProvider) {
    Analytics.Stores.unSubscribe(storeName);
    StoreUtils.unSubscribeStore(storeName, accountManager, storeCredentialsProvider);
  }

  public Completable addDefaultStore(GetStoreMetaRequest getStoreMetaRequest,
      AptoideAccountManager accountManager,
      BaseRequestWithStore.StoreCredentials storeCredentials) {

    return getStoreMetaRequest.observe()
        .flatMap(getStoreMeta -> {
          if (BaseV7Response.Info.Status.OK.equals(getStoreMeta.getInfo().getStatus())) {
            if (accountManager.isLoggedIn()) {
              return accountManager.subscribeStore(getStoreMeta.getData().getName(),
                  storeCredentials.getUsername(), storeCredentials.getPasswordSha1())
                  .andThen(Observable.just(getStoreMeta));
            } else {
              return Observable.just(getStoreMeta);
            }
          } else {
            return Observable.error(new Exception("Something went wrong while getting store meta"));
          }
        })
        .doOnNext(
            getStoreMeta -> saveStore(getStoreMeta.getData(), getStoreMetaRequest, storeAccessor))
        .doOnError((throwable) -> CrashReport.getInstance().log(throwable))
        .toCompletable();
  }

  private void saveStore(cm.aptoide.pt.model.v7.store.Store storeData,
      GetStoreMetaRequest getStoreMetaRequest, StoreAccessor storeAccessor) {
    Store store = new Store();

    store.setStoreId(storeData.getId());
    store.setStoreName(storeData.getName());
    store.setDownloads(storeData.getStats().getDownloads());

    store.setIconPath(storeData.getAvatar());
    store.setTheme(storeData.getAppearance().getTheme());

    if (isPrivateCredentialsSet(getStoreMetaRequest)) {
      store.setUsername(getStoreMetaRequest.getBody().getStoreUser());
      store.setPasswordSha1(getStoreMetaRequest.getBody().getStorePassSha1());
    }
    storeAccessor.save(store);
  }

  private boolean isPrivateCredentialsSet(GetStoreMetaRequest getStoreMetaRequest) {
    return getStoreMetaRequest.getBody().getStoreUser() != null
        && getStoreMetaRequest.getBody().getStorePassSha1() != null;
  }
}
